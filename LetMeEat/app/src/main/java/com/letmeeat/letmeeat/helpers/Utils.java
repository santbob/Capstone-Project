package com.letmeeat.letmeeat.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by santhosh on 05/04/2017.
 * A static class with bunch of utiltity methods
 */

public class Utils {

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("failed to encode", e);
        }
    }

    public static String API_URL = "https://letmeeat-jamtydtnbm.now.sh/";
}
