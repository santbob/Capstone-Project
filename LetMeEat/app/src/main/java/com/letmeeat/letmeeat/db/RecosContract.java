package com.letmeeat.letmeeat.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by santhosh on 26/03/2017.
 * Contract for the DB
 */

public class RecosContract {
    public static final String CONTENT_AUTHORITY = "com.letmeeat.letmeeat";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RECOS = "recos";
    public static final String SPACE = " ";

    public static final class RecosEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECOS).build();

        public static final String TABLE_NAME = "recos";

        /**
         * Type: TEXT
         */
        public static final String COLUMN_RECO_ID = "recoId";
        /**
         * Type: TEXT NOT NULL
         */
        public static final String COLUMN_NAME = "name";
        /**
         * Type: BLOB NOT NULL
         */
        public static final String COLUMN_CATEGORIES = "categories";
        /**
         * Type: INTEGER NOT NULL DEFAULT 0
         */
        public static final String COLUMN_REVIEWS_COUNT = "reviewsCount";
        /**
         * Type: REAL NOT NULL DEFAULT 0
         */
        public static final String COLUMN_RATINGS = "ratings";
        /**
         * Type: TEXT NOT NULL
         */
        public static final String COLUMN_PRICE_RANGE = "priceRange";
        /**
         * Type: TEXT NOT NULL
         */
        public static final String COLUMN_CURRENCY = "currency";
        /**
         * Type: TEXT NOT NULL
         */
        public static final String COLUMN_PHONE = "phone";
        /**
         * Type: TEXT
         */
        public static final String COLUMN_URL = "url";
        /**
         * Type: TEXT NOT NULL
         */
        public static final String COLUMN_ADDRESS_LINE_1 = "addressLine1";
        /**
         * Type: TEXT
         */
        public static final String COLUMN_ADDRESS_LINE_2 = "addressLine2";
        /**
         * Type: TEXT
         */
        public static final String COLUMN_CITY = "city";
        /**
         * Type: TEXT NOT NULL
         */
        public static final String COLUMN_STATE = "state";
        /**
         * Type: TEXT
         */
        public static final String COLUMN_ZIP = "zip";
        /**
         * Type: TEXT
         */
        public static final String COLUMN_LANDMARK = "landmark";
        /**
         * Type: TEXT
         */
        public static final String COLUMN_DISPLAY_ADDRESS = "displayAddress";
        /**
         * Type: TEXT  ex- "37.2366851,-121.8308739"
         */
        public static final String COLUMN_LAT_LONG = "latLong";
        /**
         * Type: TEXT NOT NULL
         */
        public static final String COLUMN_COUNTRY = "country";
        /**
         * Type: TEXT
         */
        public static final String COLUMN_IMAGE_URL = "imageUrl";
        /**
         * Type: BLOB
         */
        public static final String COLUMN_PICTURES = "pictures";

        /**
         * Matches: /items/[_id]/
         */
        public static Uri buildItemUri(long _id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECOS).appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID item detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
    }

    private RecosContract() {
    }
}
