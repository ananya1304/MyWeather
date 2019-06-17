package com.example.myweather;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherDetailFragment extends Fragment {

    TextView location, date, temp, min_temp, max_temp, humidity, wind, main;
    ImageView icon;
    public WeatherDetailFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
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

        return rootView;
    }
}
