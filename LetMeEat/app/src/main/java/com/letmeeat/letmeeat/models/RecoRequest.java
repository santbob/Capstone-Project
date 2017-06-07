package com.letmeeat.letmeeat.models;

/**
 * Created by santhosh on 27/05/2017.
 */

public class RecoRequest {

    private String location;
    private int radius;

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
}
