package com.offersky.nomad.hitchbeacon;

import java.util.List;

/**
 * Created by nomad on 29/10/16.
 */

public class User {

    public String email;
    public String age;
    public String sex;
    public String name;
    public List<String> discoveredOffers;
    public List<String> discoveredNotes;
    public String bloodGroup;
    public String fcm;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String age, String sex, String name,List<String>ofl,List<String>ntl) {
        this.email = email;
        this.age = age;
        this.sex = sex;
        this.name = name;
        this.discoveredOffers = ofl;
        this.discoveredNotes = ntl;
        this.bloodGroup = "Default";
        this.fcm = "asdf";

    }


}