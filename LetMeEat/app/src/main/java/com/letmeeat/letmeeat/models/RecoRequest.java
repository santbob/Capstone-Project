package com.letmeeat.letmeeat.models;

import android.support.annotation.Keep;

/**
 * Created by santhosh on 27/05/2017.
 * The Request body of the places request
 */
@Keep
public class RecoRequest {

    private String location;
    private int radius;
    private int limit;
    private String categories;
    private String ignore;
    private float minRating;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getIgnore() {
        return ignore;
    }

    public void setIgnore(String ignore) {
        this.ignore = ignore;
    }

    public float getMinRating() {
        return minRating;
    }

    public void setMinRating(float minRating) {
        this.minRating = minRating;
    }
}
