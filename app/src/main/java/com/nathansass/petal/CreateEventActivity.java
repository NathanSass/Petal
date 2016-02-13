package com.nathansass.petal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

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

            EventDeck.get().addEvent(new EventCard(obj));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        routeBackToMainPage();

    }

    public void routeBackToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
