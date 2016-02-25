package com.nathansass.petal.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.nathansass.petal.R;
import com.nathansass.petal.data.ServerRequests;
import com.nathansass.petal.data.UserLocalStore;
import com.nathansass.petal.interfaces.PostEventCallback;
import com.nathansass.petal.interfaces.PostUsersEventsCallback;
import com.nathansass.petal.models.EventCard;
import com.nathansass.petal.models.LikedDeck;
import com.nathansass.petal.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by nathansass on 2/2/16.
 */
public class CreateEventActivity extends AppCompatActivity {

    public static final String TAG = CreateEventActivity.class.getSimpleName();
    Context context;
    int duration;

    User currentUser;
    UserLocalStore mUserLocalStore;

    /* Datepicker */
    Date startDateTime;
    int year, month, day, hour, minute;
    EditText dateInput;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        /* Get user data for the logged in user */
        mUserLocalStore = new UserLocalStore(this);
        currentUser = mUserLocalStore.getLoggedInUser();

        /* Toast Things*/
        context = getApplicationContext();
        duration = Toast.LENGTH_SHORT;

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        showDatepickerOnFocus();

        addressAutocompleteWidget();

    }

    public void addressAutocompleteWidget() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                Toast toast = Toast.makeText(context,  "Place name: " + place.getName(), Toast.LENGTH_LONG);
                toast.show();
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
                Toast toast = Toast.makeText(context, "An error occurent with getting the place", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public void showDatepickerOnFocus() {
        dateInput = (EditText) findViewById(R.id.eventDateTime);

        dateInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateTimeDialog();
                }
            }
        });

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog();
            }
        });
    }


    private void showDateTimeDialog() {
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .setMinDate(new Date())
                        //.setMaxDate(maxDate)
                        //.setIs24HourTime(true)
                        //.setTheme(SlideDateTimePicker.HOLO_DARK)
                        //.setIndicatorColor(Color.parseColor("#990000"))
                .build()
                .show();
    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            startDateTime = date;
            /*
             Save the date object;
             change the placeholder on the thing to match;
             */

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            year = calendar.get(calendar.YEAR);
            month = calendar.get(calendar.MONTH);
            day = calendar.get(calendar.DAY_OF_MONTH);

            hour = calendar.get(calendar.HOUR_OF_DAY); //24 hour
            int hour2 = calendar.get(Calendar.HOUR);
            minute = calendar.get(calendar.MINUTE);
            int amPm = calendar.get(calendar.AM_PM);

            Toast toast = Toast.makeText(context, "hourofDay: " + hour + " hour: " + hour2 + " minute: " + minute + " amPm: " + amPm, Toast.LENGTH_LONG);
//            Toast toast = Toast.makeText(context, "Date: " + month + "/" + day + "/" + year, Toast.LENGTH_LONG);
//            Toast toast = Toast.makeText(context, "Date: " + date.toString(), Toast.LENGTH_LONG);
            toast.show();
        }
    };


    public void createEventButtonClick(View view) {
        JSONObject obj = new JSONObject();
        try {
            String title = ((EditText) findViewById(R.id.eventName)).getText().toString();
            obj.put("title", title);

            saveEvent(new EventCard(obj));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        routeBackToMainPage();

    }

    /* Saves event to the DB, creates association in UsersEvents */
    public void saveEvent(final EventCard newEvent) {
        final ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.storeEventDataInBackground(newEvent, new PostEventCallback() {
            @Override
            public void done(final EventCard returnedEventCard) {
                /* As soon as the event is saved -
                * the DB creates the associating that
                * the User Created the event and is attending it.
                * */

                serverRequests.storeUsersEventsDataInBackground(currentUser, returnedEventCard, true, true, new PostUsersEventsCallback() {
                    @Override
                    public void done(int returnedRecordId) {

                        CharSequence txt;
                        if (returnedRecordId > 0) {
                            LikedDeck.get().addEvent(newEvent);
                            txt = "Added: " + returnedEventCard.mTitle;

                        } else {
                            txt = "Error adding new event";
                        }
                        Toast toast = Toast.makeText(context, txt, duration);
                        toast.show();
                    }
                });

            }
        });
    }

    public void routeBackToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_chooseEvents) {

            Intent intent = new Intent(this, ChooseEventsActivity.class);
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
