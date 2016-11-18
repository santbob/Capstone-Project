package com.letmeeat.letmeeat.helpers;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by santhosh on 16/11/2016.
 */

public class Utils {

    public static int convertDipToPixel(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }
}
