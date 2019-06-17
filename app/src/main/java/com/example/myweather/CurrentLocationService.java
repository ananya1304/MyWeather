package com.example.myweather;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CurrentLocationService extends Service {
    private String APP_ID = "4be1f7712327c94d2f89e77944fd657b";
    static String name;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Bundle extras = intent.getExtras();
        if(extras != null) {
            String city = extras.getString("city");
            String date = extras.getString("date");
            fetchData(city, date);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {

    }
    private void fetchData(String city, String date)
    {
        if(Connectivity.isNetworkAvailable(getApplicationContext()))
        {
            DownloadWeather weatherTask=new DownloadWeather(city, date);
            weatherTask.execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

        }

    }

    private class DownloadWeather extends AsyncTask<String, Void, String> {

        String city, date;


        DownloadWeather(String city, String date){

            this.city=city;
            this.date=date;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String xml = "";
            String urlParameters = "";
                xml = Connectivity.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + city+"&appid="+APP_ID, urlParameters);
            if(xml != null)
                return xml;
            else
                return " ";
        }

        @Override
        protected void onPostExecute(String xml)
        {
            super.onPostExecute(xml);
            if (xml.length()>1)
            {
                try{
                    JSONObject jObj = new JSONObject(xml);

                    name = jObj.getString("name");
                    if(ifExists(name))
                    {
                        getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI, "city = '"+name+"'", null);
                    }
                    ContentValues values = new ContentValues();
                    JSONArray wArray = jObj.getJSONArray("weather");
                    JSONObject weatherObj = wArray.getJSONObject(0);

                    values.put(WeatherContract.WeatherEntry.COLUMN_MAIN, weatherObj.getString("main"));
                    values.put(WeatherContract.WeatherEntry.COLUMN_ICON, weatherObj.getString("icon"));

                    JSONObject mainObj = jObj.getJSONObject("main");

                    values.put(WeatherContract.WeatherEntry.COLUMN_TEMP, mainObj.getDouble("temp"));
                    values.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, mainObj.getInt("humidity"));
                    values.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, mainObj.getDouble("temp_min"));
                    values.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, mainObj.getDouble("temp_max"));

                    JSONObject windObj = jObj.getJSONObject("wind");

                    values.put(WeatherContract.WeatherEntry.COLUMN_WIND, windObj.getDouble("speed"));

                    JSONObject sysObj = jObj.getJSONObject("sys");

                    values.put(WeatherContract.WeatherEntry.COLUMN_COUNTRY, sysObj.getString("country"));

                    values.put(WeatherContract.WeatherEntry.COLUMN_CITY, name);
                    values.put(WeatherContract.WeatherEntry.COLUMN_DATE, date);

                    getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, values);
                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();

                }
            }
        }
        public boolean ifExists(String city)
        {
            try {
                Cursor cursor = getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, null,"city = '"+city+"'", null, null);
                return cursor.getCount()>0;
            }
            catch(Exception e)
            {
                Log.d("Exception occured", "Exception occured"+e);
            }
            return false;
        }

    }

    public static String getName()
    {
        return name;
    }

}
