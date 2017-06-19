package com.letmeeat.letmeeat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.letmeeat.letmeeat.helpers.Utils;
import com.letmeeat.letmeeat.models.Category;
import com.letmeeat.letmeeat.models.Preferences;
import com.letmeeat.letmeeat.service.ApiService;
import com.letmeeat.letmeeat.views.TagView;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by santhosh on 20/10/2016.
 * Recommendations in LetMeEat are based on the Preferences set in this activity.
 */

public class PreferencesActivity extends BaseActivity implements TagView.TagViewListener {

    private final String TAG = getClass().getSimpleName();

    private List<Category> autoCompleteCategoriesList;
    private DatabaseReference preferencesDBRef;
    private Map<String, Category> categoryMap = new HashMap<String, Category>();
    private ArrayAdapter<Category> adapter;


    private EditText minRatingsTextView;
    private AutoCompleteTextView autoCompleteTextView;
    private FlexboxLayout selectedCuisinesLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getCategories();
        firebaseAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        preferencesDBRef = database.getReference("preferences");
        //preferencesModel = new Preferences();

        minRatingsTextView = (EditText) findViewById(R.id.pref_minimum_ratings);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.pref_cuisine_lookup);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                if (adapter != null) {
                    Category category = adapter.getItem(pos);
                    if (category != null) {
                        addCuisine2Preference(category);
                        autoCompleteTextView.setText("");
                    }
                }
            }
        });

        selectedCuisinesLayout = (FlexboxLayout) findViewById(R.id.selected_cuisines);

        Button savePreferences = (Button) findViewById(R.id.pref_save);
        savePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save preferences
                savePreferences();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
        hideProgressDialog();
    }

    private void getCategories() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<Category>> call = apiService.getCategories("US");
        call.enqueue(new Callback<List<Category>>() {

            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    autoCompleteCategoriesList = response.body();
                    adapter = new ArrayAdapter<Category>(PreferencesActivity.this,
                            android.R.layout.simple_dropdown_item_1line, autoCompleteCategoriesList);
                    autoCompleteTextView.setAdapter(adapter);
                    autoCompleteTextView.setThreshold(0);
                }
                loadStoredPreferences();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.d(TAG, t.toString());
                loadStoredPreferences();
            }
        });
    }

    private void addCategoryTagView(Category category) {
        TagView tagView = new TagView(this);
        tagView.build(category.getTitle(), category.getAlias(), this);
        selectedCuisinesLayout.addView(tagView);
    }

    private void savePreferences() {
        float minRatings = 0;
        try {
            minRatings = Float.valueOf(minRatingsTextView.getText().toString());
        } catch (NumberFormatException e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    PreferencesActivity.this);
            alertDialogBuilder.setTitle(R.string.error);
            alertDialogBuilder.setMessage(R.string.invalid_min_rating);
            return;
        }


        Preferences preferencesModel = new Preferences();
        preferencesModel.setMinimumRatings(minRatings);
        if (categoryMap.size() > 0) {
            preferencesModel.setCategories(categoryMap);
        }

        if (firebaseAuth.getCurrentUser() != null) {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<Preferences> jsonAdapter = moshi.adapter(Preferences.class);
            String json = jsonAdapter.toJson(preferencesModel);
            preferencesDBRef.setValue(json);
        }

        storePreferences(preferencesModel);
        Utils.setSharedPrefBoolean(getApplicationContext(), Utils.PREF_MODIFIED, true);
        onBackPressed();
    }

    private void addCuisine2Preference(Category category) {
        if (!categoryMap.containsKey(category.getAlias())) {
            categoryMap.put(category.getAlias(), category);
            addCategoryTagView(category);
        }
    }

    @Override
    public void onTagDelete(TagView view) {
        if (categoryMap.containsKey(view.getValue())) {
            categoryMap.remove(view.getValue());
            selectedCuisinesLayout.removeView(view);
        }
    }

    private void getStoredPreferencesFromFirebase() {
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
                        Preferences preferencesModel = jsonAdapter.fromJson(prefJson);
                        updatePreferenceUI(preferencesModel);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception occcured " + e.toString());
                } finally {
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

    private void loadStoredPreferences() {
        if (firebaseAuth.getCurrentUser() != null) {
            getStoredPreferencesFromFirebase();
        } else {
            getStoredPreferences();
        }
    }

    private void updatePreferenceUI(Preferences preferencesModel) {
        if (preferencesModel != null) {
            if (preferencesModel.getCategories() != null) {
                for (Category category : preferencesModel.getCategories().values()) {
                    addCuisine2Preference(category);
                }
            }
            if (preferencesModel.getMinimumRatings() > 0) {
                minRatingsTextView.setText(getString(R.string.float_placeholder, preferencesModel.getMinimumRatings()));
            }
        }
    }

    private void storePreferences(Preferences preferences) {
        Utils.setSharedPrefString(getApplicationContext(), Utils.CATEGORIES, preferences.getCategoriesAlias());
        Utils.setSharedPrefFloat(getApplicationContext(), Utils.MIN_RATINGS, preferences.getMinimumRatings());
    }

    private void getStoredPreferences() {
        Preferences preferences = new Preferences();
        preferences.setMinimumRatings(Utils.getSharedPrefFloat(getApplicationContext(), Utils.MIN_RATINGS));
        String cats = Utils.getSharedPrefString(getApplicationContext(), Utils.CATEGORIES);
        Map<String, Category> catsMap = new HashMap<String, Category>();
        for (Category cat : autoCompleteCategoriesList) {
            if (cats.contains(cat.getAlias())) {
                catsMap.put(cat.getAlias(), cat);
            }
        }
        preferences.setCategories(catsMap);
        updatePreferenceUI(preferences);
    }
}

