package com.nathansass.petal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.nathansass.petal.R;
import com.nathansass.petal.data.ServerRequests;
import com.nathansass.petal.interfaces.PostEventCallback;
import com.nathansass.petal.models.EventCard;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathansass on 2/2/16.
 */
public class CreateEventActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

    }

    public void createEventButtonClick(View view) {
        JSONObject obj = new JSONObject();
        try {
            String title = ((EditText) findViewById(R.id.eventName)).getText().toString();
            obj.put("title", title);

            saveEvent(new EventCard(obj));
//            EventDeck.get().addEvent(new EventCard(obj));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        routeBackToMainPage();

    }

    /* Saves event to the DB */
    public void saveEvent(EventCard newEvent) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.storeEventDataInBackground(newEvent, new PostEventCallback() {
            @Override
            public void done(EventCard returnedEventCard) {
                // I don't think anything needs to be done here
                // The new event will just get sent to the DB
                // And it will be available for subsequent users.
            }
        });
    }

    public void routeBackToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
