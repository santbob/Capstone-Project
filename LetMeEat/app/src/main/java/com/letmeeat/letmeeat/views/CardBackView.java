package com.letmeeat.letmeeat.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.letmeeat.letmeeat.R;
import com.letmeeat.letmeeat.adapters.PicturesAdapter;
import com.letmeeat.letmeeat.models.Recommendation;

import java.util.ArrayList;

/**
 * Created by santhosh on 18/10/2016.
 * Recommendation Card Back Side View which shows photos from restaurants
 */

public class CardBackView extends CardBaseView {

    private TextView recommendationName;
    private GridView gridView;
    private PicturesAdapter picturesAdapter;

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

    public CardBackView(final Context context, Recommendation recommendation, CardInteractionListener listener) {
        super(context, recommendation, listener);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_card_back_view, this, true);

        recommendationName = (TextView) findViewById(R.id.recommendation_name);
        gridView = (GridView) findViewById(R.id.pictures_gridview);
        picturesAdapter = new PicturesAdapter(context, this, new ArrayList<String>());
        gridView.setAdapter(picturesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
            }
        });

        ImageView flipCard = (ImageView) findViewById(R.id.flip_card);
        flipCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getCardInteractionListener().flip(false);
            }
        });

        updateUI(recommendation);
    }

    @Override
    public void updateUI(Recommendation recommendation) {
        recommendationName.setText(recommendation.getName());
        picturesAdapter.updateData(recommendation.getPhotos());
    }
}
