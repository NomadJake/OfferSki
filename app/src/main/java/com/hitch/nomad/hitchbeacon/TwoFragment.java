package com.hitch.nomad.hitchbeacon;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TwoFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton fab;

    OffersAdapter adapter;
    List<Offer> offers = new ArrayList<>();

    long initialCount;

    int modifyPos = -1;
    private DatabaseReference mDatabase;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_one, container, false);
        View view = inflater.inflate(R.layout.activity_offers,container,false);
        mySwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        Log.d("Main", "onCreate");

        recyclerView = (RecyclerView) view.findViewById(R.id.list_offer);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(gridLayoutManager);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        try {
            offers = new ArrayList<>(Hitchbeacon.offerLinkedHashMap.values());
            initialCount = offers.size();

        } catch (Exception e) {
            initialCount = 0;
            e.printStackTrace();
        }

        if (savedInstanceState != null)
            modifyPos = savedInstanceState.getInt("modify");


        if (initialCount >= 0) {

//            offers = Note.findWithQuery(Note.class, "Select * from Note where discovered = ?", "true");//Note.listAll(Note.class);
            try {
                offers = new ArrayList<>(Hitchbeacon.offerLinkedHashMap.values());
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter = new OffersAdapter(getActivity(), offers);
            recyclerView.setAdapter(adapter);

            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                    new IntentFilter("offers"));
            if (offers.isEmpty())
                Snackbar.make(recyclerView, "No offers added.", Snackbar.LENGTH_LONG).show();

        }

        // tinting FAB icon
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_add_24dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.WHITE);
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);

            fab.setImageDrawable(drawable);
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        try {
            adapter.SetOnItemClickListener(new OffersAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    Log.d("Main", "click");
                    Intent i = new Intent(getContext(), DetailedActivity.class);
                    i.putExtra("isEditing", true);
                    i.putExtra("title", offers.get(position).title);
                    i.putExtra("note", offers.get(position).Offer);
                    i.putExtra("URL", offers.get(position).getUid());
                    i.putExtra("code"," ");

                    modifyPos = position;

                    startActivity(i);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("swipe", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        List<Offer> allOffers = new ArrayList<>();
                        allOffers = new ArrayList<>(Hitchbeacon.offerLinkedHashMap.values());
                        if(allOffers.size() != 0){
                            offers.clear();
                        }
                        for(Offer offer : allOffers){
                            if(offer.getDiscovered().equals(true)){
                                offers.add(offer);
                            }
                        }
                    }
                }
        );
        return view;
    }



    // Handling swipe to delete
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //Remove swiped item from list and notify the RecyclerView

//            final int position = viewHolder.getAdapterPosition();
//            final Offer note = offers.get(viewHolder.getAdapterPosition());
//            offers.remove(viewHolder.getAdapterPosition());
//            adapter.notifyItemRemoved(position);
//            initialCount -= 1;
//            mDatabase.child("offers").child(note.getUid()).setValue(null);
//
//
//            Snackbar.make(recyclerView, "Deals deleted", Snackbar.LENGTH_SHORT)
//                    .setAction("UNDO", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
////                                note.save();
//                            offers.add(position, note);
//                            adapter.notifyItemInserted(position);
//                            initialCount += 1;
//
//                        }
//                    })
//                    .show();
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            updateOffers();
            Log.d("receiver", "Got Broadcast for offers");
        }
    };

    public void updateOffers(){
        offers = new ArrayList<>(Hitchbeacon.offerLinkedHashMap.values());
//        try {
//            List<Offer> allOffers = new ArrayList<>();
//            allOffers = new ArrayList<>(Hitchbeacon.offerLinkedHashMap.values());
//            if(allOffers.size() != 0){
//                offers.clear();
//            }
//            for(Offer offer : allOffers){
//                if(Hitchbeacon.user.discoveredOffers.contains(offer)){
//                    offers.add(offer);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

}
