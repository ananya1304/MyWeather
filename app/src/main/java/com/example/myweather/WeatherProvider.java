package com.example.myweather;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class WeatherProvider extends ContentProvider {
    private WeatherDbHelper mOpenHelper;
    public static final int CODE_RESULT = 100;
    public static final int CODE_RESULT_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, WeatherContract.WeatherEntry.TABLE_NAME, CODE_RESULT);
        matcher.addURI(authority, WeatherContract.WeatherEntry.TABLE_NAME+"/#", CODE_RESULT_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate(){
        mOpenHelper = new WeatherDbHelper(getContext());
        return mOpenHelper != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        Cursor cursor;
        switch(sUriMatcher.match(uri))
        {
            case CODE_RESULT_WITH_ID:
                String _id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{_id};
                cursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME, projection, WeatherContract.WeatherEntry._ID + " = ? ", selectionArguments, null, null, sortOrder
                );
                break;

            case CODE_RESULT:
                cursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        return null;
    }
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch(sUriMatcher.match(uri))
        {
            case CODE_RESULT:
                long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);

                if(_id!=-1)
                    getContext().getContentResolver().notifyChange(uri, null);

                return WeatherContract.WeatherEntry.buildWeatherUriWithId(_id);

            default: return null;
        }
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)

    {
        return 0;
    }
}

