package com.offersky.nomad.hitchbeacon;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.List;


public class OneFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton fab;

    DealsAdapter adapter;
    List<Deals> deals = new ArrayList<>();

    long initialCount;

    int modifyPos = -1;
    private DatabaseReference mDatabase;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    public OneFragment() {
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
        View view = inflater.inflate(R.layout.activity_deals,container,false);
        mySwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        Log.d("Main", "onCreate");

        recyclerView = (RecyclerView) view.findViewById(R.id.list_deals);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(gridLayoutManager);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        try {
            deals = new ArrayList<>(Hitchbeacon.dealsLinkedHashMap.values());
            initialCount = deals.size();

        } catch (Exception e) {
            initialCount = 0;
            e.printStackTrace();
        }

        if (savedInstanceState != null)
            modifyPos = savedInstanceState.getInt("modify");


        if (initialCount >= 0) {

//            deals = Note.findWithQuery(Note.class, "Select * from Note where discovered = ?", "true");//Note.listAll(Note.class);
            try {
                deals = new ArrayList<>(Hitchbeacon.dealsLinkedHashMap.values());
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter = new DealsAdapter(getActivity(), deals);
            recyclerView.setAdapter(adapter);

            if (deals.isEmpty())
                Snackbar.make(recyclerView, "No deals added.", Snackbar.LENGTH_LONG).show();

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
            adapter.SetOnItemClickListener(new DealsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    Log.d("Main", "click");

                    Intent i = new Intent(getContext(), DetailedActivity.class);
                    i.putExtra("isEditing", true);
                    i.putExtra("note_title", deals.get(position).title);
                    i.putExtra("note", deals.get(position).deal);

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
                        List<Deals> allOffers = new ArrayList<>();
                        allOffers = new ArrayList<>(Hitchbeacon.dealsLinkedHashMap.values());
                        if(allOffers.size() != 0){
                            deals.clear();
                        }
                        for(Deals offer : allOffers){
                                deals.add(offer);

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

            final int position = viewHolder.getAdapterPosition();
            final Deals note = deals.get(viewHolder.getAdapterPosition());
            deals.remove(viewHolder.getAdapterPosition());
            adapter.notifyItemRemoved(position);
            initialCount -= 1;
            mDatabase.child("deals").child(note.getUid()).setValue(null);


            Snackbar.make(recyclerView, "Deals deleted", Snackbar.LENGTH_SHORT)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

//                                note.save();
                            deals.add(position, note);
                            adapter.notifyItemInserted(position);
                            initialCount += 1;

                        }
                    })
                    .show();
        }

    };



}
