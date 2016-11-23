package com.letmeeat.letmeeat.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.letmeeat.letmeeat.BaseActivity;
import com.letmeeat.letmeeat.R;
import com.letmeeat.letmeeat.helpers.IntentHelper;
import com.letmeeat.letmeeat.models.Recommendation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Created by santhosh on 18/10/2016.
 * Recommendation Card Front Side View
 */

public class CardFrontView extends CardBaseView {

    private static final String COMMA = ",";
    private BaseActivity activity;
    private TextView cuisineName;
    private TextView recommendationName;

    private TextView reviewsCount;
    private TextView priceRange;
    private TextView address;
    private TextView phoneNumber;
    private TextView website;
    private ImageView loveIt;
    private IntentHelper intentHelper;

    public CardFrontView(Context context) {
        super(context);
    }

    public CardFrontView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardFrontView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CardFrontView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CardFrontView(BaseActivity context, final Recommendation recommendation, CardBaseView.CardInteractionListener listener) {
        super(context, recommendation, listener);
        this.activity = context;

        intentHelper = new IntentHelper(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_card_front_view, this, true);

        recommendationName = (TextView) findViewById(R.id.recommendation_name);
        cuisineName = (TextView) findViewById(R.id.cuisine_name);
        reviewsCount = (TextView) findViewById(R.id.reviews_count);
        priceRange = (TextView) findViewById(R.id.price_range);
        address = (TextView) findViewById(R.id.address);
        TextView directions = (TextView) findViewById(R.id.directions);
        directions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                intentHelper.sendMapIntent(recommendation.getAddress().getPrintableAddress(COMMA));
            }
        });
        phoneNumber = (TextView) findViewById(R.id.phone_number);
        website = (TextView) findViewById(R.id.website);
        website.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(recommendation.getWebsite())){
                    intentHelper.sendWebIntent(recommendation.getWebsite());
                }
            }
        });

        WebView mapView = (WebView) findViewById(R.id.map_view);
        mapView.loadUrl(activity.getString(R.string.static_map_url, urlEncode(recommendation.getAddress().getCityState()), urlEncode(recommendation.getAddress().getPrintableAddress(COMMA))));
        mapView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                intentHelper.sendMapIntent(recommendation.getAddress().getPrintableAddress(COMMA));
            }
        });

        loveIt = (ImageView) findViewById(R.id.love_it);
        loveIt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"1".equals(loveIt.getTag())) {
                    loveIt.setTag("1");
                    loveIt.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                } else {
                    loveIt.setTag("0");
                    loveIt.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
                }
            }
        });
        ImageView flipCard = (ImageView) findViewById(R.id.flip_card);
        flipCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getCardInteractionListener().flip(false);
            }
        });
        final ImageView thumbDown = (ImageView) findViewById(R.id.thumb_down);
        thumbDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //vote down
                if (!"1".equals(thumbDown.getTag())) {
                    thumbDown.setTag("1");
                    thumbDown.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                } else {
                    thumbDown.setTag("0");
                    thumbDown.setColorFilter(ContextCompat.getColor(getContext(), R.color.theme_200));
                }
            }
        });
        final ImageView thumbUp = (ImageView) findViewById(R.id.thumb_up);
        thumbUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"1".equals(thumbUp.getTag())) {
                    thumbUp.setTag("1");
                    thumbUp.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                } else {
                    thumbUp.setTag("0");
                    thumbUp.setColorFilter(ContextCompat.getColor(getContext(), R.color.theme_200));
                }
            }
        });

        updateUI(recommendation);
    }

    public void updateUI(Recommendation recommendation) {
        recommendationName.setText(recommendation.getName());
        cuisineName.setText(recommendation.getCuisine());
        reviewsCount.setText(getContext().getString(R.string.reviews, recommendation.getReviewsCount()));
        priceRange.setText(getContext().getString(R.string.price_range, recommendation.getStartPrice(), recommendation.getEndPrice()));
        address.setText(recommendation.getAddress().getPrintableAddress(com.letmeeat.letmeeat.models.Address.SPACE));
        phoneNumber.setText(recommendation.getPhone());
        website.setText(recommendation.getWebsite());
    }

    public String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("failed to encode", e);
        }
    }
}


