package com.letmeeat.letmeeat.models;

import java.util.List;

/**
 * Created by santhosh on 18/10/2016.
 * Model class for the Recommendation
 */

public class Recommendation {

    private String name;
    private String cuisine;
    private int reviewsCount;
    private float rating;
    private int startPrice;
    private int endPrice;
    private String currency;
    private Address address;
    private String phone;
    private String website;
    private List<String> photos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(int startPrice) {
        this.startPrice = startPrice;
    }

    public int getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(int endPrice) {
        this.endPrice = endPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
