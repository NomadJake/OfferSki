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
    EditText etTitle, etNote;

    private DatabaseReference mDatabase;
    String title, note;
    long time;
    boolean editingNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

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
        etNote = (EditText) findViewById(R.id.note);
        fab = (FloatingActionButton) findViewById(R.id.addnote_fab);

        editingNote = getIntent().getBooleanExtra("isEditing", false);
        if (editingNote) {
            title = getIntent().getStringExtra("note_title");
            note = getIntent().getStringExtra("note");
            time = getIntent().getLongExtra("note_time", 0);
            etTitle.setText(title);
            etNote.setText(note);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Add note to DB

                String newTitle = etTitle.getText().toString();
                String newNote = etNote.getText().toString();
                Note note = new Note(newTitle, newNote);
                mDatabase.child("notes").child(note.getUid()).setValue(note);
//                /**
//                 * TODO: Check if note exists before saving
//                 */
//                if (!editingNote) {
//                    Log.d("Note", "saving");
//                    Note note = new Note(newTitle, newNote);
//                    note.save();
//                } else {
//                    Log.d("Note", "updating");
//                    List<Note> notes = Note.find(Note.class, "title = ?", title);
//                    if (notes.size() > 0) {
//                        Note note = notes.get(0);
//                        Log.d("got note", "note: " + note.title);
//                        note.title = newTitle;
//                        note.note = newNote;
////                        note.save();
//                        mDatabase.child("notes").child(note.title).setValue(note);
//                    }
//
//                }
                finish();
            }
        });


    }
}
