package com.letmeeat.letmeeat.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by santhosh on 05/04/2017.
 * A static class with bunch of utiltity methods
 */

public class Utils {

    private static final String LETMEEAT_SHARED_PREFS = "LETMEEAT_SHARED_PREFS";

    public static final String MIN_RATINGS = "MIN_RATINGS";
    public static final String CATEGORIES = "CATEGORIES";
    public static final String RECOS_CHOOSEN_IN_PAST = "RECOS_CHOOSEN_IN_PAST";
    public static final String LOCATION = "LOCATION";
    public static final String NUM_OF_DAYS_TO_IGNORE = "NUM_OF_DAYS_TO_IGNORE";
    public static final String PREF_MODIFIED = "PREF_MODIFIED";

    public static final int DEFAULT_IGNORE_DAYS = 5;

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("failed to encode", e);
        }
    }

    public static String API_URL = "https://letmeeat-yeklrwsvme.now.sh/";

    private static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
        if (context != null) {
            return context.getSharedPreferences(LETMEEAT_SHARED_PREFS,
                    Context.MODE_PRIVATE).edit();
        }
        return null;
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        if (context != null) {
            return context.getSharedPreferences(LETMEEAT_SHARED_PREFS, Context.MODE_PRIVATE);
        }
        return null;
    }

    public static void setSharedPrefString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
        if (editor != null) {
            editor.putString(key, value);
            editor.apply();
        }
    }

    public static String getSharedPrefString(Context context, String key) {
        SharedPreferences sp = getSharedPreferences(context);
        return (sp != null) ? sp.getString(key, "") : "";
    }

    public static void setSharedPrefBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
        if (editor != null) {
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public static boolean getSharedPrefBoolean(Context context, String key) {
        SharedPreferences sp = getSharedPreferences(context);
        return (sp != null) && sp.getBoolean(key, false);
    }

    public static void setSharedPrefStringSet(Context context, String key, Set<String> value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
        if (editor != null) {
            editor.putStringSet(key, value);
            editor.apply();
        }
    }

    public static Set<String> getSharedPrefStringSet(Context context, String key) {
        SharedPreferences sp = getSharedPreferences(context);
        return (sp != null) ? sp.getStringSet(key, null) : null;
    }

    public static void setSharedPrefFloat(Context context, String key, float value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
        if (editor != null) {
            editor.putFloat(key, value);
            editor.apply();
        }
    }

    public static float getSharedPrefFloat(Context context, String key) {
        SharedPreferences sp = getSharedPreferences(context);
        return (sp != null) ? sp.getFloat(key, 0) : 0;
    }

    public static void setSharedPrefInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
        if (editor != null) {
            editor.putInt(key, value);
            editor.apply();
        }
    }

    public static int getSharedPrefInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = getSharedPreferences(context);
        return (sp != null) ? sp.getInt(key, defaultValue) : defaultValue;
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static String getCommaSeparatedStringOfSet(Set<String> stringSet) {
        if (stringSet != null) {
            StringBuilder builder = new StringBuilder();
            for (Iterator<String> iterator = stringSet.iterator(); iterator.hasNext(); ) {
                String[] splitText = iterator.next().split(":");
                builder.append(splitText[0]);
                if (iterator.hasNext()) {
                    builder.append(",");
                }
            }
            return builder.toString();
        }
        return null;
    }
}
