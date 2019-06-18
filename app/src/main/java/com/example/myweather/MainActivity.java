package com.example.myweather;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView loc, date, temp, humidity, wind;
    CardView cardView;
    private boolean mTwoPane;
    public static ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
    private ImageButton button;
    private ItemListAdapter mAdapter;
    private String city="", Date;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private LocationManager locationManager;
    private String provider;
    View recyclerView;
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
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date = df.format(c);

        getLocationDetails();

        callWeatherService();
        loc = findViewById(R.id.textView);
        date = findViewById((R.id.textView3));
        temp = findViewById(R.id.textView2);
        humidity = findViewById(R.id.textView4);
        wind = findViewById(R.id.textView5);
        cardView = findViewById(R.id.card_view);
        button = findViewById(R.id.imageButton);
        accessData();

        recyclerView = findViewById(R.id.weather_list);
        assert recyclerView != null;
        setRecyclerViewData();
        setupRecyclerView((RecyclerView) recyclerView);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                callWeatherService();
                mAdapter.clear();
                accessData();
                setRecyclerViewData();
                setupRecyclerView((RecyclerView) recyclerView);

            }
        });
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {

                Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("city",city );
                extras.putString("date", Date);
                intent.putExtras(extras);
                startActivity(intent);
            }

        });

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
            callWeatherService();
            mAdapter.clear();
            accessData();
            setRecyclerViewData();
            setupRecyclerView((RecyclerView) recyclerView);
        }
    }


    private void getLocationDetails()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    MY_PERMISSION_ACCESS_FINE_LOCATION );
        }
        Location location = locationManager.getLastKnownLocation(provider);



        if (location != null) {
            onLocationChanged(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String str = addresses.get(0).getAddressLine(0);
            int i=0;
            while(str.charAt(i) != ',')
            {
                i++;
            }
            i++;
            while(str.charAt(i)!=',')
            {
                if(str.charAt(i)!=' ')
                    city+=str.charAt(i);
                i++;
            }
        }
        catch(IOException e){
        } catch (NullPointerException e) {
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    private String getCalculatedDate(int noOfDays)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
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
            String selection = WeatherContract.WeatherEntry.COLUMN_CITY+"=? AND "+ WeatherContract.WeatherEntry.COLUMN_DATE+"=?";
            String [] selectionArgs = new String[]{city, dt};
            cursor = getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, null, selection, selectionArgs, null);
            //cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                map.put("city", city);
                map.put("date", dt);
                map.put("temp", cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP)));
                map.put("icon", cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_ICON)));
                dataList.add(map);
            }
        }
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView)
    {
        mAdapter = new ItemListAdapter(this, dataList, mTwoPane);
        recyclerView.setAdapter(mAdapter);
    }
    private void accessData()
    {
        String selection = WeatherContract.WeatherEntry.COLUMN_CITY+"=? AND "+ WeatherContract.WeatherEntry.COLUMN_DATE+"=?";
        String [] selectionArgs = new String[]{city, Date};
        setData(getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, null,selection, selectionArgs, null));
    }
    private void setData(Cursor cursor)
    {
        if(cursor!=null) {
            while (cursor.moveToNext()) {
                loc.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_CITY)));
                temp.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP)));
                humidity.setText("Humidity " + cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY)));
                wind.setText("Wind " + cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND)));
                date.setText(Date);
            }
        }

    }

    private void callWeatherService()
    {
        Intent i = new Intent(this, WeatherService.class);
        Bundle extras = new Bundle();
        extras.putString("city", city);
        extras.putString("date", Date);
        i.putExtras(extras);
        startService(i);
    }

}
