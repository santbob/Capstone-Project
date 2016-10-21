package com.letmeeat.letmeeat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by santhosh on 20/10/2016.
 * Recommendations in LetMeEat are based on the Preferences set in this activity.
 */

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference preferencesRef = database.getReference("preferences");
    }
}

