package com.offersky.nomad.hitchbeacon;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    static Context context;
    private static DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private static Hitchbeacon mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mInstance = this;
        offerLinkedHashMap = new LinkedHashMap<>();
        noteLinkedHashMap = new LinkedHashMap<>();
        dealsLinkedHashMap = new LinkedHashMap<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        loggedin = sharedPreferences.getBoolean(Constants.SIGNEDIN,false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        try {
            getUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(loggedin){
            if(user==null)
                getUser();
//            setListners();
//            context.startActivity(new Intent(this,IconTabsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }else {
//            sharedPreferences.edit().putBoolean(Constants.SIGNEDIN,false).commit(); // might cause shit
//            context.startActivity(new Intent(this,OtpAuth.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }

    }

    public static void setLoggedin(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(Constants.SIGNEDIN,true).commit();
    }

    public static void setListners(){
        Query queryRef = mDatabase.child("offers").orderByChild("segment").equalTo(getSegment(user));
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                String title = (String) dataSnapshot.child("title").getValue();
//                String offer = (String) dataSnapshot.child("offer").getValue();
//                String hitchId = (String) dataSnapshot.child("hitchId").getValue();
//                String segment = (String) dataSnapshot.child("segment").getValue();
//                String uid = (String) dataSnapshot.child("logo").getValue();
//                Boolean discovered = (Boolean)dataSnapshot.child("discovered").getValue();
//                Offer offerInstance = new Offer(title,offer,discovered,hitchId,uid,segment);
                Offer offerInstance = dataSnapshot.getValue(Offer.class);
                offerLinkedHashMap.put(s,offerInstance);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("offers"));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                String title = (String) dataSnapshot.child("title").getValue();
//                String offer = (String) dataSnapshot.child("offer").getValue();
//                String hitchId = (String) dataSnapshot.child("hitchId").getValue();
//                String segment = (String) dataSnapshot.child("segment").getValue();
//                String uid = (String) dataSnapshot.child("logo").getValue();
//                Boolean discovered = (Boolean)dataSnapshot.child("discovered").getValue();
//                Offer offerInstance = new Offer(title,offer,discovered,hitchId,uid,segment);
                Log.d("Change","changed");
                Offer offerInstance = dataSnapshot.getValue(Offer.class);
                offerLinkedHashMap.put(s,offerInstance);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("offers"));

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                String title = (String) dataSnapshot.child("title").getValue();
                Offer offerInstance = dataSnapshot.getValue(Offer.class);
                offerLinkedHashMap.remove(offerInstance);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("offers"));

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query queryRefNotes = mDatabase.child("notes").orderByChild("segment").equalTo(getSegment(user));
        queryRefNotes.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                String title = (String) dataSnapshot.child("title").getValue();
//                String offer = (String) dataSnapshot.child("note").getValue();
//                Note noteInstance = new Note(title,offer);
                Note note = dataSnapshot.getValue(Note.class);
                noteLinkedHashMap.put(s,note);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("coupons"));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String title = (String) dataSnapshot.child("title").getValue();
                noteLinkedHashMap.remove(title);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("coupons"));

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getUser(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String email = sharedPreferences.getString("email",null);
        Query queryRefUser = mDatabase.child("users").orderByChild("email").equalTo(email);
        queryRefUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User userInstance = dataSnapshot.getValue(User.class);
                user = userInstance;
                setListners();
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("user"));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User userInstance = dataSnapshot.getValue(User.class);
                user = userInstance;
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static String getSegment(User user){
        String ageString = user.age;
        String sex = user.sex;
        String segment = "A";
        int age = Integer.parseInt(ageString);
        if (age<25 && sex.equals("m")){
            segment = "A";
        }else if(age >= 25 && sex.equals("f")){
            segment = "C";
        }else if(age >= 25 && sex.equals("m")){
            segment = "D";
        }else if(age < 25 && sex.equals("f")){
            segment = "B";
        }
        return segment;
    }

    public static final String TAG = Hitchbeacon.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;



    public static synchronized Hitchbeacon getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
