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

    @Override
    protected void onHandleIntent(Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            return;
        }

        sendStickyBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        // Don't even inspect the intent, we only do one thing, and that's fetch content.
        final ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        final Uri dirUri = RecosContract.RecosEntry.CONTENT_URI;

        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(dirUri).build());


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.myjson.com/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<List<Recommendation>> call = service.getRecommendations();

        call.enqueue(new Callback<List<Recommendation>>() {
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

            @Override
            public void onResponse(Call<List<Recommendation>> call, Response<List<Recommendation>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    List<Recommendation> recommendations = response.body();
                    if (recommendations != null && recommendations.size() > 0) {
                        for (int i = 0; i < recommendations.size(); i++) {
                            Recommendation reco = recommendations.get(i);
                            ContentValues values = new ContentValues();
                            values.put(RecosContract.RecosEntry.COLUMN_RECO_ID, "lme_" + i);
                            values.put(RecosContract.RecosEntry.COLUMN_NAME, reco.getName());
                            values.put(RecosContract.RecosEntry.COLUMN_CUISINE, reco.getCuisine());
                            values.put(RecosContract.RecosEntry.COLUMN_REVIEWS_COUNT, reco.getReviewsCount());
                            values.put(RecosContract.RecosEntry.COLUMN_RATINGS, reco.getRating());
                            values.put(RecosContract.RecosEntry.COLUMN_START_PRICE, reco.getStartPrice());
                            values.put(RecosContract.RecosEntry.COLUMN_END_PRICE, reco.getEndPrice());
                            values.put(RecosContract.RecosEntry.COLUMN_CURRENCY, reco.getCurrency());
                            values.put(RecosContract.RecosEntry.COLUMN_PHONE, reco.getPhone());
                            values.put(RecosContract.RecosEntry.COLUMN_WEBSITE, reco.getWebsite());
                            values.put(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_1, reco.getAddress().getStreetLine1());
                            values.put(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_2, reco.getAddress().getStreetLine2());
                            values.put(RecosContract.RecosEntry.COLUMN_CITY, reco.getAddress().getCity());
                            values.put(RecosContract.RecosEntry.COLUMN_STATE, reco.getAddress().getState());
                            values.put(RecosContract.RecosEntry.COLUMN_ZIP, reco.getAddress().getZip());
                            values.put(RecosContract.RecosEntry.COLUMN_LANDMARK, reco.getAddress().getLandmark());
                            values.put(RecosContract.RecosEntry.COLUMN_COUNTRY, "USA");
                            values.put(RecosContract.RecosEntry.COLUMN_PICTURES, getSpaceSepartedString(reco.getPhotos()));
                            cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
                        }
                        try {
                            getContentResolver().applyBatch(RecosContract.CONTENT_AUTHORITY, cpo);
                        } catch (RemoteException | OperationApplicationException e) {

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
}