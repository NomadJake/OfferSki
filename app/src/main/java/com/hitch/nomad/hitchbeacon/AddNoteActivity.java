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

public class AddNoteActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton fab;

    EditText etTitle, etTag;

    private DatabaseReference mDatabase;
    private String mUserId;
    String title, note;
    long time;

    boolean editingNote;
    private EditText etDiscovered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        toolbar = (Toolbar) findViewById(R.id.addnote_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_24dp);

        getSupportActionBar().setTitle("Add new coupon");

        // Initialize Firebase Auth and Database Reference
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        etTitle = (EditText) findViewById(R.id.addnote_title);
        etTag = (EditText) findViewById(R.id.add_hitch_id);
        etDiscovered = (EditText)findViewById(R.id.et_found);
        fab = (FloatingActionButton) findViewById(R.id.addnote_fab);


        //  handle intent

//        editingNote = getIntent() != null;
        editingNote = getIntent().getBooleanExtra("isEditing", false);
        if (editingNote) {
            title = getIntent().getStringExtra("note_title");
            note = getIntent().getStringExtra("note");
            time = getIntent().getLongExtra("note_time", 0);

            etTitle.setText(title);
            etTag.setText(note);

        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Add note to DB

                String newTitle = etTitle.getText().toString();
                String newTag = etTag.getText().toString();
                String newDiscovered = etDiscovered.getText().toString();


                /**
                 * TODO: Check if note exists before saving
                 */
                if (!editingNote) {
                    Log.d("Note", "saving");
                    Note note = new Note(newTitle, newTag,newDiscovered);
                    note.save();
                } else {
                    Log.d("Note", "updating");

//                    List<Note> notes = Note.findWithQuery(Note.class, "where title = ?", title);
                    List<Note> notes = Note.find(Note.class, "title = ?", title);
                    if (notes.size() > 0) {

                        Note note = notes.get(0);
                        Log.d("got note", "note: " + note.title);
                        note.title = newTitle;
                        note.note = newTag;

                        note.save();
                        mDatabase.child("users").child(note.title).setValue(note);

                    }

                }

                finish();


            }
        });


    }
}
