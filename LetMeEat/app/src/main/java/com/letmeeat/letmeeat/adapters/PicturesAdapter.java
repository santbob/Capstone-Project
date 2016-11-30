package com.letmeeat.letmeeat.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.letmeeat.letmeeat.R;
import com.letmeeat.letmeeat.views.CardBackView;
import com.letmeeat.letmeeat.views.SquareImageView;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by santhosh on 29/11/2016.
 * Adapter class for display images in the grid view in the back of the card.
 */

public class PicturesAdapter extends BaseAdapter {

    private Context context;
    private List<String> pictureUrls = Collections.emptyList();
    private CardBackView cardBackView;
    private int padInPx;

    public PicturesAdapter(Context context, CardBackView cardBackView, List<String> pictureUrls) {
        this.context = context;
        this.cardBackView = cardBackView;
        this.pictureUrls = pictureUrls;
        this.padInPx = context.getResources().getDimensionPixelSize(R.dimen.medium_pad);
    }

    public void updateData(List<String> pictureUrls) {
        this.pictureUrls = pictureUrls;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pictureUrls.size();
    }

    @Override
    public Object getItem(int i) {
        return pictureUrls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
//        ViewHolder viewHolder;
//
//        if (view == null) {
//            // inflate the GridView item layout
//            LayoutInflater inflater = LayoutInflater.from(context);
//            view = inflater.inflate(R.layout.layout_picture_item, cardBackView, false);
//
//            // initialize the view holder
//            viewHolder = new ViewHolder();
//            viewHolder.imageView = (ImageView) view.findViewById(R.id.picture);
//            view.setTag(viewHolder);
//        } else {
//            // recycle the already inflated view
//            viewHolder = (ViewHolder) view.getTag();
//        }
//
//        // update the item view
//        String pictureUrl = pictureUrls.get(position);
//        Picasso.with(context).load(pictureUrl).into(viewHolder.imageView);
//        view.setVisibility(View.VISIBLE);
//        return view;
        SquareImageView view = (SquareImageView) convertView;
        if (view == null) {
            view = new SquareImageView(context);
            view.setPadding(padInPx, padInPx, padInPx, padInPx);
        }
        String url = (String) getItem(position);

        Picasso.with(context).load(url).into(view);
        return view;
    }

//    private static class ViewHolder {
//        ImageView imageView;
//    }
}
