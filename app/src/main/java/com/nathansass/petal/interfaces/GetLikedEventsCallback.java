package com.nathansass.petal.interfaces;

import com.nathansass.petal.models.LikedDeck;

/**
 * Created by nathansass on 2/20/16.
 */
public interface GetLikedEventsCallback {
    void done(LikedDeck returnedLikedDeck);
}
