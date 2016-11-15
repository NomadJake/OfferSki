package com.hitch.nomad.hitchbeacon;
//Developer : nomad
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class advertise extends Service implements LeScanCallback {


    private static String mBluetoothDeviceAddress = null;
    private ArrayList<BluetoothDevice> mDevices= new ArrayList<BluetoothDevice>();
    ArrayList<String> savedAddressArrayList;
    private BluetoothGatt mConnectedGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    private static int mConnectionState;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final Handler mHandler = null;
    public ArrayList<BluetoothDevice> scanArrayList;
    public HashMap<String,String>uriMapping;
    public String[] hitchIds = {"74:DA:EA:B2:ED:64","74:DA:EA:B2:5B:EC","CC:08:7D:D1:4A:94"};
    public int [] rssiValues = {100,100,100};
    public String[] urls = {};
    Map<String,Double>scannedDevices;

    public String TAG = "advertise";
    public TrackThread scanThread;
    private int i;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Notification stateHolderNotification;
    private String oldHitchId = "rosieNips";

    List<Offer> offers = new ArrayList<>();
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;


    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    private boolean alive = true;


    public void startscanning (){
        mHandler.post(mStartRunnable);
        mHandler.postDelayed(mStopRunnable, 3000);
    }

    Handler mhandler = new Handler();
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        advertise getService() {
            // Return this instance of LocalService so clients can call public
            // methods
            return advertise.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onCreate() {
        mBluetoothManager=(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter= mBluetoothManager.getAdapter();
        Log.d(TAG,"Started service , advertising");
        uriMapping = new HashMap<>();
        scannedDevices = new HashMap<String, Double>();
        alive = true;
        uriMapping.clear();
        uriMapping.put("74:DA:EA:B2:ED:64","http://www.kotak.com/");
        uriMapping.put("74:DA:EA:B2:5B:EC","http://www.hdfcbank.com/");
        uriMapping.put("74:DA:EA:B1:43:64","http://www.rblbank.com/");
        uriMapping.put("1","Scanning...");
        super.onCreate();
        auth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void onRebind(Intent intent) {
        // TODO Auto-generated method stub
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"Started service , start command");
        if(intent != null && intent.getAction()!=null && intent.getAction().equals("done")){
            Bundle bundle = intent.getExtras();
            try {
                String url = uriMapping.get(bundle.getString("brand"));
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stopForeground(true);
        }else if(intent != null && intent.getAction()!=null && intent.getAction().equals("stop")){
            alive = false;
            Log.d(TAG,"Started stopped !!!!!! , start command");
//            mBluetoothAdapter.disable();
            stopForeground(true);
            stopSelf();
        }else {
            if (scanThread == null || !scanThread.isAlive()) {
                mBluetoothAdapter.enable();
                scanThread = new TrackThread();
                scanThread.start();
                scanArrayList = new ArrayList<>();
            }

        }
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                Offer gift = new Offer("Title","Offer","discovered","hitchuid");
//                gift.save();
//            }
//        }, 300000);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        alive = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }


    final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            // TODO Auto-generated method stub
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {

                Toast.makeText(getApplicationContext(), "foo bar", Toast.LENGTH_SHORT).show();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Toast.makeText(getApplicationContext(), "foo bar disconnected", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // TODO Auto-generated method stub
            super.onCharacteristicChanged(gatt, characteristic);
            Toast.makeText(getApplicationContext(), "char changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor descriptor, int status) {
            // TODO Auto-generated method stub
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            // TODO Auto-generated method stub
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            // TODO Auto-generated method stub
            super.onReadRemoteRssi(gatt, rssi, status);
        }


    };
    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    public void startScan() {
        mBluetoothAdapter.startLeScan(this);

    }

    public void stopScan() {
        mBluetoothAdapter.stopLeScan(this);

    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d(TAG,"---------------");
        Log.d(TAG, device.toString());
        Log.d(TAG,String.valueOf(rssi));
        Log.d(TAG,"++++++++++++++++");
        if (device.getName()!=null) {
            if(device.getName().equalsIgnoreCase("Hitch tag"))
            {
//                hitchIds[i] = device.getAddress();
//                rssiValues[i] = -rssi;
//                i += 1;
                scannedDevices.put(device.getAddress(),(double)-rssi);
            }
        }

    }
    public boolean connect(final String address) {
        String TAG="connecting situation";
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }


        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    public int findMinIdx(int[] numbers) {
        if (numbers == null || numbers.length == 0) return -1; // Saves time for empty array
        int minVal = numbers[0];// Keeps a running count of the smallest value so far
        int minIdx = 0; // Will store the index of minVal
        for(int idx=1; idx<numbers.length; idx++) {
            if(numbers[idx] < minVal) {
                minVal = numbers[idx];
                minIdx = idx;
            }
        }
        return minIdx;
    }

    public void foundHitch(String hitchId){
        offers = new ArrayList<>(Hitchbeacon.offerLinkedHashMap.values());//Offer.findWithQuery(Offer.class, "Select * from Offer where offer = ?", hitchId);
        if (offers.size() != 0) {
            for(Offer offer : offers){
                String hid = offer.getHitchId();
                Log.d(hid,"hid");
                if(!hid.equals(null)&&hid.equals(hitchId)&&!offer.getDiscovered().equals("true")){
                    notifyUser(offer);
                    try {
                        offer.setDiscovered("true");
                        Hitchbeacon.offerLinkedHashMap.put(offer.getTitle(),offer);
                        mDatabase.child("offers").child(offer.getUid()).child("discovered").setValue("true");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("offers"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void notifyUser(Offer found_offer){
        Intent doneIntent = new Intent(this,advertise.class);
        doneIntent.setAction("done");
//        doneIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        doneIntent.putExtra("brand",found_offer.getTitle());
        PendingIntent pendingDoneIntent = PendingIntent.getService(this, 0,
                doneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this,advertise.class);
        stopIntent.setAction("stop");
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0,
                stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.fronthitchlogo);

        builder = new NotificationCompat.Builder(getApplicationContext());

        builder.setContentTitle("You found a new coupon !");
        builder.setContentText(found_offer.getOffer());
        builder.setSmallIcon(R.drawable.ic_add_24dp);
        builder.setLargeIcon(Bitmap.createScaledBitmap(icon, 200, 200, false));
        builder.setOngoing(true);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setContentIntent(pendingDoneIntent);
        builder.setOngoing(true);
        builder.setAutoCancel(false);
//        builder.addAction(R.drawable.ic_close_black_24dp,"Stop",pendingStopIntent);
        stateHolderNotification = builder.build();

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
        r.play();

//        if (!oldHitchId.equals(hitchId) && alive ) {
//            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
//            r.play();
//        }
//        oldHitchId = hitchId;

        startForeground(101,
                stateHolderNotification);
    }

    public class TrackThread extends Thread {

        @Override
        public void run() {
            super.run();

            while (alive) {
                Log.d(TAG,"Tracking thread running...");
                startScan();
                try {
                    Thread.currentThread().sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopScan();
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                int nearestTagIndex = findMinIdx(rssiValues);
//                notifyUser(hitchIds[nearestTagIndex]);
                Map.Entry<String, Double> min = null;
                for (Map.Entry<String, Double> entry : scannedDevices.entrySet()) {
                    if (min == null || min.getValue() > entry.getValue()) {
                        min = entry;
                    }
                }
                if (min != null) {
                    foundHitch(min.getKey());
                }
                scannedDevices.clear();
            }
        }
    }

}


