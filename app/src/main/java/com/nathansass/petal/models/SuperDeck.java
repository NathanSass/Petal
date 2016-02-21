package com.nathansass.petal.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by nathansass on 2/5/16.
 */

/* This is never used directly, only needed to instatiate other classes */
public class SuperDeck {

//    private static SuperDeck mInstance  = null;
//    These functions probably do not need to be static
//    Change this to not be singleton, then have singleton things that instantiate it.

    private List<EventCard> mAllDeck = null;

    SuperDeck() {
        mAllDeck = new ArrayList<>();
    }

    public List<EventCard> getDeck() {
        return mAllDeck;
    }

    public void buildEventDeck(JSONArray events_j) {

        try {

            for (int i = 0; i < events_j.length(); i++) {

                JSONObject event_obj = events_j.getJSONObject(i);
                addEvent(new EventCard(event_obj));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void addEvent(EventCard eventCard) {

        mAllDeck.add(eventCard);

    }

    public void removeEvent(EventCard eventCard) {

        mAllDeck.remove(eventCard);
    }

    public int getEventCount() {
        return mAllDeck.size();
    }

    public EventCard getNewEvent() {
        int idx = new Random().nextInt(getEventCount());
        return mAllDeck.get(idx);
    }

    public void clearDeck() {
        mAllDeck = new ArrayList<>();
    }
}
