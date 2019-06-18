package com.example.myweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class WeatherDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        if(savedInstanceState == null)
        {
            Bundle arguments = new Bundle();
            arguments.putString("city", getIntent().getStringExtra("city"));
            arguments.putString("date", getIntent().getStringExtra("date"));
            WeatherDetailFragment fragment = new WeatherDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.news_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if(id == android.R.id.home)
        {
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
