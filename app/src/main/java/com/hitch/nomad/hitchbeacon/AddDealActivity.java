package com.hitch.nomad.hitchbeacon;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddDealActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton fab;
    EditText etTitle, etDeal;

    private DatabaseReference mDatabase;
    String title, deal;
    long time;
    boolean editingNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deal);

        toolbar = (Toolbar) findViewById(R.id.addnote_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_24dp);

        getSupportActionBar().setTitle("Add new coupon");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etTitle = (EditText) findViewById(R.id.note_title);
        etDeal = (EditText) findViewById(R.id.note);
        fab = (FloatingActionButton) findViewById(R.id.addnote_fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Add note to DB

                String newTitle = etTitle.getText().toString();
                String newNote = etDeal.getText().toString();
                Deals deal = new Deals(newTitle, newNote);
                mDatabase.child("deals").child(deal.title).setValue(deal);
                finish();
            }
        });


    }
}
