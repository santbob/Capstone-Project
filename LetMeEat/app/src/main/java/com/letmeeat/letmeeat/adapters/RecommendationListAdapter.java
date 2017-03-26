package com.letmeeat.letmeeat.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letmeeat.letmeeat.R;
import com.letmeeat.letmeeat.models.Recommendation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh on 24/03/2017.
 * RecyclerView Adatper which is responsible for displaying the recommendation in a listview.
 */

public class RecommendationListAdapter extends RecyclerView.Adapter<RecommendationListAdapter.ViewHolder> {

    private List<Recommendation> recommendations;
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
                Recommendation recommendation = recommendations.get(getAdapterPosition());
                if (recommendation != null) {
                    itemClickListener.onItemClick(view, getAdapterPosition());
                }
            }
        }
    }

    public RecommendationListAdapter(Activity activity, OnItemClickListener itemClickListener) {
        this.activity = activity;
        this.itemClickListener = itemClickListener;
        this.recommendations = new ArrayList<Recommendation>();
    }

    public void updateData(List<Recommendation> recos) {
        if (this.recommendations == null) {
            this.recommendations = new ArrayList<Recommendation>();
        } else {
            this.recommendations.clear();
        }
        this.recommendations.addAll(recos);
        notifyDataSetChanged();
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
        Recommendation reco = recommendations.get(position);
        if (reco != null) {
            holder.name.setText(reco.getName());
            if (reco.getPhotos() != null && reco.getPhotos().get(0) != null) {
                Picasso.with(activity).load(reco.getPhotos().get(0))
                        .resize(200, 200)
                        .centerCrop()
                        .into(holder.image);
            }
            holder.reviewsCount.setText(activity.getString(R.string.reviews_count, reco.getReviewsCount()));

            float ratings = reco.getRating();
            holder.ratingStar1.setImageResource(ratings >= 1 ? R.drawable.star : (ratings < 1 && ratings > 0) ? R.drawable.star_half : R.drawable.star_outline);
            holder.ratingStar2.setImageResource(ratings >= 2 ? R.drawable.star : (ratings < 2 && ratings > 1) ? R.drawable.star_half : R.drawable.star_outline);
            holder.ratingStar3.setImageResource(ratings >= 3 ? R.drawable.star : (ratings < 3 && ratings > 2) ? R.drawable.star_half : R.drawable.star_outline);
            holder.ratingStar4.setImageResource(ratings >= 4 ? R.drawable.star : (ratings < 4 && ratings > 3) ? R.drawable.star_half : R.drawable.star_outline);
            holder.ratingStar5.setImageResource(ratings == 5 ? R.drawable.star : (ratings < 5 && ratings > 4) ? R.drawable.star_half : R.drawable.star_outline);

            holder.priceRange.setText(activity.getString(R.string.pricerange, reco.getStartPrice(), reco.getEndPrice()));
            holder.cuisine.setText(reco.getCuisine());
            holder.address.setText(reco.getAddress().getPrintableAddress(" "));
        }
    }


    @Override
    public int getItemCount() {
        return recommendations.size();
    }
}
