package com.nathansass.petal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathansass on 2/2/16.
 */
public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void createEventButtonClick(View view) {
        JSONObject obj = new JSONObject();
        try {
            String title = ((EditText) findViewById(R.id.eventName)).getText().toString();
            obj.put("title", title);

            EventDeck.addEvent(new EventCard(obj));

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Route back to the event swipe page

    }

}
