package com.letmeeat.letmeeat.models;

/**
 * Created by santhosh on 27/05/2017.
 */

public class RecoRequest {

    private String location;
    private int radius;
    private int limit;

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
}
