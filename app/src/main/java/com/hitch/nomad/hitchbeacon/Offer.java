package com.hitch.nomad.hitchbeacon;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;

/**
 * Created by nomad on 1/21/16.
 */
@IgnoreExtraProperties
public class Offer{
    String title;
    String Offer;
    String discovered;
    String hitchId;

    public Offer() {
    }


    public Offer(String title, String Offer, String discovered, String hitchId) {
        this.title = title;
        this.Offer = Offer;
        this.discovered = discovered;
        this.hitchId = hitchId;
    }

    public Offer(String title, String Offer) {
        this.title = title;
        this.Offer = Offer;
        this.discovered = "false";
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOffer() {
        return Offer;
    }

    public void setOffer(String Offer) {
        this.Offer = Offer;
    }

    public String getDiscovered() {
        return discovered;
    }

    public void setDiscovered(String discovered) {
        this.discovered = discovered;
    }

    public String getHitchId() {
        return hitchId;
    }

    public void setHitchId(String hitchId) {
        this.hitchId = hitchId;
    }

}
