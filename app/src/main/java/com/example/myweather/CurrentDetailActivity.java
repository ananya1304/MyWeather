package com.example.myweather;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class CurrentDetailActivity extends AppCompatActivity {
    TextView location, date, temp, min_temp, max_temp, humidity, wind, main;
    ImageView icon;
    private String city, Date;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_detail);
        location = findViewById(R.id.textView16);
        date = findViewById(R.id.textView17);
        temp = findViewById(R.id.textView18);
        min_temp = findViewById(R.id.textView6);
        max_temp = findViewById(R.id.textView7);
        humidity = findViewById(R.id.textView19);
        wind = findViewById(R.id.textView20);
        main = findViewById(R.id.textView11);
        icon = findViewById(R.id.imageView5);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            city = extras.getString("city");
            Date = extras.getString("date");
        }

        Cursor cursor=getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, null, "city = '"+city+"' AND date = '"+date+"'", null, null);
        cursor.moveToFirst();
        while(cursor.moveToNext())
        {
            location.setText(city + ", " + cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_COUNTRY)));
            temp.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP)));
            min_temp.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)));
            max_temp.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)));
            humidity.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY)));
            wind.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND)));
            main.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAIN)));
            date.setText(Date);
            //icon.setImageResource(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_ICON)));
        }
    }
}
