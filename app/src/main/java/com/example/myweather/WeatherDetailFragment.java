package com.example.myweather;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class WeatherDetailFragment extends Fragment {

    TextView location, date, temp, min_temp, max_temp, humidity, wind, main;
    ImageView icon;
    Activity activity;
    String Date, city;

    public WeatherDetailFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Date = getArguments().getString("date");
        city = getArguments().getString("city");
        activity = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.weather_detail, container, false);
        location = rootView.findViewById(R.id.textView16);
        date = rootView.findViewById(R.id.textView17);
        temp = rootView.findViewById(R.id.textView18);
        min_temp = rootView.findViewById(R.id.textView6);
        max_temp = rootView.findViewById(R.id.textView7);
        humidity = rootView.findViewById(R.id.textView19);
        wind = rootView.findViewById(R.id.textView20);
        main = rootView.findViewById(R.id.textView11);
        icon = rootView.findViewById(R.id.imageView5);

        String selection = WeatherContract.WeatherEntry.COLUMN_CITY+"=? AND "+ WeatherContract.WeatherEntry.COLUMN_DATE+"=?";
        String [] selectionArgs = new String[]{city, Date};
        Cursor cursor = activity.getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, null, selection, selectionArgs, null);
        while(cursor.moveToNext())
        {
            location.setText(city + ", " + cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_COUNTRY)));
            temp.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP)));
            min_temp.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)));
            max_temp.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)));
            humidity.setText("Humidity "+cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY)));
            wind.setText("Wind "+cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND)));
            main.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAIN)));
            date.setText(Date);
            String iconUrl = "http://openweathermap.org/img/w/" + cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_ICON)) + ".png";
            Picasso.with(activity).load(iconUrl).into(icon);
        }

        return rootView;
    }
}
