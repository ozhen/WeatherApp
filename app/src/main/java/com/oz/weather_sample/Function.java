package com.oz.weather_sample;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//TODO: Create a JSONobj list to store both current and daily weather data

public class Function {

    //Daily request
    private static final String OPEN_WEATHER_MAP_URL =
           "http://api.openweathermap.org/data/2.5/weather?q=%s,%s&units=metric";

    //Weekly request
    private static final String OPEN_WEATHER_MAP_FORCAST_URL =
            "http://api.openweathermap.org/data/2.5/forecast/daily?q=%s,%s&units=metric&cnt=7";

    private static final String OPEN_WEATHER_MAP_API =
            "14690f349339811822071d5e32bc915f";

    public static String setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }

    //Method for data which doesn't include sunrise and sunset values
    public static String setWeatherIcon(int actualId){
        int id = actualId / 100;
        String icon = "";

            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        return icon;
    }

    public interface AsyncResponse {

        void processFinish(String output1, String output2, String output3, String output4,
                           String output5, String output6, String output7, String output8,
                           JSONArray week_forecast);

    }
    public static class placeIdTask extends AsyncTask<String, Void, List<JSONObject>> {

        public AsyncResponse delegate = null;//Call back interface

        public placeIdTask(AsyncResponse asyncResponse) {
            //Assigning call back interfacethrough constructor
            delegate = asyncResponse;
        }

        //doInBackground which will return a list of JSONObject
        @Override
        protected List<JSONObject> doInBackground(String... params) {
            List<JSONObject> jsonWeather_list = null;

            try {
                jsonWeather_list = jsonList(params[0], params[1]);
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }
            return jsonWeather_list;
        }


        //TODO:Clean up the unnecessary comments
        @Override
        protected void onPostExecute(List<JSONObject> json) {

                if(json != null){
                        try{
                            //Current temp JSONObject
                            JSONObject jsonObj_today = json.get(0);

                            //Weekly forecast JSONObject
                            JSONObject jsonObj_week = json.get(1);

                            JSONObject today_details = jsonObj_today.getJSONArray("weather").getJSONObject(0);
                            JSONObject today_main = jsonObj_today.getJSONObject("main");

                            //JSONObject jsonObj_tmr = jsonObj_week.getJSONArray("list").getJSONObject(1);
                            //JSONObject tmr_temp = jsonObj_tmr.getJSONObject("temp");
                            JSONArray jsonArr_week = jsonObj_week.getJSONArray("list");
                           // System.out.println(jsonArr_week.toString());

                            //Date
                            DateFormat df = DateFormat.getDateTimeInstance();

                            String city = jsonObj_today.getString("name").toUpperCase(Locale.US) + ", "
                                    + jsonObj_today.getJSONObject("sys").getString("country");
                            String today_description = today_details.getString("description").toUpperCase(Locale.US);
                            String today_temp = String.valueOf(today_main.getInt("temp"))+ "°";
                            String humidity = today_main.getString("humidity") + "%";
                            String pressure = today_main.getString("pressure") + " hPa";
                            String updatedOn = df.format(new Date(jsonObj_today.getLong("dt")*1000));
                            String iconText = setWeatherIcon(today_details.getInt("id"),
                                    jsonObj_today.getJSONObject("sys").getLong("sunrise") * 1000,
                                    jsonObj_today.getJSONObject("sys").getLong("sunset") * 1000);


                            //Passing current weather info and weekly forecast JSONArray
                            delegate.processFinish(city, today_description, today_temp, humidity,
                                    pressure, updatedOn, iconText, ""+ (jsonObj_today.getJSONObject("sys").getLong("sunrise") * 1000),
                                    jsonArr_week);

                        }catch (JSONException e){
                        }
                    }
                }

        }

    public static List<JSONObject> jsonList(String city, String country){
        try {
            //URL for today's weather
            URL url_current_weather = new URL(String.format(OPEN_WEATHER_MAP_URL, city, country));

            //URL for tomorrow and weekly weather
            URL url_daily_forecast = new URL(String.format(OPEN_WEATHER_MAP_FORCAST_URL, city, country));

            HttpURLConnection connection_current_weather =
                    (HttpURLConnection)url_current_weather.openConnection();

            HttpURLConnection connection_daily_forecast =
                    (HttpURLConnection)url_daily_forecast.openConnection();

            connection_current_weather.addRequestProperty("x-api-key", "14690f349339811822071d5e32bc915f");
            connection_daily_forecast.addRequestProperty("x-api-key", "14690f349339811822071d5e32bc915f");

            BufferedReader reader_current_weather = new BufferedReader(
                    new InputStreamReader(connection_current_weather.getInputStream()));

            BufferedReader reader_daily_weather = new BufferedReader(
                    new InputStreamReader(connection_daily_forecast.getInputStream()));

            //TODO: Create a JSONObj list to store both current and daily weather data
            List<JSONObject> jsonObj_list = new ArrayList<>();

            //Get json data from current weather URL
            StringBuffer json_current_weather = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader_current_weather.readLine())!=null)
                json_current_weather.append(tmp).append("\n");
            reader_current_weather.close();

            JSONObject data_current_weather = new JSONObject(json_current_weather.toString());
            // This value will be 404 if the request was not
            // successful
            if(data_current_weather.getInt("cod") != 200){
                return null;
            }

            //Get json data from daily weather URL
            StringBuffer json_daily_weather = new StringBuffer(1024);
            String tmp1="";
            while((tmp1=reader_daily_weather.readLine())!=null)
                json_daily_weather.append(tmp1).append("\n");
            reader_daily_weather.close();

            JSONObject data_daily_weather = new JSONObject(json_daily_weather.toString());
            if(data_daily_weather.getInt("cod") != 200){
                return null;
            }

            //Add both current weather and daily weather JSONObjs to the list
            jsonObj_list.add(data_current_weather);
            jsonObj_list.add(data_daily_weather);
            return jsonObj_list;

        }catch(Exception e){
            return null;
        }
    }
}




