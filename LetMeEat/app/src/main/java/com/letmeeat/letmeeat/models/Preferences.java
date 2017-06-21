package com.letmeeat.letmeeat.models;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by santhosh on 23/10/2016.
 * Preferences model, containg pref like cuisine, minimum ratings and anything else.
 */

public class Preferences {

    private float minimumRatings;
    private Set<String> categories = new HashSet<String>();
    private Set<String> choosenRecos = new HashSet<String>();

    public float getMinimumRatings() {
        return minimumRatings;
    }

    public void setMinimumRatings(float minimumRatings) {
        this.minimumRatings = minimumRatings;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public Set<String> getChoosenRecos() {
        return choosenRecos;
    }

    public void setChoosenRecos(Set<String> choosenRecos) {
        this.choosenRecos = choosenRecos;
    }
}
