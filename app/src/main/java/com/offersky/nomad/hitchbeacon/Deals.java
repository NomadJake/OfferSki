package com.offersky.nomad.hitchbeacon;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.UUID;

/**
 * Created by nomad on 1/21/16.
 */
@IgnoreExtraProperties
public class Deals {
    String title;
    String deal;
    String uid;

    public Deals() {
    }


    public Deals(String title, String deal) {
        this.title = title;
        this.deal = deal;
        this.uid  = UUID.randomUUID().toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getdeal() {
        return deal;
    }

    public void setdeal(String deal) {
        this.deal = deal;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDeal() {
        return deal;
    }

    public void setDeal(String deal) {
        this.deal = deal;
    }
}