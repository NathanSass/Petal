package com.nathansass.petal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    EventCard mCurrentEventCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Instantiate different decks */
        EventDeck.get();
        LikedDeck.get();

        /* On first instantiation: pulls in data to populate the event cards */
        if ( EventDeck.get().getDeck().isEmpty() ) {

            try {
                String activityString = loadJSONFromAsset("activities");
                JSONObject obj        = new JSONObject(activityString);
                JSONArray events_arr  = obj.getJSONArray("events");

                EventDeck.get().buildEventDeck(events_arr);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        updateEventCardUI();

    }


    public String loadJSONFromAsset(String filePath) {
        String json = null;
        try {
            InputStream is = getAssets().open(filePath);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void eventLikeButtonClick(View view) {

        if ( !EventDeck.get().getDeck().isEmpty() ){

            EventDeck.get().removeEvent(mCurrentEventCard);

            LikedDeck.get().addEvent(mCurrentEventCard);
        }

        updateEventCardUI();
    }

    public void eventSkipButtonClick(View view) {
        // todo: delete event when skip is clicked

        updateEventCardUI();
    }

    public void updateEventCardUI() {
        TextView eventTitle = (TextView) findViewById(R.id.eventTitle);

        if ( !EventDeck.get().getDeck().isEmpty() ) {
            mCurrentEventCard   = EventDeck.get().getNewEvent();
            eventTitle.setText(mCurrentEventCard.mTitle);
        } else {
            eventTitle.setText(R.string.deck_empty_message);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_createEvent) {

            Intent intent = new Intent(this, CreateEventActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_likedEvents) {
            Intent intent = new Intent(this, LikedEventsListActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
