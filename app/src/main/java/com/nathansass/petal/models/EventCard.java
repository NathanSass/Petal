package com.nathansass.petal.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathansass on 2/1/16.
 */
public class EventCard {
    public static final String TAG = EventCard.class.getSimpleName();

    public String mTitle, id;
    public String street;

    public EventCard(JSONObject event_obj) {

        try {

            mTitle = event_obj.getString("title");
            street = "123 Farm St.";

            if (event_obj.has("_id")) {
                id  = event_obj.getString("_id");
                setId(id);
            } else {
                setId("");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setId(String id) {
        this.id = id;
    }

}
