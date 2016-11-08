package com.hitch.nomad.hitchbeacon;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.UUID;

public class AddOfferActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton fab;

    EditText etTitle, etTag;
    public CheckBox checkBox;

    private DatabaseReference mDatabase;
    private String mUserId;
    String title, offer;
    long time;

    boolean editingOffer;
    private EditText etOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

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

        checkBox = (CheckBox)findViewById(R.id.discovered_checkBox);
        etTitle = (EditText) findViewById(R.id.addnote_title);
        etTag = (EditText) findViewById(R.id.hitchid);
        etOffer = (EditText)findViewById(R.id.offer);
        fab = (FloatingActionButton) findViewById(R.id.addnote_fab);


        //  handle intent

//        editingOffer = getIntent() != null;
        editingOffer = getIntent().getBooleanExtra("isEditing", false);
        if (editingOffer) {
            title = getIntent().getStringExtra("offer_title");
            offer = getIntent().getStringExtra("offer");
            time = getIntent().getLongExtra("offer_time", 0);

            etTitle.setText(title);
            etTag.setText(offer);

        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Add offer to DB

                String newTitle = etTitle.getText().toString();
                String newTag = etTag.getText().toString();
                String newOffer = etOffer.getText().toString();
                String isDiscovered = "false";
                if(checkBox.isChecked()){
                    isDiscovered = "true";
                }
                String uid = UUID.randomUUID().toString();

                Offer offer = new Offer(newTitle,newOffer,isDiscovered,newTag,uid);
                mDatabase.child("offers").child(offer.getUid()).setValue(offer);
                finish();


            }
        });


    }
}
