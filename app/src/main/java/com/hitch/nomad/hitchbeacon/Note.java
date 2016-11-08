package com.hitch.nomad.hitchbeacon;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;

import java.util.UUID;

/**
 * Created by nomad on 1/21/16.
 */
@IgnoreExtraProperties
public class Note {
    String uid;
    String title;
    String note;

    public Note() {
    }


    public Note(String title, String note) {
        this.title = title;
        this.note = note;
        this.uid  = UUID.randomUUID().toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
