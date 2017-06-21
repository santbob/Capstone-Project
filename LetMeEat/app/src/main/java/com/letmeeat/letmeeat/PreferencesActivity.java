package com.letmeeat.letmeeat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.letmeeat.letmeeat.helpers.PreferencesHelper;
import com.letmeeat.letmeeat.helpers.Utils;
import com.letmeeat.letmeeat.models.Category;
import com.letmeeat.letmeeat.models.Preferences;
import com.letmeeat.letmeeat.service.ApiService;
import com.letmeeat.letmeeat.views.TagView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final Set<String> categorySet = new HashSet<>();
    private ArrayAdapter<Category> adapter;


    private EditText minRatingsTextView;
    private AutoCompleteTextView autoCompleteTextView;
    private FlexboxLayout selectedCuisinesLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };
        preferencesHelper = new PreferencesHelper(new PreferencesHelper.PreferencesListener() {
            @Override
            public void onPreferencesLoaded(Preferences preferences) {
                hideProgressDialog();
                updatePreferenceUI(preferences);
            }
        });

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
        getCategories();
        loadStoredPreferences();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.prefs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            savePreferences();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                    adapter = new ArrayAdapter<>(PreferencesActivity.this,
                            android.R.layout.simple_dropdown_item_1line, autoCompleteCategoriesList);
                    autoCompleteTextView.setAdapter(adapter);
                    autoCompleteTextView.setThreshold(0);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }

    private void addCategoryTagView(Category category) {
        TagView tagView = new TagView(this);
        tagView.build(category.getTitle(), category.getAlias(), this);
        selectedCuisinesLayout.addView(tagView);
    }

    private void savePreferences() {
        float minRatings;
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
        preferencesModel.setCategories(categorySet);
        preferencesModel.setChoosenRecos(Utils.getSharedPrefStringSet(getApplicationContext(), Utils.RECOS_CHOOSEN_IN_PAST));
        preferencesHelper.writePreferences(preferencesModel, firebaseAuth.getCurrentUser() != null);

        Utils.setSharedPrefBoolean(getApplicationContext(), Utils.PREF_MODIFIED, true);
        onBackPressed();
    }

    private void addCuisine2Preference(Category category) {
        if (!categorySet.contains(category.getAlias())) {
            categorySet.add(category.getAlias());
            addCategoryTagView(category);
        }
    }

    @Override
    public void onTagDelete(TagView view) {
        if (categorySet.contains(view.getValue())) {
            categorySet.remove(view.getValue());
            selectedCuisinesLayout.removeView(view);
        }
    }

    private void loadStoredPreferences() {
        showProgressDialog(null, null);
        preferencesHelper.readStoredPreferences(firebaseAuth.getCurrentUser() != null);
    }

    private void updatePreferenceUI(Preferences preferencesModel) {
        if (preferencesModel != null) {
            if (preferencesModel.getCategories() != null) {
                String commaSeparatedCat = Utils.getCommaSeparatedStringOfSet(preferencesModel.getCategories());
                for (Category category : autoCompleteCategoriesList) {
                    if (commaSeparatedCat.contains(category.getAlias())) {
                        addCuisine2Preference(category);
                    }
                }
            }
            if (preferencesModel.getMinimumRatings() > 0) {
                minRatingsTextView.setText(getString(R.string.float_placeholder, preferencesModel.getMinimumRatings()));
            }
        }
    }
}

