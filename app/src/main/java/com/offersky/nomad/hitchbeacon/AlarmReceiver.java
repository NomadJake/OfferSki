package com.offersky.nomad.hitchbeacon;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    Context contextOne;
    @Override
    public void onReceive(Context context, Intent intent) {
        contextOne = context;
        // For our recurring task, we'll just display a message
//        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        if (!isMyServiceRunning(advertise.class)) {
            Intent serviceIntetnt = new Intent(context,advertise.class);
            serviceIntetnt.setAction("track");
            context.startService(serviceIntetnt);
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) contextOne.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}