package com.letmeeat.letmeeat.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letmeeat.letmeeat.R;

/**
 * Created by santhosh on 27/10/2016.
 */

public class TagView extends LinearLayout {

    private String value;
    private TagViewListener listener;
    private int smallPadInPx = getResources().getDimensionPixelSize(R.dimen.small_pad);

    public interface TagViewListener {
        void onTagDelete(TagView view);
    }

    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void build(String label, final String value, TagViewListener listener) {
        this.value = value;
        this.listener = listener;
        TextView tagTextView = new TextView(getContext());
        tagTextView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tagTextView.setText(label);
        tagTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        tagTextView.setPadding(0, 0, smallPadInPx, 0);
        ImageView deleteImageView = new ImageView(getContext());
        deleteImageView.setImageDrawable(getResources().getDrawable(R.drawable.close_circle_outline));
        deleteImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TagView.this.listener != null) {
                    TagView.this.listener.onTagDelete(TagView.this);
                }
            }
        });
        this.addView(tagTextView);
        this.addView(deleteImageView);
        this.setOrientation(HORIZONTAL);
        this.setPadding(smallPadInPx, smallPadInPx, smallPadInPx, smallPadInPx);
        //ViewGroup.LayoutParams lp = new LayoutParams(new MarginLayoutParams(getContext()).rightMargin);
        this.setBackground(getResources().getDrawable(R.drawable.box));
    }

    public String getValue() {
        return value;
    }
}
