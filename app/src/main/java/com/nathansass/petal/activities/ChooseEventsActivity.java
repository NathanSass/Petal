package com.nathansass.petal.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nathansass.petal.R;
import com.nathansass.petal.data.ServerRequests;
import com.nathansass.petal.data.UserLocalStore;
import com.nathansass.petal.interfaces.GetEventsCallback;
import com.nathansass.petal.interfaces.GetLikedEventsCallback;
import com.nathansass.petal.interfaces.PostUsersEventsCallback;
import com.nathansass.petal.models.EventCard;
import com.nathansass.petal.models.EventDeck;
import com.nathansass.petal.models.LikedDeck;
import com.nathansass.petal.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class ChooseEventsActivity extends AppCompatActivity {

    public static final String TAG = ChooseEventsActivity.class.getSimpleName();
    Context context;
    int duration;

    EventCard mCurrentEventCard = null;
    private Toolbar toolbar;

    UserLocalStore mUserLocalStore;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_events);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

         /* Instantiate different decks */
        EventDeck.get();
        LikedDeck.get();

        /* Get user data for the logged in user */
        mUserLocalStore = new UserLocalStore(this);
        currentUser     = mUserLocalStore.getLoggedInUser();

        /* Toast Things*/
        context  = getApplicationContext();
        duration = Toast.LENGTH_SHORT;

        /* On first instantiation: pulls in data to populate the event cards */
        if ( EventDeck.get().getDeck().isEmpty() ) {
            getDataFromServer();
        } else {
            updateEventCardUI();
        }
    }

    public void getDataFromServer() {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchEventDataInBackground(currentUser, new GetEventsCallback() {
            @Override
            public void done(EventDeck returnedEventDeck) {

                updateEventCardUI();

            }
        });
        serverRequests.fetchLikedEventsDataInBackground(currentUser, new GetLikedEventsCallback() {
            @Override
            public void done(LikedDeck returnedLikedDeck) {
                // Nothing needs to be done here
            }
        });
    }

    public void eventSkipButtonClick(View view) {
        // TODO: delete event when skip is clicked

        updateEventCardUI();
    }

    public void updateEventCardUI() {
        TextView eventTitle = (TextView) findViewById(R.id.eventTitle);

        if ( !EventDeck.get().getDeck().isEmpty() ) {
            mCurrentEventCard = EventDeck.get().getNewEvent();
            eventTitle.setText(mCurrentEventCard.mTitle);
        } else {
            eventTitle.setText(R.string.deck_empty_message);
        }

    }

    public void eventLikeButtonClick(View view) {

        if ( !EventDeck.get().getDeck().isEmpty() ){

            EventDeck.get().removeEvent(mCurrentEventCard);

            LikedDeck.get().addEvent(mCurrentEventCard);

            ServerRequests serverRequests = new ServerRequests(this);
            serverRequests.storeUsersEventsDataInBackground(currentUser, mCurrentEventCard, false, true, new PostUsersEventsCallback() {
                @Override
                public void done(int returnedRecordId) {
                    CharSequence txt;
                    if (returnedRecordId < 1) {
                        txt = "Error Liking: " + mCurrentEventCard.mTitle;
                        Toast toast = Toast.makeText(context, txt, duration);
                        toast.show();
                    }
                }
            });
        }

        updateEventCardUI();
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

        if (id == R.id.action_account) {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* DEPRECATED: Methods for getting Event Info from local JSON File*/
    public void getDataFromLocalJSONFile() {
        try {
            String activityString = loadJSONFromAsset("activities");
            JSONObject obj        = new JSONObject(activityString);
            JSONArray events_arr  = obj.getJSONArray("events");

            EventDeck.get().buildEventDeck(events_arr);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String loadJSONFromAsset(String filePath) {
        String json;
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
    /* */
}
