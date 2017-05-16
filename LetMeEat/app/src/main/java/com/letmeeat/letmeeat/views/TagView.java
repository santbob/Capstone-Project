package com.letmeeat.letmeeat.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letmeeat.letmeeat.R;

/**
 * Created by santhosh on 27/10/2016.
 * Custom view for Tag like UI, which has delete button and will call listener when clicked.
 */

public class TagView extends LinearLayout {

    private String value;
    private TagViewListener listener;
    private TextView tagTextView;

    public interface TagViewListener {
        void onTagDelete(TagView view);
    }

    public TagView(Context context) {
        this(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    private TagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_tag, this, true);
        tagTextView = (TextView) findViewById(R.id.tag_label);
        ImageView deleteIcon = (ImageView) findViewById(R.id.del_tag_icon);
        deleteIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TagView.this.listener != null) {
                    TagView.this.listener.onTagDelete(TagView.this);
                }
            }
        });
    }

    public void build(String label, final String value, TagViewListener listener) {
        this.value = value;
        this.listener = listener;
        tagTextView.setText(label);
    }

    public String getValue() {
        return value;
    }
}
