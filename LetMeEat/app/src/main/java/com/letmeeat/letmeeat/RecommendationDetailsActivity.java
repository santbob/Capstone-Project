package com.letmeeat.letmeeat;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.letmeeat.letmeeat.adapters.CardViewPagerAdapter;
import com.letmeeat.letmeeat.views.CardViewPager;

/**
 * Created by santhosh on 26/03/2017.
 * Displays the extended details of the Recommendation with pictures and can be paged to the next recommendation.
 */

public class RecommendationDetailsActivity extends BaseActivity {

    private CardViewPager pager;
    private CardViewPagerAdapter cardViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_detail);

        cardViewPagerAdapter = new CardViewPagerAdapter();
        pager = (CardViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(cardViewPagerAdapter);
        pager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        pager.setPageMarginDrawable(new ColorDrawable(0x22000000));

    }
}
