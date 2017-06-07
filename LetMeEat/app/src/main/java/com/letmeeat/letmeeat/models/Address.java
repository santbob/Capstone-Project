package com.letmeeat.letmeeat.models;

import android.text.TextUtils;

/**
 * Created by santhosh on 18/10/2016.
 * Model class which holds the physical address of the Restaurants/Establishments
 */

public class Address {

    private static final String SPACE = " ";
    public static final String COMMA = ",";
    private String streetLine1;
    private String streetLine2;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String displayAddress;
    private Coordinates coordinates;
    private String landmark;

    public String getStreetLine1() {
        return streetLine1;
    }

    public void setStreetLine1(String streetLine1) {
        this.streetLine1 = streetLine1;
    }

    public String getStreetLine2() {
        return streetLine2;
    }

    public void setStreetLine2(String streetLine2) {
        this.streetLine2 = streetLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDisplayAddress() {
        return displayAddress;
    }

    public void setDisplayAddress(String displayAddress) {
        this.displayAddress = displayAddress;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getPrintableAddress(String separter) {
        separter = (separter != null) ? separter : SPACE;
        String address = "";
        if (!TextUtils.isEmpty(streetLine1)) {
            address += streetLine1;
        }
        if (!TextUtils.isEmpty(streetLine2)) {
            address += separter + streetLine2;
        }
        if (!TextUtils.isEmpty(city)) {
            address += separter + city;
        }
        if (!TextUtils.isEmpty(state)) {
            address += separter + state;
        }
        if (!TextUtils.isEmpty(zip)) {
            address += SPACE + zip;
        }
        return address;
    }

    public String getCityState() {
        String cityState = "";
        if (!TextUtils.isEmpty(city)) {
            cityState += city;
        }
        if (!TextUtils.isEmpty(state)) {
            cityState += SPACE + state;
        }
        return cityState;
    }
}
