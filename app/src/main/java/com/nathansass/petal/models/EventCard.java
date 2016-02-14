package com.nathansass.petal.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathansass on 2/1/16.
 */
public class EventCard {
    public String mTitle;
    public String mAddress;

    public EventCard(JSONObject event_obj) {
        try {
            mTitle = event_obj.getString("title");
            mAddress = "123 Farm St.";
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
