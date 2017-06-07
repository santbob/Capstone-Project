package com.letmeeat.letmeeat.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.letmeeat.letmeeat.R;
import com.letmeeat.letmeeat.db.RecosContract;
import com.letmeeat.letmeeat.helpers.IntentHelper;
import com.letmeeat.letmeeat.models.Address;
import com.letmeeat.letmeeat.models.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by santhosh on 24/03/2017.
 * RecyclerView Adatper which is responsible for displaying the recommendation in a listview.
 */

public class RecosAdapter extends RecyclerView.Adapter<RecosAdapter.ViewHolder> {

    private final Cursor cursor;
    private final Activity activity;
    private final OnItemClickListener itemClickListener;
    private Gson gson = new Gson();

    private final IntentHelper intentHelper;

    public interface OnItemClickListener {
        void onItemClick(long itemId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        private final TextView name;
        private final TextView reviewsCount;
        private final TextView priceRange;
        private final TextView dotSeparator;
        private final TextView cuisine;
        private final TextView address;
        private final ImageView image;
        private final ImageView ratingStar1, ratingStar2, ratingStar3, ratingStar4, ratingStar5, phoneIcon, linkIcon, directionsIcon;

        public ViewHolder(LinearLayout v) {
            super(v);
            name = (TextView) v.findViewById(R.id.reco_name);
            image = (ImageView) v.findViewById(R.id.reco_image);
            priceRange = (TextView) v.findViewById(R.id.price_range);
            dotSeparator = (TextView) v.findViewById(R.id.dot_separator);
            reviewsCount = (TextView) v.findViewById(R.id.reviews_count);
            ratingStar1 = (ImageView) v.findViewById(R.id.rating_star_1);
            ratingStar2 = (ImageView) v.findViewById(R.id.rating_star_2);
            ratingStar3 = (ImageView) v.findViewById(R.id.rating_star_3);
            ratingStar4 = (ImageView) v.findViewById(R.id.rating_star_4);
            ratingStar5 = (ImageView) v.findViewById(R.id.rating_star_5);
            cuisine = (TextView) v.findViewById(R.id.cuisine_type);
            address = (TextView) v.findViewById(R.id.address);
            phoneIcon = (ImageView) v.findViewById(R.id.phone);
            linkIcon = (ImageView) v.findViewById(R.id.link);
            directionsIcon = (ImageView) v.findViewById(R.id.directions);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(RecosAdapter.this.getItemId(getAdapterPosition()));
            }
        }
    }

    public RecosAdapter(Activity activity, Cursor cursor, OnItemClickListener itemClickListener) {
        this.activity = activity;
        this.itemClickListener = itemClickListener;
        this.cursor = cursor;
        this.intentHelper = new IntentHelper(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reco_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        cursor.moveToPosition(position);

        holder.name.setText(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_NAME)));

        String imageUrl = cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_IMAGE_URL));
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(activity).load(imageUrl)
                    .resize(200, 200)
                    .centerCrop()
                    .into(holder.image);
        }

        holder.reviewsCount.setText(activity.getString(R.string.reviews_count, cursor.getInt(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_REVIEWS_COUNT))));

        float ratings = cursor.getFloat(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_RATINGS));
        holder.ratingStar1.setImageResource(ratings >= 1 ? R.drawable.star : (ratings < 1 && ratings > 0) ? R.drawable.star_half : R.drawable.star_outline);
        holder.ratingStar2.setImageResource(ratings >= 2 ? R.drawable.star : (ratings < 2 && ratings > 1) ? R.drawable.star_half : R.drawable.star_outline);
        holder.ratingStar3.setImageResource(ratings >= 3 ? R.drawable.star : (ratings < 3 && ratings > 2) ? R.drawable.star_half : R.drawable.star_outline);
        holder.ratingStar4.setImageResource(ratings >= 4 ? R.drawable.star : (ratings < 4 && ratings > 3) ? R.drawable.star_half : R.drawable.star_outline);
        holder.ratingStar5.setImageResource(ratings == 5 ? R.drawable.star : (ratings < 5 && ratings > 4) ? R.drawable.star_half : R.drawable.star_outline);

        String priceRange = cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_PRICE_RANGE));
        if (!TextUtils.isEmpty(priceRange)) {
            holder.priceRange.setText(priceRange);
            holder.dotSeparator.setVisibility(View.VISIBLE);
        } else {
            holder.priceRange.setVisibility(View.GONE);
            holder.dotSeparator.setVisibility(View.GONE);
        }

        byte[] jsonBytes = cursor.getBlob(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_CATEGORIES));
        String jsonStr = new String(jsonBytes);
        ArrayList<Category> categories = gson.fromJson(jsonStr, new TypeToken<ArrayList<Category>>() {
        }.getType());
        String cuisines = null;
        if (categories != null && categories.size() > 0) {
            cuisines = categories.toString();
            cuisines = cuisines.substring(1, cuisines.length() - 1);
        }
        if (cuisines != null) {
            holder.cuisine.setText(cuisines);
        }

        final Address address = getPrintableAddress(cursor);
        holder.address.setText(address.getPrintableAddress(null));

        holder.directionsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentHelper.sendMapIntent(address.getPrintableAddress(Address.COMMA));
            }
        });

        final String phoneNumber = cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_PHONE));
        if (TextUtils.isEmpty(phoneNumber)) {
            holder.phoneIcon.setVisibility(View.GONE);
        } else {
            holder.phoneIcon.setVisibility(View.VISIBLE);
            holder.phoneIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentHelper.sendDialIntent(phoneNumber);
                }
            });
        }

        final String link = cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_WEBSITE));
        if (TextUtils.isEmpty(phoneNumber)) {
            holder.linkIcon.setVisibility(View.GONE);
        } else {
            holder.linkIcon.setVisibility(View.VISIBLE);
            holder.linkIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentHelper.sendWebIntent(link);
                }
            });
        }
    }

    private Address getPrintableAddress(Cursor cursor) {
        Address address = new Address();
        address.setStreetLine1(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_1)));
        address.setStreetLine2(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_2)));
        address.setCity(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_CITY)));
        address.setState(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_STATE)));
        address.setZip(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ZIP)));
        address.setLandmark(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_LANDMARK)));
        return address;
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(cursor.getColumnIndex(RecosContract.RecosEntry._ID));
    }
}
