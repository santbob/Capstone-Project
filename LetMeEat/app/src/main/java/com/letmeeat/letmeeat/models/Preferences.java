package com.letmeeat.letmeeat.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh on 23/10/2016.
 * Preferences model, containg pref like cuisine, minimum ratings and anything else.
 */

public class Preferences {

    private float minimumRatings;
    private List<Category> preferedCuisines = new ArrayList<Category>();

    public float getMinimumRatings() {
        return minimumRatings;
    }

    public void setMinimumRatings(float minimumRatings) {
        this.minimumRatings = minimumRatings;
    }

    public List<Category> getPreferedCuisines() {
        return preferedCuisines;
    }

    public void setPreferedCuisines(List<Category> preferedCuisines) {
        this.preferedCuisines = preferedCuisines;
    }
}
