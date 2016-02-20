package com.nathansass.petal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.nathansass.petal.R;
import com.nathansass.petal.data.ServerRequests;
import com.nathansass.petal.data.UserLocalStore;
import com.nathansass.petal.interfaces.PostEventCallback;
import com.nathansass.petal.interfaces.PostUsersEventsCallback;
import com.nathansass.petal.models.EventCard;
import com.nathansass.petal.models.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathansass on 2/2/16.
 */
public class CreateEventActivity extends AppCompatActivity {

    public static final String TAG = CreateEventActivity.class.getSimpleName();
    User currentUser;
    UserLocalStore mUserLocalStore;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        /* Get user data for the logged in user */
        mUserLocalStore = new UserLocalStore(this);
        currentUser     = mUserLocalStore.getLoggedInUser();

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

    }

    public void createEventButtonClick(View view) {
        JSONObject obj = new JSONObject();
        try {
            String title = ((EditText) findViewById(R.id.eventName)).getText().toString();
            obj.put("title", title);

            saveEvent(new EventCard(obj));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        routeBackToMainPage();

    }

    /* Saves event to the DB */
    public void saveEvent(EventCard newEvent) {
        final ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.storeEventDataInBackground(newEvent, new PostEventCallback() {
            @Override
            public void done(EventCard returnedEventCard) {

                int eventCardId = returnedEventCard.id;
                Log.v(TAG, "eventCardID: " + Integer.toString(eventCardId));
                int currentUserId = currentUser.id;
                Log.v(TAG, "currentUserId: " + currentUserId);

                serverRequests.storeUsersEventsDataInBackground(currentUser, returnedEventCard, new PostUsersEventsCallback() {
                    @Override
                    public void done(User user) {
                        // Currently getting back a user, but it may be better to return a boolean
                        // I don't think anything has to be done here, maybe displaying a toast

                    }
                });

            }
        });
    }

    public void routeBackToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
