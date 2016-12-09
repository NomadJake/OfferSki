package com.hitch.nomad.hitchbeacon;
//Developer : nomad
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import android.annotation.TargetApi;
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
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
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

import static com.hitch.nomad.hitchbeacon.Constants.BLE.SCAN_PERIOD;
import static com.hitch.nomad.hitchbeacon.Hitchbeacon.context;
import static com.hitch.nomad.hitchbeacon.Hitchbeacon.user;
@TargetApi(21)
public class advertise extends Service {


    private static String mBluetoothDeviceAddress = null;
    private ArrayList<BluetoothDevice> mDevices= new ArrayList<BluetoothDevice>();
    ArrayList<String> savedAddressArrayList;
    private BluetoothGatt mConnectedGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothLeScanner mLEScanner;
    private List<ScanFilter> filters;


    private static int mConnectionState;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int REQUEST_ENABLE_BT = 0;
    private Handler mHandler;
    public ArrayList<BluetoothDevice> scanArrayList;
    public HashMap<String,String>uriMapping;
    public String[] hitchIds = {"74:DA:EA:B2:ED:64","74:DA:EA:B2:5B:EC","CC:08:7D:D1:4A:94"};
    public int [] rssiValues = {100,100,100};
    public String[] urls = {};
    Map<String,Double>scannedDevices;
    private ScanSettings settings;
    public String TAG = "advertise";
    public TrackThread scanThread;
    public SayHello couponThread;
    private int i;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Notification stateHolderNotification;
    private String oldHitchId = "rosieNips";

    List<Offer> offers = new ArrayList<>();
    List<Note> coupons = new ArrayList<>();
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;


    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    private boolean alive = true;

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
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        Log.d(TAG,"Started service , advertising");
        uriMapping = new HashMap<>();
        scannedDevices = new HashMap<String, Double>();
        alive = true;
        super.onCreate();
        auth = FirebaseAuth.getInstance();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<ScanFilter>();
        mHandler = new Handler();

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
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    if (Build.VERSION.SDK_INT >= 21) {
                        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                        settings = new ScanSettings.Builder()
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                .build();
                        filters = new ArrayList<ScanFilter>();
                    }
                }
                scanThread = new TrackThread();
                couponThread = new SayHello();
                couponThread.start();
                scanThread.start();
                scanArrayList = new ArrayList<>();
            }
        }
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


    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            BluetoothDevice device = result.getDevice();
            if (device.getName()!=null) {
                if(device.getName().equalsIgnoreCase("Hitch tag"))
                {
                    scannedDevices.put(device.getAddress(),(double)-result.getRssi());
                }
            }
//            connectToDevice(btDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    if (device.getName()!=null) {
                        if(device.getName().equalsIgnoreCase("Hitch tag"))
                        {
                            scannedDevices.put(device.getAddress(),(double)-rssi);
                        }
                    }
                }
            };


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(mScanCallback);

                    }
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
            }
        }
    }



    public void foundHitch(String hitchId){
        for (Map.Entry<String, Offer> entry : Hitchbeacon.offerLinkedHashMap.entrySet()) {
            String key = entry.getKey();
            Offer offer = entry.getValue();
            String hid = offer.getHitchId();
            Log.d(hid,"hid");
            if(hid.equals(hitchId)){
                if(!user.discoveredOffers.contains(offer.getOffer())){
                    Log.d("offerfound","hitch found ... notifying user");
                    notifyUser(offer.title,offer.getOffer(),offer.getLogoURI());
                    try {
//                        offer.setDiscovered(true);
//                        Hitchbeacon.offerLinkedHashMap.put(key,offer);
                        user.discoveredOffers.add(offer.getOffer());
                        mDatabase.child("users").child(user.email).setValue(user);
                        Log.d("push","push");
//                    mDatabase.child("offers").child(key).child("discovered").setValue(true);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("offers"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public void notifyUser(String title,String description,String image){
        Intent doneIntent = new Intent(this,DetailedActivity.class);
        doneIntent.setAction("done");
//        doneIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        doneIntent.putExtra("title",title);
        doneIntent.putExtra("note",description);
        doneIntent.putExtra("URL",image);
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

        builder.setContentTitle(title);
        builder.setContentText(description);
        builder.setSmallIcon(R.drawable.ic_add_24dp);
        builder.setLargeIcon(Bitmap.createScaledBitmap(icon, 200, 200, false));
//        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setContentIntent(pendingDoneIntent);
//        builder.setOngoing(false);
//        builder.setAutoCancel(false);
        stateHolderNotification = builder.build();


        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
        r.play();
        startForeground(131,
                stateHolderNotification);
    }

    public class TrackThread extends Thread {

        @Override
        public void run() {
            super.run();
            if(Hitchbeacon.user==null){
                stopSelf();
            }
            while (alive) {
                Log.d(TAG,"Tracking thread running...");
                scanLeDevice(true);
//                startScan();
                try {
                    Thread.currentThread().sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mLEScanner.stopScan(mScanCallback);
//                stopScan();
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Map.Entry<String, Double> min = null;
                for (Map.Entry<String, Double> entry : scannedDevices.entrySet()) {
                    if (min == null || min.getValue() > entry.getValue()) {
                        min = entry;
                    }
                }
                if (min != null) {
                    Log.d("foundHitch","foundHitch");
                    foundHitch(min.getKey());
                }
                scannedDevices.clear();
            }
        }
    }

    class SayHello extends Thread {
        public void run() {
            super.run();
            while (Hitchbeacon.user!=null) {
                Log.d("SayHello", "said hello");
                coupons = new ArrayList<>(Hitchbeacon.noteLinkedHashMap.values());
                Random r = new Random();
                int Low = 0;
                int High = coupons.size();
                Note testNote = null;
                try {
                    int Result = r.nextInt(High-Low) + Low;
                    Log.d("Random: ",Integer.toString(Result));
                    testNote = coupons.get(Result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (testNote!=null) {
                    if (!user.discoveredNotes.contains(testNote.getNote())) {
                        notifyUser(testNote.title, testNote.note, testNote.logoURI);
    //                    testNote.discovered = true;
                        user.discoveredNotes.add(testNote.getNote());
                        mDatabase.child("users").child(user.email).setValue(user);
    //                                mDatabase.child("notes").child((String) pair.getKey()).child("discovered").setValue(true);
    //                    Log.d("HMKey", (String) getKeyFromValue(Hitchbeacon.noteLinkedHashMap,testNote));
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("notes"));
                    }
                }
                try {
                    Thread.currentThread().sleep(1 * 60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public Object getKeyFromValue(Map hm, Object value) {
            for (Object o : hm.keySet()) {
                if (hm.get(o).equals(value)) {
                    return o;
                }
            }
            return null;
        }
    }

    // And From your main() method or any other method


}


