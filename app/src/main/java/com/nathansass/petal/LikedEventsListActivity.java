package com.nathansass.petal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by nathansass on 2/4/16.
 */
public class LikedEventsListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liked_events);

        String[] myStringArray={"A","B","C"};

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, myStringArray);

        ListView myList= (ListView) findViewById(R.id.likedEvents_listView);

        myList.setAdapter(myAdapter);
    }
}
