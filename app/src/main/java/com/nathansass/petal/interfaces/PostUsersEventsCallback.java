package com.nathansass.petal.interfaces;

/**
 * Created by nathansass on 2/20/16.
 */
public interface PostUsersEventsCallback {
    /* These are the same cards that were originally sent */
    /* Possible should return a boolean */
    void done(int returnedRecordId);
}
