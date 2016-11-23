package com.letmeeat.letmeeat.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.letmeeat.letmeeat.models.Recommendation;

/**
 * Created by santhosh on 18/10/2016.
 * Base Custom View for the Card.
 */

public abstract class CardBaseView extends LinearLayout {

    public interface CardInteractionListener {
        void flip(boolean toBack);
    }

    private Recommendation recommendation;
    private CardInteractionListener listener;

    public CardBaseView(Context context) {
        super(context);
    }

    public CardBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CardBaseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CardBaseView(Context context, Recommendation recommendation, CardInteractionListener listener) {
        super(context);
        this.recommendation = recommendation;
        this.listener = listener;
    }

    protected Recommendation getRecommendation() {
        return recommendation;
    }

    protected CardInteractionListener getCardInteractionListener() {
        return listener;
    }

    public abstract void updateUI(Recommendation recommendation);
}
