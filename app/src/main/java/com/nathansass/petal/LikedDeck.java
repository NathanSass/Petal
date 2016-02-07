package com.nathansass.petal;

/**
 * Created by nathansass on 2/5/16.
 */
public class LikedDeck extends SuperDeck {
    private static LikedDeck instance = null;

    protected LikedDeck() {
        // Exists only to defeat instantiation.
    }

    public static LikedDeck get() {
        if(instance == null) {
            instance = new LikedDeck();
        }
        return instance;
    }
}