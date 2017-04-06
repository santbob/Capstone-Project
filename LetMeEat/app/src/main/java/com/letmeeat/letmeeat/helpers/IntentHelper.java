package com.letmeeat.letmeeat.helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.letmeeat.letmeeat.R;

/**
 * Created by santhosh on 05/04/2017.
 * Helps with starting other Intent.
 */

public class IntentHelper {

    private Activity activity;

    public IntentHelper(Activity activity) {
        this.activity = activity;
    }

    public boolean sendMapIntent(String address) {
        return sendURIIntent(Uri.parse(activity.getString(R.string.map_intent_url, address)));
    }

    public boolean sendWebIntent(String url) {
        return sendURIIntent(getHttpUrl(url));
    }

    private Uri getHttpUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return Uri.parse(url);
    }

    private boolean sendURIIntent(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
            return true;
        }
        return false;
    }

    public void sendDialIntent(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        activity.startActivity(intent);
    }
}