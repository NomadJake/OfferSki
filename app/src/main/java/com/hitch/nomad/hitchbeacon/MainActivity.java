package com.hitch.nomad.hitchbeacon;
//Developer : nomad
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab;

    OffersAdapter adapter;
    List<Offer> offers = new ArrayList<>();

    long initialCount;

    int modifyPos = -1;
    private DatabaseReference mDatabase;

    public Button buttonForOffers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Main", "onCreate");
        recyclerView = (RecyclerView) findViewById(R.id.main_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
//        buttonForOffers = (Button)findViewById(R.id.toggle_offers_button);
//        buttonForOffers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,CouponsActivity.class));
//            }
//        });
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(gridLayoutManager);
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        offers = new ArrayList<>(Hitchbeacon.offerLinkedHashMap.values());
        initialCount = 0;
        if (savedInstanceState != null)
            modifyPos = savedInstanceState.getInt("modify");
        if (initialCount >= 0) {

//            offers = Offer.findWithQuery(Offer.class, "Select * from Offer where discovered = ?", "true");//Offer.listAll(Offer.class);
            try {
                offers = new ArrayList<>(Hitchbeacon.offerLinkedHashMap.values());
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter = new OffersAdapter(MainActivity.this, offers);
            recyclerView.setAdapter(adapter);

            if (offers.isEmpty())
                Snackbar.make(recyclerView, "No offers added.", Snackbar.LENGTH_LONG).show();

        }

        // tinting FAB icon
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_add_24dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.WHITE);
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
            fab.setImageDrawable(drawable);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, AddOfferActivity.class);
                startActivity(i);
            }
        });
        // Handling swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //Remove swiped item from list and notify the RecyclerView

                final int position = viewHolder.getAdapterPosition();
                final Offer offer = offers.get(viewHolder.getAdapterPosition());
                offers.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(position);

//                offer.delete();
                initialCount -= 1;
                mDatabase.child("offers").child(offer.title).setValue(null);
                Snackbar.make(recyclerView, "Offer deleted", Snackbar.LENGTH_SHORT)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                    // Remove this sugar orm shit and port everything to firebase.
//                                offer.save();
                                offers.add(position, offer);
                                adapter.notifyItemInserted(position);
                                initialCount += 1;
                            }
                        })
                        .show();
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        try {
            adapter.SetOnItemClickListener(new OffersAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    Log.d("Main", "click");

                    Intent i = new Intent(MainActivity.this, AddOfferActivity.class);
                    i.putExtra("isEditing", true);
                    i.putExtra("offer_title", offers.get(position).title);
                    i.putExtra("offer", offers.get(position).Offer);


                    modifyPos = position;

                    startActivity(i);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


//        List<Offer> offers = Offer.findWithQuery(Offer.class, "Select title from Offer where title = ?", "%offer%");
//        if (offers.size() > 0)
//            Log.d("Offers", "offer: " + offers.get(0).title);

        Intent fcmrefresh = new Intent(this, MyFirebaseInstanceIDService.class);
        startService(fcmrefresh);
        if(!isMyServiceRunning(advertise.class)){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent serviceIntetnt = new Intent(MainActivity.this,advertise.class);
                    serviceIntetnt.setAction("track");
//                    startService(serviceIntetnt);
                }
            }, 10000);

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.showCoupons)
        {
            startActivity(new Intent(this,CouponsActivity.class));

        }

        if (id == R.id.allOffers){
            startActivity(new Intent(this,MainActivity.class));

        }
        if (id == R.id.allDeals){
            startActivity(new Intent(this,DealsActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("modify", modifyPos);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        modifyPos = savedInstanceState.getInt("modify");
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        final long newCount = offers.size();
//
//        if (newCount > initialCount) {
//            // A offer is added
//            Log.d("Main", "Adding new offer");
//
//            // Just load the last added offer (new)
//            Offer offer = Offer.last(Offer.class);
//
//            if (offer.getDiscovered().equals("true")) {
//                offers.add(offer);
//                adapter.notifyItemInserted((int) newCount);
//
//                initialCount = newCount;
//            }
//        }
//
//        if (modifyPos != -1) {
//            offers.set(modifyPos, Offer.listAll(Offer.class).get(modifyPos));
//            adapter.notifyItemChanged(modifyPos);
//        }
//
//    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateFormat(long date) {
        return new SimpleDateFormat("dd MMM yyyy").format(new Date(date));
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
