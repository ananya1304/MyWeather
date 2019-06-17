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

public class ForecastService extends Service {
    private String APP_ID = "4be1f7712327c94d2f89e77944fd657b";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
            String city = intent.getStringExtra("city");
            fetchData(city);

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {

    }
    private void fetchData(String city)
    {
        if(Connectivity.isNetworkAvailable(getApplicationContext()))
        {
            DownloadWeather weatherTask=new DownloadWeather(city);
            weatherTask.execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

        }

    }

    private class DownloadWeather extends AsyncTask<String, Void, String> {

        String city;


        DownloadWeather(String city){

            this.city=city;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String xml = "";
            String urlParameters = "";
            xml = Connectivity.excuteGet("https://api.openweathermap.org/data/2.5/forecast?q=" + city+"&appid="+APP_ID, urlParameters);
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
                    ContentValues values = new ContentValues();
                    String country = jObj.getString("country");
                    String dtTxt, Date="";

                    JSONArray list = jObj.getJSONArray("list");
                    for(int i=0;i<list.length();i++)
                    {
                        JSONObject listObj = list.getJSONObject(i);
                        dtTxt=listObj.getString("dt_txt");
                        int j=0;
                        while(dtTxt.charAt(j)!=' ')
                        {
                            Date+=dtTxt.charAt(j);
                            j++;
                        }
                        if(ifExists(city, Date))
                        {
                            getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI, "city = '"+city+"' AND date = " + "'" +Date+"'", null);
                        }

                        values.put(WeatherContract.WeatherEntry.COLUMN_DATE, Date);

                        JSONObject main=listObj.getJSONObject("main");

                        values.put(WeatherContract.WeatherEntry.COLUMN_TEMP, main.getString("temp"));
                        values.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, main.getString("temp_min"));
                        values.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, main.getString("temp_max"));
                        values.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, main.getString("humidity"));

                        JSONObject weather=listObj.getJSONObject("weather");

                        values.put(WeatherContract.WeatherEntry.COLUMN_MAIN, weather.getString("main"));
                        values.put(WeatherContract.WeatherEntry.COLUMN_ICON, weather.getString("icon"));

                        JSONObject wind = listObj.getJSONObject("wind");
                        values.put(WeatherContract.WeatherEntry.COLUMN_WIND, wind.getString("speed"));

                        values.put(WeatherContract.WeatherEntry.COLUMN_CITY, city);

                        getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, values);
                    }
                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();

                }
            }
        }
        public boolean ifExists(String city, String date)
        {
            try {
                Cursor cursor = getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, null,"city = '"+city+"' AND date = " + "'" +date+"'", null, null);
                return cursor.getCount()>0;
            }
            catch(Exception e)
            {
                Log.d("Exception occured", "Exception occured"+e);
            }
            return false;
        }
    }
}
