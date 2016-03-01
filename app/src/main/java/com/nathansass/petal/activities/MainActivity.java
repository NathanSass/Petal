package com.nathansass.petal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nathansass.petal.data.ServerRequests;
import com.nathansass.petal.data.UserLocalStore;
import com.nathansass.petal.interfaces.GetEventsCallback;
import com.nathansass.petal.models.EventDeck;
import com.nathansass.petal.models.LikedDeck;
import com.nathansass.petal.models.User;

public class MainActivity extends AppCompatActivity  {

    UserLocalStore userLocalStore;
    User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* By clearing the decks on every main entry, it will ensure new data */
        LikedDeck.get().clearDeck();
        EventDeck.get().clearDeck();

        userLocalStore = new UserLocalStore(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if ( authenticate() ) {
            currentUser = userLocalStore.getLoggedInUser();

            getDataFromServerThenRouteToChooseEvents();

        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

    }

    private void getDataFromServerThenRouteToChooseEvents() {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchEventDataInBackground(currentUser, new GetEventsCallback() {
            @Override
            public void done(EventDeck returnedEventDeck) {
                /* When the events are back, it starts the event so it feels non-laggy */
                startActivity(new Intent(MainActivity.this, ChooseEventsActivity.class));
            }
        });
    }

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }

}
