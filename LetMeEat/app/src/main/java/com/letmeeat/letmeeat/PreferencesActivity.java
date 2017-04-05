package com.letmeeat.letmeeat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.letmeeat.letmeeat.models.Preferences;
import com.letmeeat.letmeeat.views.TagView;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh on 20/10/2016.
 * Recommendations in LetMeEat are based on the Preferences set in this activity.
 */

public class PreferencesActivity extends BaseActivity implements TagView.TagViewListener {

    private final String TAG = getClass().getSimpleName();

    private final String[] cuisines = new String[]{
            "Meditranean", "Italian", "Indian", "Chineese", "American"
    };

    private DatabaseReference preferencesDBRef;
    private List<String> cuisinePref = new ArrayList<String>();
    private Preferences preferencesModel;


    private EditText minRatingsTextView;
    private AutoCompleteTextView autoCompleteTextView;
    private FlexboxLayout selectedCuisinesLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        preferencesDBRef = database.getReference("preferences");
        preferencesModel = new Preferences();

        minRatingsTextView = (EditText) findViewById(R.id.pref_minimum_ratings);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, cuisines);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.pref_cuisine_lookup);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                addCuisine2Preference(adapter.getItem(pos));
                autoCompleteTextView.setText("");
            }
        });
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(0);

        selectedCuisinesLayout = (FlexboxLayout) findViewById(R.id.selected_cuisines);

        readPreferences();

        Button savePreferences = (Button) findViewById(R.id.pref_save);
        savePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save preferences
                savePreferences();
            }
        });
    }


    private void addCuisineTag(String cuisine) {
        TagView tagView = new TagView(this);
        tagView.build(cuisine, cuisine, this);
        selectedCuisinesLayout.addView(tagView);
    }

    private void savePreferences() {
        if (preferencesModel == null) {
            preferencesModel = new Preferences();
        }
        //FIXME: prone to number format exception, fix this
        preferencesModel.setMinimumRatings(Float.valueOf(minRatingsTextView.getText().toString()));
        preferencesModel.setPreferedCuisines(cuisinePref);

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Preferences> jsonAdapter = moshi.adapter(Preferences.class);

        String json = jsonAdapter.toJson(preferencesModel);
        preferencesDBRef.setValue(json);
    }

    private void addCuisine2Preference(String cuisine) {
        if (cuisinePref.indexOf(cuisine) < 0) {
            cuisinePref.add(cuisine);
            addCuisineTag(cuisine);
        }
    }

    @Override
    public void onTagDelete(TagView view) {
        int index = cuisinePref.indexOf(view.getValue());
        if (index >= 0) {
            cuisinePref.remove(index);
            selectedCuisinesLayout.removeView(view);
        }
    }

    private void readPreferences() {
        showProgressDialog(null, null);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                try {
                    String prefJson = (String) dataSnapshot.getValue();
                    Moshi moshi = new Moshi.Builder().build();
                    JsonAdapter<Preferences> jsonAdapter = moshi.adapter(Preferences.class);

                    if (prefJson != null) {
                        preferencesModel = jsonAdapter.fromJson(prefJson);
                        if (preferencesModel != null) {
                            if (preferencesModel.getPreferedCuisines() != null) {
                                for (String cuisine : preferencesModel.getPreferedCuisines()) {
                                    addCuisine2Preference(cuisine);
                                }
                            }
                            if (preferencesModel.getMinimumRatings() > 0) {
                                minRatingsTextView.setText(getString(R.string.float_placeholder, preferencesModel.getMinimumRatings()));
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception occcured " + e.toString());
                } finally {
                    minRatingsTextView.setVisibility(View.VISIBLE);
                    autoCompleteTextView.setVisibility(View.VISIBLE);
                    selectedCuisinesLayout.setVisibility(View.VISIBLE);
                    hideProgressDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        preferencesDBRef.addValueEventListener(postListener);
    }
}

