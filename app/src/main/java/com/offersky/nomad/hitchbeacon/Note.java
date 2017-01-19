package com.offersky.nomad.hitchbeacon;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by nomad on 1/21/16.
 */
@IgnoreExtraProperties
public class Note {
    String shopURI;
    String title;
    String note;
    String segment;
    String logoURI;
    String code;
    Boolean discovered;

    public Note() {
    }

    public Note(String title, String note, String segment,String shopURI,String logoURI, String code,Boolean discovered) {
        this.title = title;
        this.note = note;
        this.shopURI  = shopURI;
        this.segment = segment;
        this.logoURI = logoURI;
        this.code = code;
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

    public String getUid() {
        return shopURI;
    }

    public void setUid(String uid) {
        this.shopURI = uid;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShopURI() {
        return shopURI;
    }

    public void setShopURI(String shopURI) {
        this.shopURI = shopURI;
    }

    public String getLogoURI() {
        return logoURI;
    }

    public void setLogoURI(String logoURI) {
        this.logoURI = logoURI;
    }

    public Boolean getDiscovered() {
        return discovered;
    }

    public void setDiscovered(Boolean discovered) {
        this.discovered = discovered;
    }


}
