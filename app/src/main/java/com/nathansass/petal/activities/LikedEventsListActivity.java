package com.nathansass.petal.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nathansass.petal.R;
import com.nathansass.petal.data.UserLocalStore;
import com.nathansass.petal.models.EventCard;
import com.nathansass.petal.models.LikedDeck;
import com.nathansass.petal.models.User;

/**
 * Created by nathansass on 2/4/16.
 */
public class LikedEventsListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    public static final String TAG = LikedEventsListActivity.class.getSimpleName();
    Context context;
    int duration;

    UserLocalStore mUserLocalStore;
    User currentUser;

    ListView myList = null;
    LikedEventsAdapter mAdapter = null;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liked_events);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        /* Get user data for the logged in user */
        mUserLocalStore = new UserLocalStore(this);
        currentUser     = mUserLocalStore.getLoggedInUser();

        /* Toast Things*/
        context  = getApplicationContext();
        duration = Toast.LENGTH_SHORT;

        /* Sets Adapter */
        myList   = (ListView) findViewById(R.id.likedEvents_listView);
        mAdapter = new LikedEventsAdapter(this);
        myList.setAdapter(mAdapter);

        /* Set listener to Launch Event details page*/
        myList.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_chooseEvents) {

            Intent intent = new Intent(this, ChooseEventsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_createEvent) {

            Intent intent = new Intent(this, CreateEventActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_liked_events, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra("eventPosition", position + "");
        startActivity(intent);

    }
}

class LikedEventsAdapter extends BaseAdapter {

    Context mContext;

    LikedEventsAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {

        return LikedDeck.get().getEventCount();
    }

    @Override
    public Object getItem(int position) {

        return LikedDeck.get().getDeck().get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.liked_events_single_row, viewGroup, false);

        /* Find all text fields here to be set */
        TextView title    = (TextView) row.findViewById(R.id.likedEvents_title);
        TextView location = (TextView) row.findViewById(R.id.likedEvents_location);

        EventCard currentCard = (EventCard) getItem(position);
        /* set the text of each field here */
        title.setText(currentCard.mTitle);
        location.setText(currentCard.street);

        return row;
    }
}