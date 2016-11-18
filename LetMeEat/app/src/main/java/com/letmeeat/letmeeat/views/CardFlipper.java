package com.letmeeat.letmeeat.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.letmeeat.letmeeat.models.Recommendation;

/**
 * Created by santhosh on 18/10/2016.
 * Custom CardView Class which acts as Container for Front and Back View of the Card.
 */

public class CardFlipper extends RelativeLayout implements CardBaseView.CardInteractionListener{

    private CardFrontView frontView;
    private CardBackView backView;

    private Recommendation recommendation;

    public CardFlipper(Context context) {
        super(context);
    }

    public CardFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardFlipper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CardFlipper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CardFlipper(Context context, Recommendation recommendation) {
        super(context);
        this.recommendation = recommendation;
        init();
    }

    private void init() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(CENTER_VERTICAL);

        frontView = new CardFrontView(getContext(), recommendation, this);
        this.addView(frontView, layoutParams);

        backView = new CardBackView(getContext(), recommendation, this);
        backView.setVisibility(View.INVISIBLE);
        backView.setRotationY(-90);
        this.addView(backView, layoutParams);
    }

    @Override
    public void flip(boolean isBack) {

    }
}
