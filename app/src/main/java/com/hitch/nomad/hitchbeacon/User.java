package com.hitch.nomad.hitchbeacon;

/**
 * Created by nomad on 29/10/16.
 */

public class User {

    public String email;
    public String age;
    public String sex;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String age, String sex) {
        this.email = email;
        this.age = age;
        this.sex = sex;
    }

}