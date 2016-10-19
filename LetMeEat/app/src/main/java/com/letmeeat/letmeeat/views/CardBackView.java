package com.letmeeat.letmeeat.views;

import android.content.Context;
import android.util.AttributeSet;

import com.letmeeat.letmeeat.models.Recommendation;

/**
 * Created by santhosh on 18/10/2016.
 * Recommendation Card Back Side View which shows photos from restaurants
 */

public class CardBackView extends CardBaseView {

    public CardBackView(Context context) {
        super(context);
    }

    public CardBackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardBackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CardBackView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CardBackView(Context context, Recommendation recommendation, CardInteractionListener listener) {
        super(context, recommendation, listener);
    }
}
