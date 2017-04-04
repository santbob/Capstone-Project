package com.letmeeat.letmeeat;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.letmeeat.letmeeat.adapters.RecommendationListAdapter;
import com.letmeeat.letmeeat.adapters.RecosAdapter;
import com.letmeeat.letmeeat.db.UpdaterService;
import com.letmeeat.letmeeat.loaders.RecosLoader;
import com.letmeeat.letmeeat.models.Recommendation;
import com.letmeeat.letmeeat.service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = getClass().getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recomendationsListView;
    private RecommendationListAdapter recommendationListAdapter;
    private LinearLayout noRecommendationsLayout;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    //FB login callbackManager
    private CallbackManager fbCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

//        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.login_button);
//        fbLoginButton.setReadPermissions("email", "public_profile");
//
//        // Callback registration
//        fbLoginButton.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        handleFacebookAccessToken(loginResult.getAccessToken());
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Log.d(TAG, "fb cancel callback");
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                        Log.d(TAG, "fb exception callback");
//                    }
//                }
//
//        );

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        recomendationsListView = (RecyclerView) findViewById(R.id.reco_list_view);
        recomendationsListView.setHasFixedSize(true);
        recomendationsListView.setLayoutManager(new LinearLayoutManager(this));
//        recommendationListAdapter = new RecommendationListAdapter(this, new RecommendationListAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent detailsIntent = new Intent(MainActivity.this, RecommendationDetailsActivity.class);
//                //detailsIntent.putParcelableArrayListExtra("recos", recommendationListAdapter.getRecommendations());
//                detailsIntent.putExtra("currentIndex", position);
//                startActivity(detailsIntent);
//            }
//        });
//        recomendationsListView.setAdapter(recommendationListAdapter);
        noRecommendationsLayout = (LinearLayout) findViewById(R.id.no_recommendations);
        getLoaderManager().initLoader(0, null, MainActivity.this);

        if (savedInstanceState == null) {
            refresh();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver, new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }

    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() {
        swipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    private void getData() {
        //json is store in the url https://api.myjson.com/bins/4vp7g for testing
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.myjson.com/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<List<Recommendation>> call = service.getRecommendations();

        call.enqueue(new Callback<List<Recommendation>>() {
            @Override
            public void onResponse(Call<List<Recommendation>> call, Response<List<Recommendation>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.body() != null && response.body().size() > 0) {
                    List<Recommendation> recommendations = response.body();
                    recomendationsListView.setVisibility(View.VISIBLE);
                    noRecommendationsLayout.setVisibility(View.GONE);
                    recommendationListAdapter.updateData(recommendations);

                } else {
                    recomendationsListView.setVisibility(View.GONE);
                    noRecommendationsLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Recommendation>> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent prefIntent = new Intent(this, PreferencesActivity.class);
            startActivity(prefIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return RecosLoader.newAllRecosInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        RecosAdapter adapter = new RecosAdapter(this, cursor, new RecosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent detailsIntent = new Intent(MainActivity.this, RecommendationDetailsActivity.class);
                detailsIntent.putExtra("currentIndex", position);
                startActivity(detailsIntent);
            }
        });
        adapter.setHasStableIds(true);
        recomendationsListView.setVisibility(View.VISIBLE);
        noRecommendationsLayout.setVisibility(View.GONE);
        recomendationsListView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recomendationsListView.setAdapter(null);
    }
}
