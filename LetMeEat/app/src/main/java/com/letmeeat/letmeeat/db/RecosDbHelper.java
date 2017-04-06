package com.letmeeat.letmeeat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by santhosh on 26/03/2017.
 * DB class which creates and deletes reco table
 */

class RecosDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "letmeeat.db";
    private static final int DATABASE_VERSION = 1;

    RecosDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RecosContract.RecosEntry.TABLE_NAME + " ("
                + RecosContract.RecosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RecosContract.RecosEntry.COLUMN_RECO_ID + " TEXT,"
                + RecosContract.RecosEntry.COLUMN_NAME + " TEXT NOT NULL,"
                + RecosContract.RecosEntry.COLUMN_CUISINE + " TEXT NOT NULL,"
                + RecosContract.RecosEntry.COLUMN_REVIEWS_COUNT + " INTEGER NOT NULL DEFAULT 0,"
                + RecosContract.RecosEntry.COLUMN_RATINGS + " REAL NOT NULL DEFAULT 0,"
                + RecosContract.RecosEntry.COLUMN_START_PRICE + " INTEGER NOT NULL DEFAULT 0,"
                + RecosContract.RecosEntry.COLUMN_END_PRICE + " INTEGER NOT NULL DEFAULT 10,"
                + RecosContract.RecosEntry.COLUMN_CURRENCY + " TEXT NOT NULL DEFAULT USD,"
                + RecosContract.RecosEntry.COLUMN_PHONE + " TEXT NOT NULL,"
                + RecosContract.RecosEntry.COLUMN_WEBSITE + " TEXT,"
                + RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_1 + " TEXT NOT NULL,"
                + RecosContract.RecosEntry.COLUMN_ADDRESS_LINE_2 + " TEXT,"
                + RecosContract.RecosEntry.COLUMN_CITY + " TEXT,"
                + RecosContract.RecosEntry.COLUMN_STATE + " TEXT NOT NULL,"
                + RecosContract.RecosEntry.COLUMN_ZIP + " TEXT NOT NULL,"
                + RecosContract.RecosEntry.COLUMN_LANDMARK + " TEXT,"
                + RecosContract.RecosEntry.COLUMN_COUNTRY + " TEXT NOT NULL,"
                + RecosContract.RecosEntry.COLUMN_PICTURES + " BLOB"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecosContract.RecosEntry.TABLE_NAME);
        onCreate(db);
    }
}

