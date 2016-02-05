package com.nathansass.petal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by nathansass on 2/1/16.
 */
public class EventDeck { //A Singleton

    private static EventDeck mInstance = null;

    private static List<EventCard> mEventDeck = null;

    private EventDeck() {
        mEventDeck = new ArrayList<EventCard>();
    }

    public static EventDeck getInstance() {
        if ( mInstance == null) {
            mInstance = new EventDeck();
        }

        return mInstance;
    }

    public static List<EventCard> getDeck() {
        return mEventDeck;
    }

    public static void buildEventDeck(JSONArray events_j) {

        try {

            for (int i = 0; i < events_j.length(); i++) {

                JSONObject event_obj = events_j.getJSONObject(i);
                addEvent(new EventCard(event_obj));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public static EventCard getCardAtIndex(int index) {
//        return mEventDeck.get(index);
//    }


    public static void addEvent(EventCard eventCard) {

        mEventDeck.add(eventCard);

    }

    public static int getEventCount() {
        return mEventDeck.size();
    }

    public static EventCard getNewEvent() {
        int idx = new Random().nextInt(getEventCount());
        return mEventDeck.get(idx);
    }
}
