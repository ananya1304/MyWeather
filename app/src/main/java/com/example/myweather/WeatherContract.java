package com.example.myweather;
import android.net.Uri;
import android.provider.BaseColumns;

public class WeatherContract {
    private WeatherContract(){}
    public static final String CONTENT_AUTHORITY = "com.example.myweather";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final class WeatherEntry implements BaseColumns{
        public static final String TABLE_NAME = "current";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_MAIN = "main";
        public static final String COLUMN_TEMP = "temp";
        public static final String COLUMN_MIN_TEMP = "minTemp";
        public static final String COLUMN_MAX_TEMP = "maxTemp";
        public static final String COLUMN_WIND = "wind";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_ICON = "icon";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static Uri buildWeatherUriWithId(long id)
        {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
    }
}

