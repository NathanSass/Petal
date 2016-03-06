package com.nathansass.petal.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nathansass.petal.R;
import com.nathansass.petal.data.ServerRequests;
import com.nathansass.petal.data.UserLocalStore;
import com.nathansass.petal.interfaces.PutEventAttendingCallback;
import com.nathansass.petal.models.EventCard;
import com.nathansass.petal.models.EventDeck;
import com.nathansass.petal.models.EventDisplayer;
import com.nathansass.petal.models.LikedDeck;
import com.nathansass.petal.models.User;

public class ChooseEventsActivity extends EventDisplayer {

    public static final String TAG = ChooseEventsActivity.class.getSimpleName();
    Context context;
    int duration;

    private Toolbar toolbar;
    private android.support.v7.app.ActionBar actionBar;

    UserLocalStore mUserLocalStore;

    EventCard currentEvent = null;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_events);

        /* Create the toolbar */
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.petal_logo_with_text);

        getUIComponents();

        /* Get user data for the logged in user */
        mUserLocalStore = new UserLocalStore(this);
        currentUser     = mUserLocalStore.getLoggedInUser();

        /* Toast Things*/
        context  = getApplicationContext();
        duration = Toast.LENGTH_SHORT;

        updateEventCardUI();
    }


    public void eventSkipButtonClick(View view) {
        // TODO: delete event when skip is clicked

        updateEventCardUI();
    }

    public void updateEventCardUI() {

        if ( !EventDeck.get().getDeck().isEmpty() ) {
            currentEvent = EventDeck.get().getNewEvent();

            updateEventUI(currentEvent);
        } else {
            setEmptyDeck();
        }

    }

    public void eventLikeButtonClick(View view) {

        if ( !EventDeck.get().getDeck().isEmpty() ){

            EventDeck.get().removeEvent(currentEvent);

            LikedDeck.get().addEvent(currentEvent);

            ServerRequests serverRequests = new ServerRequests(this);
            serverRequests.storeEventAttendDataInBackground(currentUser, currentEvent, true, new PutEventAttendingCallback() {
                @Override
                public void done(Void returnedRecordId) {
                    /* Nothing needs to be done */
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

}
