package com.letmeeat.letmeeat.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by santhosh on 18/10/2016.
 * Custom ViewPager to Handle the Recommendation Cards
 */

public class CardViewPager extends ViewPager {

    private boolean isPagingEnabled = true;
    private View currentView;

    public CardViewPager(Context context) {
        super(context);
    }

    public CardViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return isValidTouch() && super.onTouchEvent(event);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return isValidTouch() && super.onInterceptTouchEvent(event);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidTouch() {
        return getAdapter() != null && getAdapter().getCount() > 0 && this.isPagingEnabled;
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

    public boolean moveForward() {
        int current = getCurrentItem();
        if (current < getCount() - 1) {
            setCurrentItem(current + 1);
            return true;
        }
        return false;
    }

    public boolean moveBackward() {
        int current = getCurrentItem();
        if (current < getCount() && current > 0) {
            setCurrentItem(current - 1);
            return true;
        }
        return false;
    }

    public int getCount() {
        if (getAdapter() != null) {
            return getAdapter().getCount();
        }
        return 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (currentView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int height = 0;
        currentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int h = currentView.getMeasuredHeight();
        if (h > height) height = h;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void measureCurrentView(View currentView) {
        this.currentView = currentView;
        requestLayout();
    }

}
