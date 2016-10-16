package com.hitch.nomad.hitchbeacon;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;

/**
 * Created by nomad on 1/21/16.
 */
@IgnoreExtraProperties
public class Note extends SugarRecord {
    String title;
    String note;
    String discovered;

    public Note() {
    }


    public Note(String title, String note, String discovered) {
        this.title = title;
        this.note = note;
        this.discovered = discovered;
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

    public String getDiscovered() {
        return discovered;
    }

    public void setDiscovered(String discovered) {
        this.discovered = discovered;
    }

}
