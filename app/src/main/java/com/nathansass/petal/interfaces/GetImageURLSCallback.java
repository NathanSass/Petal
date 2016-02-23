package com.nathansass.petal.interfaces;

import org.json.JSONArray;

/**
 * Created by nathansass on 2/23/16.
 */
public interface GetImageURLSCallback {
    void done(JSONArray returnedUrls);
}
