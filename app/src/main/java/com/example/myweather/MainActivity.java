package com.example.myweather;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView location, date, temp, humidity, wind;
    CardView cardView;
    private boolean mTwoPane;
    public static ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
    String city="london";
    Double latitude=0.0, longitude=0.0;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private String formattedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.weather_detail_container) != null)
        {
            mTwoPane=true;
        }
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd");
        formattedDate = df.format(c);

        callCurrentLocationService();
        callForecastService();

        location = findViewById(R.id.textView);
        date = findViewById((R.id.textView3));
        temp = findViewById(R.id.textView2);
        humidity = findViewById(R.id.textView4);
        wind = findViewById(R.id.textView5);
        cardView = findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, CurrentDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("city", city);
                extras.putString("date", formattedDate);
                intent.putExtras(extras);
                startActivity(intent);
            }

        });

        setRecyclerViewData();
        View recyclerView = findViewById(R.id.weather_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        accessData();


        handleIntent(getIntent());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            city=query;
        }
    }

    private String getCalculatedDate(int noOfDays)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MMM-dd");
        cal.add(Calendar.DAY_OF_YEAR, noOfDays);
        return s.format(new Date(cal.getTimeInMillis()));
    }
    private void setRecyclerViewData()
    {
        HashMap<String, String> map = new HashMap<>();
        Cursor cursor;
        String dt;
        for(int i=1;i<6;i++)
        {
            dt=getCalculatedDate(i);
            cursor = getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, null, "city = '"+city+"' AND date = '"+dt+"'", null, null);
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                map.put("date", dt);
                map.put("temp", cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP)));
                map.put("icon", cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_ICON)));
                dataList.add(map);
            }
        }
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView)
    {
        ItemListAdapter mAdapter = new ItemListAdapter(this, dataList, mTwoPane);
        recyclerView.setAdapter(mAdapter);
    }
    private void accessData()
    {
        setData(getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, null,"city = '"+city+"'", null, null));
    }
    private void setData(Cursor cursor)
    {
        if(cursor!=null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                location.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_CITY)));
                temp.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP)));
                humidity.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY)));
                wind.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND)));
                date.setText(formattedDate);
            }
        }

    }

    private void callCurrentLocationService()
    {
        Intent i = new Intent(this, CurrentLocationService.class);
        Bundle extras = new Bundle();
        extras.putString("city", city);
        extras.putString("date", formattedDate);
        i.putExtras(extras);
    }

    private void callForecastService()
    {
        Intent i = new Intent(this, ForecastService.class);
        i.putExtra("city", city);
    }

}
