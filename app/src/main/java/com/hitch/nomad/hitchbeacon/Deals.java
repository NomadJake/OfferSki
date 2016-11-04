package com.hitch.nomad.hitchbeacon;
import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.SugarRecord;
/**
 * Created by nomad on 1/21/16.
 */
@IgnoreExtraProperties
public class Deals {
    String title;
    String deal;

    public Deals() {
    }


    public Deals(String title, String deal) {
        this.title = title;
        this.deal = deal;
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
}