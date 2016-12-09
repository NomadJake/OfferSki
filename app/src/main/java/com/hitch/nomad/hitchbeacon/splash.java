package com.hitch.nomad.hitchbeacon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import static com.hitch.nomad.hitchbeacon.Hitchbeacon.context;
import static com.hitch.nomad.hitchbeacon.Hitchbeacon.getUser;

public class splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(this, IconTabsActivity.class);
        getUser();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean loggedin = sharedPreferences.getBoolean(Constants.SIGNEDIN,false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Intent intent1 = new Intent(this,OtpAuth.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Hitchbeacon.user == null){
                    startActivity(intent1);
                    finish();
                }else {
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);

    }
}