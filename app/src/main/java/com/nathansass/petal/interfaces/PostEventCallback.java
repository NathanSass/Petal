package com.nathansass.petal.interfaces;

import com.nathansass.petal.models.EventCard;

/**
 * Created by nathansass on 2/19/16.
 */
public interface PostEventCallback {
    void done(EventCard returnedEventCard);
}
