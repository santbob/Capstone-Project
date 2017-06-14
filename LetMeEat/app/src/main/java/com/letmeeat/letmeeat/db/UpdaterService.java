package com.letmeeat.letmeeat.db;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.letmeeat.letmeeat.helpers.Utils;
import com.letmeeat.letmeeat.models.RecoRequest;
import com.letmeeat.letmeeat.models.Recommendation;
import com.letmeeat.letmeeat.service.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by santhosh on 26/03/2017.
 * Service which takes care of downloading the data.
 */

public class UpdaterService extends IntentService {
    private static final String TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.letmeeat.letmeeat.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.letmeeat.letmeeat.intent.extra.REFRESHING";

    public UpdaterService() {
        super(TAG);
    }

    private ApiService apiService;
    // Don't even inspect the intent, we only do one thing, and that's fetch content.
    private final ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

    private final Uri dirUri = RecosContract.RecosEntry.CONTENT_URI;

    @Override
    protected void onHandleIntent(Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            return;
        }

        sendStickyBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(dirUri).build());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        getRecommendations();
    }

    private void getRecommendations() {
        RecoRequest recoRequest = new RecoRequest();
        recoRequest.setLocation("95131");
        recoRequest.setRadius(8047);
        recoRequest.setLimit(4);

        Call<List<Recommendation>> call = apiService.getRecommendations(recoRequest);

        call.enqueue(new Callback<List<Recommendation>>() {

            @Override
            public void onResponse(Call<List<Recommendation>> call, Response<List<Recommendation>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    List<Recommendation> recommendations = response.body();
                    Gson gson = new Gson();
                    if (recommendations != null && recommendations.size() > 0) {
                        for (int i = 0; i < recommendations.size(); i++) {
                            Recommendation reco = recommendations.get(i);
                            if (reco != null) {
                                ContentValues values = new ContentValues();
                                values.put(RecosContract.RecosEntry.COLUMN_RECO_ID, reco.getId());
                                values.put(RecosContract.RecosEntry.COLUMN_NAME, reco.getName());
                                values.put(RecosContract.RecosEntry.COLUMN_CATEGORIES, gson.toJson(reco.getCategories()).getBytes());
                                values.put(RecosContract.RecosEntry.COLUMN_REVIEWS_COUNT, reco.getReviewsCount());
                                values.put(RecosContract.RecosEntry.COLUMN_RATINGS, reco.getRating());
                                values.put(RecosContract.RecosEntry.COLUMN_PRICE_RANGE, reco.getPriceRange());
                                values.put(RecosContract.RecosEntry.COLUMN_CURRENCY, reco.getCurrency());
                                values.put(RecosContract.RecosEntry.COLUMN_PHONE, reco.getPhone());
                                values.put(RecosContract.RecosEntry.COLUMN_WEBSITE, reco.getWebsite());
                                values.put(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_1, reco.getAddress().getStreetLine1());
                                values.put(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_2, reco.getAddress().getStreetLine2());
                                values.put(RecosContract.RecosEntry.COLUMN_CITY, reco.getAddress().getCity());
                                values.put(RecosContract.RecosEntry.COLUMN_STATE, reco.getAddress().getState());
                                values.put(RecosContract.RecosEntry.COLUMN_ZIP, reco.getAddress().getZip());
                                values.put(RecosContract.RecosEntry.COLUMN_LANDMARK, reco.getAddress().getLandmark());
                                values.put(RecosContract.RecosEntry.COLUMN_DISPLAY_ADDRESS, reco.getAddress().getDisplayAddress());
                                values.put(RecosContract.RecosEntry.COLUMN_LAT_LONG, reco.getAddress().getCoordinates().getLatitude() + "," + reco.getAddress().getCoordinates().getLongitude());
                                values.put(RecosContract.RecosEntry.COLUMN_COUNTRY, (TextUtils.isEmpty(reco.getAddress().getCountry()) ? reco.getAddress().getCountry() : "US"));
                                values.put(RecosContract.RecosEntry.COLUMN_IMAGE_URL, reco.getImageUrl());
                                values.put(RecosContract.RecosEntry.COLUMN_PICTURES, getSpaceSepartedString(reco.getPhotos()));
                                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
                            }
                        }
                        try {
                            getContentResolver().applyBatch(RecosContract.CONTENT_AUTHORITY, cpo);
                        } catch (RemoteException | OperationApplicationException e) {
                            //do nothing
                        }
                    }
                }
                sendStickyBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
            }

            @Override
            public void onFailure(Call<List<Recommendation>> call, Throwable t) {
                Log.d(TAG, t.toString());
                sendStickyBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
            }
        });
    }

    private String getSpaceSepartedString(List<String> stringList) {
        StringBuilder builder = new StringBuilder();
        if (stringList != null && stringList.size() > 0) {
            for (String text : stringList) {
                if (!TextUtils.isEmpty(text)) {
                    builder.append(text);
                    builder.append(RecosContract.SPACE);
                }
            }
        }
        return builder.toString().trim();
    }
}