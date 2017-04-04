package com.letmeeat.letmeeat.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letmeeat.letmeeat.R;
import com.letmeeat.letmeeat.db.RecosContract;
import com.letmeeat.letmeeat.models.Address;
import com.squareup.picasso.Picasso;

/**
 * Created by santhosh on 24/03/2017.
 * RecyclerView Adatper which is responsible for displaying the recommendation in a listview.
 */

public class RecosAdapter extends RecyclerView.Adapter<RecosAdapter.ViewHolder> {

    private Cursor cursor;
    private final Activity activity;
    private final OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        private final TextView name;
        private final TextView reviewsCount;
        private final TextView priceRange;
        private final TextView cuisine;
        private final TextView address;
        private final ImageView image;
        private final ImageView ratingStar1, ratingStar2, ratingStar3, ratingStar4, ratingStar5;

        public ViewHolder(LinearLayout v) {
            super(v);
            name = (TextView) v.findViewById(R.id.reco_name);
            image = (ImageView) v.findViewById(R.id.reco_image);
            priceRange = (TextView) v.findViewById(R.id.price_range);
            reviewsCount = (TextView) v.findViewById(R.id.reviews_count);
            ratingStar1 = (ImageView) v.findViewById(R.id.rating_star_1);
            ratingStar2 = (ImageView) v.findViewById(R.id.rating_star_2);
            ratingStar3 = (ImageView) v.findViewById(R.id.rating_star_3);
            ratingStar4 = (ImageView) v.findViewById(R.id.rating_star_4);
            ratingStar5 = (ImageView) v.findViewById(R.id.rating_star_5);
            cuisine = (TextView) v.findViewById(R.id.cuisine_type);
            address = (TextView) v.findViewById(R.id.address);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public RecosAdapter(Activity activity, Cursor cursor, OnItemClickListener itemClickListener) {
        this.activity = activity;
        this.itemClickListener = itemClickListener;
        this.cursor = cursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommendation_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        cursor.moveToPosition(position);

        holder.name.setText(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_NAME)));

        byte[] blob = cursor.getBlob(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_PICTURES));

        String pictureBlob = new String(blob);
        String[] pictures = pictureBlob.split(",");

        if (pictures[0] != null) {
            Picasso.with(activity).load(pictures[0])
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

        holder.priceRange.setText(activity.getString(R.string.pricerange, cursor.getInt(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_START_PRICE)), cursor.getInt(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_END_PRICE))));
        holder.cuisine.setText(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_CUISINE)));
        holder.address.setText(getPrintableAddress(cursor));

    }

    private String getPrintableAddress(Cursor cursor) {
        Address address = new Address();
        address.setStreetLine1(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_1)));
        address.setStreetLine2(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_2)));
        address.setCity(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_CITY)));
        address.setState(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_STATE)));
        address.setZip(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_ZIP)));
        address.setLandmark(cursor.getString(cursor.getColumnIndex(RecosContract.RecosEntry.COLUMN_LANDMARK)));
        return address.getPrintableAddress(null);
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }
}
