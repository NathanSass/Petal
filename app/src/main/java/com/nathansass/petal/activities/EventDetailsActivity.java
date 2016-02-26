package com.nathansass.petal.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.nathansass.petal.R;
import com.nathansass.petal.models.EventCard;
import com.nathansass.petal.models.LikedDeck;

public class EventDetailsActivity extends AppCompatActivity {
    public static final String TAG = EventDetailsActivity.class.getSimpleName();
    Context context;
    int duration;

    EventCard currentEventCard = null;
    private Toolbar toolbar;
    private android.support.v7.app.ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        /* Toast Things*/
        context  = getApplicationContext();
        duration = Toast.LENGTH_SHORT;

        String positionS = (String) getIntent().getSerializableExtra("eventPosition");
        int position     = Integer.parseInt(positionS);

        currentEventCard = LikedDeck.get().getDeck().get(position);

        updateEventCardUI();
    }

    public void updateEventCardUI(){
        TextView eventTitle = (TextView) findViewById(R.id.eventTitle);
        eventTitle.setText(currentEventCard.mTitle);
    }

}
