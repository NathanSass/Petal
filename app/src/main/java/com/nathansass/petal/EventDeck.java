package com.nathansass.petal;

/**
 * Created by nathansass on 2/1/16.
 */
public class EventDeck extends SuperDeck { //A Singleton

    private static EventDeck instance = null;

    protected EventDeck() {
        // Exists only to defeat instantiation.
    }

    public static EventDeck get() {
        if(instance == null) {
            instance = new EventDeck();
        }
        return instance;
    }


}
