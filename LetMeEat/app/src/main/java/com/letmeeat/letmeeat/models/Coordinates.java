package com.letmeeat.letmeeat.models;

import java.io.Serializable;

/**
 * Created by santhosh on 04/06/2017.
 * Object holding the lat & lang of the Recommendation address
 */

public class Coordinates implements Serializable{
    private float latitude;
    private float longitude;

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
