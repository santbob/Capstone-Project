package com.letmeeat.letmeeat.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.letmeeat.letmeeat.R;
import com.letmeeat.letmeeat.models.Recommendation;

/**
 * Created by santhosh on 18/10/2016.
 * Recommendation Card Front Side View
 */

public class CardFrontView extends CardBaseView {

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
    public CardFrontView(Context context, Recommendation recommendation, CardBaseView.CardInteractionListener listener) {
        super(context, recommendation, listener);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_card_front_view, this, true);


    }
}
