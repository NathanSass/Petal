package com.nathansass.petal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nathansass.petal.data.UserLocalStore;

public class MainActivity extends AppCompatActivity  {

    UserLocalStore userLocalStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userLocalStore = new UserLocalStore(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (authenticate()) {

            startActivity(new Intent(MainActivity.this, ChooseEventsActivity.class));

        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

    }

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }

}
