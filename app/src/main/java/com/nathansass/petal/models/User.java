package com.nathansass.petal.models;

/**
 * Created by nathansass on 2/9/16.
 */
public class User {
    public String name, username, password;
    public int age;

    public User (String name, int age, String username, String password) {
        this.name     = name;
        this.age      = age;
        this.username = username;
        this.password = password;
    }

    public User (String username, String password) {
        this.username = username;
        this.password = password;
        this.age      = -1;
        this.name     = "";
    }
}
