package com.letmeeat.letmeeat.adapters;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.letmeeat.letmeeat.views.CardFlipper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh on 18/10/2016.
 * Custom ViewPagerAdapter for handling CardViewPager
 */

public class CardViewPagerAdapter extends PagerAdapter {
    private List<CardFlipper> cards;

    public CardViewPagerAdapter() {
        cards = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        View layout = cards.get(position);

        ViewGroup layoutParent = (ViewGroup) layout.getParent();
        if (layoutParent == null) {
            collection.addView(layout); //A view can have only 1 parent
        }
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        if (position >= 0 && cards.size() > position) {
            collection.removeView(cards.get(position));
        } else {
            collection.removeView((View) view);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        int index = cards.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "LetMeEat";
    }

    @Override
    public float getPageWidth(int position) {
        return 1.0f;
    }

    public View getMachineCardAt(int position) {
        if (cards != null && cards.size() > 0) {
            int cardsCount = getCount();
            if (position >= cardsCount) {
                return cards.get(cardsCount - 1);
            } else {
                return cards.get(position);
            }
        }
        return null;
    }

    public void resetPages(ViewPager pager) {
        removePagersAllChildren(pager);
        cards.clear();
        notifyDataSetChanged();
    }

    public void removePagersAllChildren(ViewGroup parent) {
        if (parent != null) {
            parent.removeAllViews();
        }
    }

    //process the cards, currently it just updates the taptoscan.
    public void processCards() {
        if (cards != null && cards.size() > 0) {
            for (CardFlipper flipper : cards) {
                //call the corresponding cards
            }
        }
    }

    public void updateViews(List<CardFlipper> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
        notifyDataSetChanged();
    }

    public View getView(int position) {
        return cards.get(position);
    }
}
