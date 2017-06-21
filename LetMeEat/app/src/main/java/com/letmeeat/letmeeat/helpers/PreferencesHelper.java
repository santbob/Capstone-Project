package com.letmeeat.letmeeat.helpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.letmeeat.letmeeat.models.Preferences;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by santhosh on 20/06/2017.
 * Anytime the preferences needs to updated/read this helper class should be used.
 */

public class PreferencesHelper {

    private final static long MILLISECONDS_IN_A_DAY = 604800000;

    public interface PreferencesListener {
        void onPreferencesLoaded(Preferences preferences);
    }

    private final DatabaseReference preferencesDBRef;
    private final PreferencesListener preferencesListener;

    public PreferencesHelper(PreferencesListener prefListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.preferencesDBRef = database.getReference("preferences");
        this.preferencesListener = prefListener;
    }

    private void writeLocalPreferences(Preferences preferences) {
        Utils.setSharedPrefFloat(getApplicationContext(), Utils.MIN_RATINGS, preferences.getMinimumRatings());
        Utils.setSharedPrefStringSet(getApplicationContext(), Utils.CATEGORIES, preferences.getCategories());
        Utils.setSharedPrefStringSet(getApplicationContext(), Utils.RECOS_CHOOSEN_IN_PAST, preferences.getChoosenRecos());

    }

    public void writePreferences(Preferences preferences, boolean isUserLoggedIn) {
        writeLocalPreferences(preferences);
        if (isUserLoggedIn) {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<Preferences> jsonAdapter = moshi.adapter(Preferences.class);
            String json = jsonAdapter.toJson(preferences);
            preferencesDBRef.setValue(json);
        }
    }

    private Preferences getLocalStoredPreferences() {
        Preferences preferences = new Preferences();
        preferences.setMinimumRatings(Utils.getSharedPrefFloat(getApplicationContext(), Utils.MIN_RATINGS));
        preferences.setCategories(Utils.getSharedPrefStringSet(getApplicationContext(), Utils.CATEGORIES));
        preferences.setChoosenRecos(Utils.getSharedPrefStringSet(getApplicationContext(), Utils.RECOS_CHOOSEN_IN_PAST));
        return preferences;
    }

    public void readStoredPreferences(boolean isUserLoggedIn) {
        if (isUserLoggedIn) {
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    Preferences preferencesModel = null;
                    try {
                        String prefJson = (String) dataSnapshot.getValue();
                        Moshi moshi = new Moshi.Builder().build();
                        JsonAdapter<Preferences> jsonAdapter = moshi.adapter(Preferences.class);

                        if (prefJson != null) {
                            preferencesModel = jsonAdapter.fromJson(prefJson);
                        }
                    } catch (Exception e) {
                       //donothing
                    } finally {
                        if (preferencesListener != null) {
                            preferencesListener.onPreferencesLoaded(preferencesModel);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    preferencesListener.onPreferencesLoaded(getLocalStoredPreferences());
                }
            };
            preferencesDBRef.addValueEventListener(postListener);
        } else {
            preferencesListener.onPreferencesLoaded(getLocalStoredPreferences());
        }
    }

    public void updateChoosenReco(String recoId, boolean isUserLoggedIn) {
        Set<String> newValues = new HashSet<>();
        long currentTime = new Date().getTime();
        Set<String> currentValues = Utils.getSharedPrefStringSet(getApplicationContext(), Utils.RECOS_CHOOSEN_IN_PAST);
        if (currentValues != null) {
            for (String recoSetString : currentValues) {
                String[] splits = recoSetString.split(":");
                //check if its been N days since it was last marked has choosen, if yes remove it from choosen.
                if (currentTime < Long.parseLong(splits[1]) + (MILLISECONDS_IN_A_DAY * Utils.DEFAULT_IGNORE_DAYS)) {
                    newValues.add(recoSetString);
                }
            }
        }
        newValues.add(recoId + ":" + new Date().getTime());
        Preferences preferencesModel = getLocalStoredPreferences();
        preferencesModel.setChoosenRecos(newValues);
        writePreferences(preferencesModel, isUserLoggedIn);
    }
}
