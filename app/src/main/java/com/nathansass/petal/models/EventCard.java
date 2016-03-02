package com.nathansass.petal.models;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathansass on 2/1/16.
 */
public class EventCard {
    public static final String TAG = EventCard.class.getSimpleName();

    public String id, title, street, city, state, about;

    public DateTime startDateTime, endDateTime;

    public int price, eventSize;

    public Double lat, lng;

    public EventCard(JSONObject event_obj) {

        try {
            String mId    =  event_obj.getString("_id");
            String mTitle = event_obj.getString("title");

            String mStreet = event_obj.getString("street");
            String mCity   = event_obj.getString("city");
            String mState  = event_obj.getString("state");

            String mStart   = event_obj.getString("startDateTime");
            String mEnd     = event_obj.getString("endDateTime");
            DateTime mStartDate = new DateTime(mStart);
            DateTime mEndDate   = new DateTime(mEnd);

            String mAbout  = event_obj.getString("about");
            int mPrice     = event_obj.getInt("price");
            int mEventSize = event_obj.getInt("eventSize");

            double mLat = event_obj.getJSONObject("loc").getDouble("lat");
            double mLng = event_obj.getJSONObject("loc").getDouble("lng");

            this.id    = mId;
            this.title = mTitle;

            this.street = mStreet;
            this.city   = mCity;
            this.state  = mState;

            this.startDateTime = mStartDate;
            this.endDateTime   = mEndDate;

            this.about     = mAbout;
            this.price     = mPrice;
            this.eventSize = mEventSize;

            this.lat = mLat;
            this.lng = mLng;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public EventCard(String id,
                     String title, String street, String city, String state,
                     DateTime startDateTime, DateTime endDateTime,
                     String about, int price, int eventSize,
                     Double lat, Double lng) {

        this.id    = id;
        this.title = title;

        this.street = street;
        this.city   = city;
        this.state  = state;

        this.startDateTime = startDateTime;
        this.endDateTime   = endDateTime;

        this.about     = about;
        this.price     = price;
        this.eventSize = eventSize;

        this.lat = lat;
        this.lng = lng;
    }


    public void setId(String id) {
        this.id = id;
    }

}
