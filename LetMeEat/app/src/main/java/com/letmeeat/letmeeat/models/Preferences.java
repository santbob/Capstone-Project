package com.letmeeat.letmeeat.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh on 23/10/2016.
 * Preferences model, containg pref like cuisine, minimum ratings and anything else.
 */

public class Preferences {

    private float minimumRatings;
    private List<String> preferedCuisines = new ArrayList<String>();

    public float getMinimumRatings() {
        return minimumRatings;
    }

    public void setMinimumRatings(float minimumRatings) {
        this.minimumRatings = minimumRatings;
    }

    public List<String> getPreferedCuisines() {
        return preferedCuisines;
    }

    public void setPreferedCuisines(List<String> preferedCuisines) {
        this.preferedCuisines = preferedCuisines;
    }
}
