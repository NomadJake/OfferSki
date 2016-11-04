package com.hitch.nomad.hitchbeacon;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;

/**
 * Created by nomad on 1/21/16.
 */
@IgnoreExtraProperties
public class Note {
    String title;
    String note;

    public Note() {
    }


    public Note(String title, String note) {
        this.title = title;
        this.note = note;
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


}
