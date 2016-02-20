package com.nathansass.petal.models;

/**
 * Created by nathansass on 2/9/16.
 */
public class User {
    public String name, username, password;
    public int age, id;

    public User (int id, String name, int age, String username, String password) {
        this.name     = name;
        this.age      = age;
        this.username = username;
        this.password = password;
        this.id       = id;
    }

    public User (String name, int age, String username, String password) {
        this(-1, name, age, username, password);
    }

    public User (String username, String password) {
        this(-1, "", -1, username, password);
    }
}
