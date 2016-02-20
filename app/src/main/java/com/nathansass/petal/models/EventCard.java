package com.nathansass.petal.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathansass on 2/1/16.
 */
public class EventCard {
    public String mTitle;
    public String street;
    public int id;

    public EventCard(JSONObject event_obj) {

        try {

            mTitle = event_obj.getString("title");
            street = "123 Farm St.";

            if (event_obj.has("id")) {
                id  = Integer.parseInt(event_obj.getString("id"));
                setId(id);
            } else {
                setId(-1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setId(int id) {
        this.id = id;
    }

}
