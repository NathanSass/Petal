package com.nathansass.petal;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by nathansass on 2/4/16.
 */
public class LikedEventsListActivity extends AppCompatActivity {
    ListView myList = null;

    LikedEventsAdapter mAdapter = null;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liked_events);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        myList   = (ListView) findViewById(R.id.likedEvents_listView);
        mAdapter = new LikedEventsAdapter(this);
        myList.setAdapter(mAdapter);
    }
}

class LikedEventsAdapter extends BaseAdapter{

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
        location.setText(currentCard.mAddress);

        return row;
    }
}