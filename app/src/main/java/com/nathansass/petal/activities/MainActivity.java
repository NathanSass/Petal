package com.nathansass.petal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nathansass.petal.data.ServerRequests;
import com.nathansass.petal.data.UserLocalStore;
import com.nathansass.petal.interfaces.GetEventsCallback;
import com.nathansass.petal.interfaces.GetLikedEventsCallback;
import com.nathansass.petal.models.EventDeck;
import com.nathansass.petal.models.LikedDeck;
import com.nathansass.petal.models.User;

public class MainActivity extends AppCompatActivity  {

    UserLocalStore userLocalStore;
    User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userLocalStore = new UserLocalStore(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if ( authenticate() ) {
            currentUser = userLocalStore.getLoggedInUser();

            if ( EventDeck.get().getDeck().isEmpty() ) {

                getDataFromServerThenRouteToChooseEvents();

            } else {

                startActivity(new Intent(MainActivity.this, ChooseEventsActivity.class));

            }

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
        /* Nothing needs to be done in the callback, maybe displaying an error message or retrying if a fail happens */
        serverRequests.fetchLikedEventsDataInBackground(currentUser, new GetLikedEventsCallback() {
            @Override
            public void done(LikedDeck returnedLikedDeck) {}
        });
    }

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }

}
