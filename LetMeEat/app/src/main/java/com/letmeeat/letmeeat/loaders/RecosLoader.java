package com.letmeeat.letmeeat.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.letmeeat.letmeeat.db.RecosContract;

/**
 * Created by santhosh on 04/04/2017.
 * Loader class for the loading the recommendations or recommendation and return a cursor.
 */

public class RecosLoader extends CursorLoader {

    public static RecosLoader newAllRecosInstance(Context context) {
        return new RecosLoader(context, RecosContract.RecosEntry.CONTENT_URI);
    }

    public static RecosLoader newInstanceForRecoId(Context context, long itemId) {
        return new RecosLoader(context, RecosContract.RecosEntry.buildItemUri(itemId));
    }

    private RecosLoader(Context context, Uri uri) {
        super(context, uri, null, null, null, RecosContract.RecosEntry._ID);
    }
}
