package com.letmeeat.letmeeat.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by santhosh on 23/10/2016.
 * Preferences model, containg pref like cuisine, minimum ratings and anything else.
 */

public class Preferences {

    private float minimumRatings;
    private Map<String, Category> categories = new HashMap<String, Category>();

    public float getMinimumRatings() {
        return minimumRatings;
    }

    public void setMinimumRatings(float minimumRatings) {
        this.minimumRatings = minimumRatings;
    }

    public Map<String, Category> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, Category> categories) {
        this.categories = categories;
    }

    public String getCategoriesAlias() {
        String alias = "";
        for (Iterator<Category> iterator = categories.values().iterator(); iterator.hasNext(); ) {
            Category cat = iterator.next();
            alias += cat.getAlias();
            if (iterator.hasNext()) {
                alias += ",";
            }
        }
        return alias;
    }
}
