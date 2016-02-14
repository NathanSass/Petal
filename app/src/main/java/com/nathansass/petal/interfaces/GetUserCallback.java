package com.nathansass.petal.interfaces;

import com.nathansass.petal.models.User;

/**
 * Created by nathansass on 2/10/16.
 */
public interface GetUserCallback {

    void done(User returnedUser);
}
