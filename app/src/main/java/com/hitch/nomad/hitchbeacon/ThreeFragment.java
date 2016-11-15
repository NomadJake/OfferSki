package com.hitch.nomad.hitchbeacon;

import android.annotation.SuppressLint;
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


public class ThreeFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton fab;

    CouponAdapter adapter;
    List<Note> deals = new ArrayList<>();

    long initialCount;

    int modifyPos = -1;
    private DatabaseReference mDatabase;

    public ThreeFragment() {
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
        View view = inflater.inflate(R.layout.activity_main,container,false);
        Log.d("Main", "onCreate");

        recyclerView = (RecyclerView) view.findViewById(R.id.list_coupons);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(gridLayoutManager);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        try {
            deals = new ArrayList<>(Hitchbeacon.noteLinkedHashMap.values());
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
                deals = new ArrayList<>(Hitchbeacon.noteLinkedHashMap.values());
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter = new CouponAdapter(getActivity(), deals);
            recyclerView.setAdapter(adapter);

            if (deals.isEmpty())
                Snackbar.make(recyclerView, "No deals added.", Snackbar.LENGTH_LONG).show();

        }

        // tinting FAB icon
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//
//            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_add_24dp);
//            drawable = DrawableCompat.wrap(drawable);
//            DrawableCompat.setTint(drawable, Color.WHITE);
//            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
//
//            fab.setImageDrawable(drawable);
//        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);




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
            final Note note = deals.get(viewHolder.getAdapterPosition());
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
