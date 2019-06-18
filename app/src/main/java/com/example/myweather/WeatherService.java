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

public class WeatherService extends Service {

    private String APP_ID = "4be1f7712327c94d2f89e77944fd657b";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = new Bundle();
        extras=intent.getExtras();
        String city = extras.getString("city");
        String date = extras.getString("date");
        fetchData(city, 1, "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + APP_ID, date);
        fetchData(city, 2, "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + APP_ID, date);

        return START_STICKY;
    }

    private void fetchData(String city, int flag, String url, String date) {
        if (Connectivity.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather weatherTask=new DownloadWeather(city, flag, url, date);
            weatherTask.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private class DownloadWeather extends AsyncTask<String, Void, String> {

        String city;
        int flag;
        String url;
        String date;


        DownloadWeather(String city, int flag, String url, String date) {

            this.city = city;
            this.flag=flag;
            this.url=url;
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
            xml = Connectivity.excuteGet(url, urlParameters);
            if (xml != null)
                return xml;
            else
                return " ";
        }

        @Override
        protected void onPostExecute(String xml) {
            super.onPostExecute(xml);
            if (xml.length() > 1) {
                try {
                    switch(flag) {
                        case 1:
                            JSONObject jObj = new JSONObject(xml);

                            if(ifExists(date))
                            {
                                getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI, "city = '"+city+"'", null);
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

                            values.put(WeatherContract.WeatherEntry.COLUMN_CITY, city);
                            values.put(WeatherContract.WeatherEntry.COLUMN_DATE, date);

                            getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, values);
                            break;

                        case 2:
                            JSONObject jaObj = new JSONObject(xml);
                        ContentValues mvalues = new ContentValues();
                        String country = jaObj.optString("country");
                        JSONArray listArr = jaObj.optJSONArray("list");
                        for (int i = 0; i < listArr.length(); i++) {
                            JSONObject listObj = listArr.optJSONObject(i);
                            String str = listObj.optString("dt_txt");
                            String[] splited = str.split(" ");
                            String dt = splited[0];

                            if (ifExists(dt)) {
                                String selection = WeatherContract.WeatherEntry.COLUMN_CITY+"=? AND "+ WeatherContract.WeatherEntry.COLUMN_DATE+"=?";
                                String [] selectionArgs = new String[]{city, dt};
                                getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI, selection, selectionArgs);
                            }

                            mvalues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dt);

                            JSONObject main = listObj.optJSONObject("main");

                            mvalues.put(WeatherContract.WeatherEntry.COLUMN_TEMP, main.optString("temp"));
                            mvalues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, main.optString("temp_min"));
                            mvalues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, main.optString("temp_max"));
                            mvalues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, main.optString("humidity"));

                            JSONArray wArr = listObj.optJSONArray("weather");
                            JSONObject weather = wArr.optJSONObject(0);

                            mvalues.put(WeatherContract.WeatherEntry.COLUMN_MAIN, weather.optString("main"));
                            mvalues.put(WeatherContract.WeatherEntry.COLUMN_ICON, weather.optString("icon"));

                            JSONObject wind = listObj.optJSONObject("wind");
                            mvalues.put(WeatherContract.WeatherEntry.COLUMN_WIND, wind.optString("speed"));

                            mvalues.put(WeatherContract.WeatherEntry.COLUMN_CITY, city);
                            mvalues.put(WeatherContract.WeatherEntry.COLUMN_COUNTRY, country);

                            getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, mvalues);
                        }
                        break;
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();

                }

            }
        }

        private boolean ifExists(String current) {
            try {

                String selection = WeatherContract.WeatherEntry.COLUMN_CITY+"=? AND "+ WeatherContract.WeatherEntry.COLUMN_DATE+"=?";
                String [] selectionArgs = new String[]{city, current};
                Cursor cursor = getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, null, selection, selectionArgs, null);
                boolean res=cursor.getCount()>0;
                cursor.close();
                return  res;
            } catch (Exception e) {
                Log.d("Exception occured", "Exception occured" + e);
            }
            return false;
        }
    }
}
