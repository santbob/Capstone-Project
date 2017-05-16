package com.letmeeat.letmeeat.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.letmeeat.letmeeat.R;
import com.squareup.picasso.Picasso;

/**
 * Created by santhosh on 29/11/2016.
 * Adapter class for display images in the grid view in the back of the card.
 */

public class PhotosAdapter extends BaseAdapter {

    private final Context context;
    private final String[] pictureUrls;
    private final int padInPx;
    private final int imageWidth;

    public PhotosAdapter(Context context, String[] pictureUrls) {
        this.context = context;
        this.pictureUrls = pictureUrls;
        this.padInPx = context.getResources().getDimensionPixelSize(R.dimen.medium_pad);
        this.imageWidth = context.getResources().getDimensionPixelSize(R.dimen.picture_width);
    }

    @Override
    public int getCount() {
        return pictureUrls != null ? pictureUrls.length : 0;
    }

    @Override
    public Object getItem(int i) {
        return pictureUrls != null ? pictureUrls[i] : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ImageView view = (ImageView) convertView;
        if (view == null) {
            view = new ImageView(context);
            view.setPadding(padInPx, padInPx, padInPx, padInPx);
        }
        String url = (String) getItem(position);
        if (url != null) {
            Picasso.with(context)
                    .load(url)
                    .resize(imageWidth, imageWidth)
                    .centerCrop()
                    .into(view);
        }

        return view;
    }
}
