package com.hitch.nomad.hitchbeacon;

/**
 * Created by nomad on 29/10/16.
 */

public class User {

    public String email;
    public String age;
    public String sex;
    public String name;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String age, String sex, String name) {
        this.email = email;
        this.age = age;
        this.sex = sex;
        this.name = name;
    }

}