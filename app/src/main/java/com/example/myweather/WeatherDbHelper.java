package com.example.myweather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weather";
    private static final int DATABASE_VERSION = 1;
    public WeatherDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RESULT_TABLE = "CREATE TABLE " +
                WeatherContract.WeatherEntry.TABLE_NAME + " (" +
                WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WeatherContract.WeatherEntry.COLUMN_DATE + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_CITY + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_COUNTRY + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_MAIN + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_TEMP + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_WIND + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_ICON + " TEXT NOT NULL" + ")";

        db.execSQL(SQL_CREATE_RESULT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}

