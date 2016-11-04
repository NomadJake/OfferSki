package com.hitch.nomad.hitchbeacon;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;

/**
 * Created by nomad on 29/10/16.
 */

public class Hitchbeacon extends Application {
    public static boolean loggedin = false;
    public static User user;
    public static LinkedHashMap<String,Offer> offerLinkedHashMap;
    public static LinkedHashMap<String,Note> noteLinkedHashMap;
    public static LinkedHashMap<String,Deals> dealsLinkedHashMap;
    Context context;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        auth = FirebaseAuth.getInstance();
        offerLinkedHashMap = new LinkedHashMap<>();
        noteLinkedHashMap = new LinkedHashMap<>();
        dealsLinkedHashMap = new LinkedHashMap<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        loggedin = sharedPreferences.getBoolean(Constants.SIGNEDIN,false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("offers").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String title = (String) dataSnapshot.child("title").getValue();
                String offer = (String) dataSnapshot.child("offer").getValue();
                String hitchId = (String) dataSnapshot.child("hitchId").getValue();
                Offer offerInstance = new Offer(title,offer,"false",hitchId);
                offerLinkedHashMap.put(title,offerInstance);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("offers"));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String title = (String) dataSnapshot.child("title").getValue();
                offerLinkedHashMap.remove(title);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("offers"));


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("notes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String title = (String) dataSnapshot.child("title").getValue();
                String offer = (String) dataSnapshot.child("note").getValue();
                Note noteInstance = new Note(title,offer);
                noteLinkedHashMap.put(title,noteInstance);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("coupons"));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String title = (String) dataSnapshot.child("title").getValue();
                noteLinkedHashMap.remove(title);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("coupons"));


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("deals").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String title = (String) dataSnapshot.child("title").getValue();
                String offer = (String) dataSnapshot.child("deal").getValue();
                Deals noteInstance = new Deals(title,offer);
                dealsLinkedHashMap.put(title,noteInstance);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("coupons"));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String title = (String) dataSnapshot.child("title").getValue();
                dealsLinkedHashMap.remove(title);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("deals"));
//                offerLinkedHashMap.put(title,offerInstance);


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(loggedin){
            sharedPreferences.edit().putBoolean(Constants.SIGNEDIN,true).apply(); //might cause shit
            context.startActivity(new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }else {
            sharedPreferences.edit().putBoolean(Constants.SIGNEDIN,false).apply(); // might cause shit
            context.startActivity(new Intent(this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
    }
}
