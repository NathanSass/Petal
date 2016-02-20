package com.nathansass.petal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nathansass.petal.R;
import com.nathansass.petal.data.ServerRequests;
import com.nathansass.petal.interfaces.GetLikedEventsCallback;
import com.nathansass.petal.models.LikedDeck;
import com.nathansass.petal.models.User;
import com.nathansass.petal.data.UserLocalStore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button bLogout;
    EditText etName, etAge, etUsername;
    UserLocalStore userLocalStore;
    User currentUser;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        bLogout    = (Button)   findViewById(R.id.bLogout);

        etName     = (EditText) findViewById(R.id.etName);
        etAge      = (EditText) findViewById(R.id.etAge);
        etUsername = (EditText) findViewById(R.id.etUsername);

        bLogout.setOnClickListener(this);

        userLocalStore = new UserLocalStore(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bLogout:
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);

                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (authenticate()) {
            currentUser = userLocalStore.getLoggedInUser();
//            startActivity(new Intent(MainActivity.this, ChooseEventsActivity.class));
            displayUserDetails();
            retrieveDataForAppInBackground();

        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

    }

    public void retrieveDataForAppInBackground() {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchLikedEventsDataInBackground(currentUser, new GetLikedEventsCallback() {
            @Override
            public void done(LikedDeck returnedLikedDeck) {
                // Nothing to be done here
                // Maybe update
            }
        });
    }

    private void displayUserDetails() {
        etUsername.setText(currentUser.username);
        etName.setText(currentUser.name);
        etAge.setText(currentUser.age + "");
    }

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
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

        if (id == R.id.action_likedEvents) {
            Intent intent = new Intent(this, LikedEventsListActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
