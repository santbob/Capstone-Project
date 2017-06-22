package com.letmeeat.letmeeat.widgets;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.letmeeat.letmeeat.R;
import com.letmeeat.letmeeat.db.RecosContract;
import com.letmeeat.letmeeat.models.Category;

import java.util.ArrayList;

/**
 * Created by santhosh on 21/06/2017.
 * RemoteViewService to update the RemoveViews of the Widget with Recommendations
 */

public class WidgetRemoteViewsService extends RemoteViewsService {

    private final Gson gson = new Gson();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor cursor = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (cursor != null) {
                    cursor.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                cursor = getContentResolver().query(RecosContract.RecosEntry.CONTENT_URI, null, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }

                String recoName = cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_NAME));
                String imageUrl = cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_IMAGE_URL));
                String reviewCount = getString(R.string.reviews_count, cursor.getInt(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_REVIEWS_COUNT)));
                float ratings = cursor.getFloat(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_RATINGS));

                int star1ResId = ratings >= 1 ? R.drawable.star : (ratings < 1 && ratings > 0) ? R.drawable.star_half : R.drawable.star_outline;
                int star2ResId = ratings >= 2 ? R.drawable.star : (ratings < 2 && ratings > 1) ? R.drawable.star_half : R.drawable.star_outline;
                int star3ResId = ratings >= 3 ? R.drawable.star : (ratings < 3 && ratings > 2) ? R.drawable.star_half : R.drawable.star_outline;
                int star4ResId = ratings >= 4 ? R.drawable.star : (ratings < 4 && ratings > 3) ? R.drawable.star_half : R.drawable.star_outline;
                int star5ResId = ratings == 5 ? R.drawable.star : (ratings < 5 && ratings > 4) ? R.drawable.star_half : R.drawable.star_outline;

                String priceRange = cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_PRICE_RANGE));

                byte[] jsonBytes = cursor.getBlob(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_CATEGORIES));
                String jsonStr = new String(jsonBytes);
                ArrayList<Category> categories = gson.fromJson(jsonStr, new TypeToken<ArrayList<Category>>() {
                }.getType());
                String cuisines = null;
                if (categories != null && categories.size() > 0) {
                    cuisines = categories.toString();
                    cuisines = cuisines.substring(1, cuisines.length() - 1);
                }
                String displayAddress = cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_DISPLAY_ADDRESS));

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_reco_list_item);

                views.setTextViewText(R.id.reco_name, recoName);
                views.setTextViewText(R.id.reviews_count, reviewCount);
                views.setTextViewText(R.id.price_range, priceRange);
                views.setTextViewText(R.id.cuisine_type, cuisines);
                views.setTextViewText(R.id.address, displayAddress);

                views.setImageViewResource(R.id.rating_star_1, star1ResId);
                views.setImageViewResource(R.id.rating_star_2, star2ResId);
                views.setImageViewResource(R.id.rating_star_3, star3ResId);
                views.setImageViewResource(R.id.rating_star_4, star4ResId);
                views.setImageViewResource(R.id.rating_star_5, star5ResId);

                if (position % 2 == 1) {
                    views.setInt(R.id.widget_list_item, "setBackgroundResource", R.color.blue_grey);
                } else {
                    views.setInt(R.id.widget_list_item, "setBackgroundResource", R.color.white);
                }

                // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
                Bundle extras = new Bundle();
                extras.putLong(RecosContract.RecosEntry._ID, cursor.getLong(cursor.getColumnIndex(RecosContract.RecosEntry._ID)));
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_reco_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (cursor.moveToPosition(position)) {
                    return cursor.getLong(cursor.getColumnIndex(RecosContract.RecosEntry._ID));
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
