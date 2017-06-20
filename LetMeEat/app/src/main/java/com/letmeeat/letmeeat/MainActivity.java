package com.letmeeat.letmeeat;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.letmeeat.letmeeat.adapters.RecosAdapter;
import com.letmeeat.letmeeat.db.RecosContract;
import com.letmeeat.letmeeat.db.UpdaterService;
import com.letmeeat.letmeeat.helpers.CircleTransform;
import com.letmeeat.letmeeat.helpers.LocationHelper;
import com.letmeeat.letmeeat.helpers.Utils;
import com.letmeeat.letmeeat.loaders.RecosLoader;
import com.squareup.picasso.Picasso;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = getClass().getSimpleName();

    private final static int REQUEST_APP_SETTINGS_FOR_LOCATION = 10;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 20;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recomendationsListView;
    private LinearLayout noRecommendationsLayout;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    private LinearLayout loggedInStateLayout;
    private ImageView profileImage;
    private TextView profileInfo;

    private LinearLayout guestStateLayout;
    private LoginButton fbLoginButton;

    private AlertDialog permissionConfirmDialog;
    private LocationHelper locationHelper;

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
                handleLoginState(firebaseAuth);
            }
        };

        guestStateLayout = (LinearLayout) findViewById(R.id.guest_state_layout);
        fbLoginButton = (LoginButton) findViewById(R.id.login_button);
        fbLoginButton.setReadPermissions("email", "public_profile");
        // Callback registration
        fbLoginButton.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "fb cancel callback");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d(TAG, "fb exception callback");
                    }
                }

        );

        loggedInStateLayout = (LinearLayout) findViewById(R.id.loggedin_state_layout);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileInfo = (TextView) findViewById(R.id.profile_info);
        TextView logoutText = (TextView) findViewById(R.id.logout);
        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLoginButton.performClick();
                firebaseAuth.signOut();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recomendationsListView = (RecyclerView) findViewById(R.id.reco_list_view);
        recomendationsListView.setHasFixedSize(true);
        recomendationsListView.setLayoutManager(new LinearLayoutManager(this));
        noRecommendationsLayout = (LinearLayout) findViewById(R.id.no_recommendations);
        getLoaderManager().initLoader(0, null, MainActivity.this);

        if (locationHelper == null) {
            locationHelper = new LocationHelper(this);
        }

        if (savedInstanceState == null) {
            refresh();
        }
        handleLoginState(firebaseAuth);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.getSharedPrefBoolean(getApplicationContext(), Utils.PREF_MODIFIED)) {
            refresh();
            Utils.setSharedPrefBoolean(getApplicationContext(), Utils.PREF_MODIFIED, false);
        }
    }

    private boolean mIsRefreshing = false;

    private final BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
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

    private void handleLoginState(FirebaseAuth fbaseAuth) {
        FirebaseUser user = fbaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            if (user.getPhotoUrl() != null) {
                Picasso.with(MainActivity.this).load(user.getPhotoUrl())
                        .resize(200, 200)
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.ic_account_circle)
                        .centerCrop()
                        .into(profileImage);
            }
            if (user.getDisplayName() != null) {
                profileInfo.setText(user.getDisplayName());
            }
            guestStateLayout.setVisibility(View.GONE);
            loggedInStateLayout.setVisibility(View.VISIBLE);
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
            guestStateLayout.setVisibility(View.VISIBLE);
            loggedInStateLayout.setVisibility(View.GONE);
        }
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
                    }
                });
    }

    private void refresh() {
        recomendationsListView.setAdapter(null);
        MainActivityPermissionsDispatcher.getLocationFromGPSWithCheck(MainActivity.this);
    }

    private void showNoRecommendation() {
        Log.d("Santhosh", "showNoRecommendation called");
//        recomendationsListView.setAdapter(null);
//        recomendationsListView.setVisibility(View.GONE);
//        noRecommendationsLayout.setVisibility(View.VISIBLE);
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
        } else if (id == R.id.action_refresh) {
            refresh();
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
        final RecosAdapter adapter = new RecosAdapter(this, cursor, new RecosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(long itemId) {
                startActivity(new Intent(Intent.ACTION_VIEW, RecosContract.RecosEntry.buildItemUri(itemId)));
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

    @SuppressWarnings("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void getLocationFromGPS() {
        if (!Utils.isGPSEnabled(this)) {
            handleNoLocationPermissionDialog(R.string.permission_needed, R.string.location_permission_denied_gps_off);
        } else {
            locationHelper.getLocation(new LocationHelper.LocationHelperListener() {
                @Override
                public void onLocationIdentified(Location location) {
                    if (location != null) {
                        Utils.setSharedPrefString(getApplicationContext(), Utils.LOCATION, (location.getLatitude() + "," + location.getLongitude()));
                        startService(new Intent(MainActivity.this, UpdaterService.class));
                    }
                }
            });
        }
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForLocationPermission(final PermissionRequest request) {

    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForLocationPermission() {
        handleNoLocationPermissionDialog(R.string.permission_needed, R.string.location_permission_denied_for_app);
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForLocationPermission() {
        handleNoLocationPermissionDialog(R.string.permission_needed, R.string.location_permission_denied_permanently);
    }

    private void handleNoLocationPermissionDialog(int titleResId, final int messageResId) {
        destroyPermissionConfirmDialog();
        permissionConfirmDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (messageResId == R.string.location_permission_denied_gps_off) {
                            goToLocationSettings();
                        } else {
                            goToSettings(REQUEST_APP_SETTINGS_FOR_LOCATION);
                        }
                    }

                })
                .setNegativeButton(R.string.no, null)
                .create();

        permissionConfirmDialog.show();
        showNoRecommendation();
    }

    private void destroyPermissionConfirmDialog() {
        if (permissionConfirmDialog != null) {
            permissionConfirmDialog.dismiss();
            permissionConfirmDialog = null;
        }
    }

    private void goToSettings(int requestCode) {
        try {
            Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
            myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(myAppSettings, requestCode);
        } catch (ActivityNotFoundException e) {
            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);
        }
    }

    private void goToLocationSettings() {
        Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        if (viewIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(viewIntent, REQUEST_APP_SETTINGS_FOR_LOCATION);
        } else {
            new AlertDialog.Builder(MainActivity.this).setMessage(R.string.location_permission_denied_gps_off).setTitle(R.string.error).create().show();
        }
    }
}
