package com.nathansass.petal.interfaces;

import com.nathansass.petal.models.EventDeck;

/**
 * Created by nathansass on 2/18/16.
 */
public interface GetEventsCallback {
    void done(EventDeck returnedEventDeck);
}
