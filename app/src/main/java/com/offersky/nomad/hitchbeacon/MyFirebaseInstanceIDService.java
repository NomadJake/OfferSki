package com.offersky.nomad.hitchbeacon;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by sankarmanoj on 30/05/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIIDService";
    private DatabaseReference mDatabase;


    @Override
    public void onTokenRefresh() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        try {
            this.mDatabase.child("users").child(Hitchbeacon.user.fcm).setValue(refreshedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
