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
import com.google.android.gms.maps.model.LatLng;
import com.nathansass.petal.R;
import com.nathansass.petal.data.ServerRequests;
import com.nathansass.petal.data.UserLocalStore;
import com.nathansass.petal.interfaces.PostEventCallback;
import com.nathansass.petal.models.EventCard;
import com.nathansass.petal.models.User;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    DateTime startDateTime, endDateTime;
    int year, month, day, hour, minute;

    /* Address */
    Double lat, lng;
    String placeName;
    String street, city, state;

    /* UI Fields */
    EditText etEventTitle;
    EditText etEventDuration;
    EditText etEventPrice;
    EditText etEventAbout;
    EditText etEventSize;
    EditText etEventDateTime;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        /* Get UI components */
        etEventTitle    = (EditText) findViewById(R.id.eventName);
        etEventDuration = (EditText) findViewById(R.id.eventDuration);
        etEventPrice    = (EditText) findViewById(R.id.eventPrice);
        etEventAbout    = (EditText) findViewById(R.id.eventAbout);
        etEventSize     = (EditText) findViewById(R.id.eventSize);
        etEventDateTime = (EditText) findViewById(R.id.eventDateTime);

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

                placeName = (String) place.getName();     // 5021 Strathmore Ave || bob's

                LatLng latLng    = place.getLatLng();            // (40.935776999999995,-81.401623)
                String address   = (String) place.getAddress();  // 5021 Strathmore Ave, Kensington, MD 20895, USA

                List<String> addressList = Arrays.asList(address.split(","));

                street = addressList.get(0).trim();
                city   = addressList.get(1).trim();
                state  = addressList.get(2).substring(0, 3).trim();

                lat = latLng.latitude;
                lng = latLng.longitude;
                Log.v(TAG, "street " + street + " city: " + city + " state: " + state);


            }

            @Override
            public void onError(Status status) {
                Toast.makeText(context, "An error occurred with getting the place", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showDatepickerOnFocus() {

        etEventDateTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateTimeDialog();
                }
            }
        });

        etEventDateTime.setOnClickListener(new View.OnClickListener() {
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
                        .setIs24HourTime(true)
                        //.setTheme(SlideDateTimePicker.HOLO_DARK)
                        //.setIndicatorColor(Color.parseColor("#990000"))
                .build()
                .show();
    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            startDateTime = new DateTime(date);

            LocalDate localDate   = new LocalDate(startDateTime);
            String namedDayOfWeek = localDate.withDayOfWeek(2).dayOfWeek().getAsText();

            CharSequence prettyDate = startDateTime.hourOfDay().get() + ":"
                    + startDateTime.minuteOfHour().get() + " on "
                    + namedDayOfWeek + " "
                    + startDateTime.dayOfMonth().get()  + "/"
                    + startDateTime.monthOfYear().get() + "/"
                    + startDateTime.year().get();

            etEventDateTime.setHint(prettyDate);
        }
    };


    public void createEventButtonClick(View view) {

        String title  = etEventTitle.getText().toString();
        String about  = etEventAbout.getText().toString();
        int price     = Integer.parseInt(etEventPrice.getText().toString());
        int eventSize = Integer.parseInt(etEventSize.getText().toString());

        endDateTime = startDateTime.plusHours(2);

        saveEvent( new EventCard("", title, street, city, state,
                                startDateTime, endDateTime,
                                about, price, eventSize,
                                lat, lng) );

        routeBackToMainPage();

    }

    /* Saves event to the DB, creates association in UsersEvents */
    public void saveEvent(final EventCard newEvent) {
        final ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.storeEventDataInBackground(currentUser, newEvent, new PostEventCallback() {
            @Override
            public void done(final EventCard returnedEventCard) {
                /* Event is stored & association is created with the user who created the event */
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
