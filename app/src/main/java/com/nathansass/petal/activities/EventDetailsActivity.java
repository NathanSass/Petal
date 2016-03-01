package com.nathansass.petal.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nathansass.petal.R;
import com.nathansass.petal.data.ServerRequests;
import com.nathansass.petal.data.UserLocalStore;
import com.nathansass.petal.interfaces.PostUsersEventsCallback;
import com.nathansass.petal.models.EventCard;
import com.nathansass.petal.models.EventDeck;
import com.nathansass.petal.models.LikedDeck;
import com.nathansass.petal.models.User;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = EventDetailsActivity.class.getSimpleName();
    Context context;
    int duration;

    EventCard currentEventCard = null;
    private Toolbar toolbar;
    private ActionBar actionBar;

    Button bDeleteEvent, bAttendEvent;
    Boolean attending;

    ServerRequests serverRequests;
    UserLocalStore userLocalStore;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String positionS = (String) getIntent().getSerializableExtra("eventPosition");
        int position = Integer.parseInt(positionS);
        attending = true;

        // TODO: Change view depending if it is for the person who made it attending
        setContentView(R.layout.activity_event_details);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        /* App level things */
        serverRequests = new ServerRequests(this);
        userLocalStore = new UserLocalStore(this);
        currentUser    = userLocalStore.getLoggedInUser();

        /* Toast Things*/
        context  = getApplicationContext();
        duration = Toast.LENGTH_SHORT;

        /* Find things */
        bDeleteEvent = (Button) findViewById(R.id.deleteEventButton);
        bAttendEvent = (Button) findViewById(R.id.attendEventButton);

        /* Register click listeners */
        bDeleteEvent.setOnClickListener(this);
        bAttendEvent.setOnClickListener(this);

        /* Do things */
        currentEventCard = LikedDeck.get().getDeck().get(position);
        updateEventCardUI();
    }

    public void updateEventCardUI() {
        TextView eventTitle = (TextView) findViewById(R.id.eventTitle);
        eventTitle.setText(currentEventCard.mTitle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteEventButton:
                Log.v(TAG, "delete button clicked");
                break;
            case R.id.attendEventButton:
                CharSequence buttonText;
                int color;
                if (attending) {
                    buttonText = getResources().getString(R.string.action_eventDetails_notAttending);
                    color = Color.BLACK;
                    attending = false;
                } else {
                    buttonText = getResources().getString(R.string.action_eventDetails_attending);
                    color = ContextCompat.getColor(context, R.color.ColorPositiveGreen);
                    attending = true;
                }

                bAttendEvent.setText(buttonText);
                bAttendEvent.setTextColor(color);

                Log.v(TAG, "attend button clicked");

                LikedDeck.get().removeEvent(currentEventCard);
                EventDeck.get().addEvent(currentEventCard);

                serverRequests.storeUsersEventsDataInBackground(currentUser, currentEventCard, attending, new PostUsersEventsCallback() {
                    @Override
                    public void done(Void returnedRecordId) {
                    /* Nothing needs to be done */
                    }
                });
                break;
        }
    }
}
