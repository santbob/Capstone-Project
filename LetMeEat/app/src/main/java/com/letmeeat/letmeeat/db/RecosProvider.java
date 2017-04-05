package com.letmeeat.letmeeat.db;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh on 26/03/2017.
 * Contents Provider for Recommendations.
 */

public class RecosProvider extends ContentProvider {

    private SQLiteOpenHelper mDbHelper;

    private static final int RECOS = 0;
    private static final int RECOS_WITH_ID = 1;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecosContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, RecosContract.PATH_RECOS, RECOS);
        matcher.addURI(authority, RecosContract.PATH_RECOS + "/#", RECOS_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new RecosDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        Cursor cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
        if (cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RECOS: {
                final long id = db.insertOrThrow(RecosContract.RecosEntry.TABLE_NAME, null, values);
                if (getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return ContentUris.withAppendedId(RecosContract.RecosEntry.CONTENT_URI, id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        int rowNumber = builder.where(selection, selectionArgs).update(db, values);
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowNumber;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        int deletedRowNumber = builder.where(selection, selectionArgs).delete(db);
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRowNumber;
    }

    private SelectionBuilder buildSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        return buildSelection(uri, match, builder);
    }

    private SelectionBuilder buildSelection(Uri uri, int match, SelectionBuilder builder) {
        final List<String> paths = uri.getPathSegments();
        switch (match) {
            case RECOS: {
                return builder.table(RecosContract.RecosEntry.TABLE_NAME);
            }
            case RECOS_WITH_ID: {
                final String _id = paths.get(1);
                return builder.table(RecosContract.RecosEntry.TABLE_NAME).where(RecosContract.RecosEntry._ID + "=?", _id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @NonNull
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case RECOS:
                // directory
                return "vnd.android.cursor.dir" + "/" + RecosContract.CONTENT_AUTHORITY + "." + RecosContract.PATH_RECOS;
            case RECOS_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + RecosContract.CONTENT_AUTHORITY + "." + RecosContract.PATH_RECOS;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

}
