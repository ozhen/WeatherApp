package com.oz.weather_sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import static com.oz.weather_sample.Function.setWeatherIcon;


/**
 * Created by Oscar on 2017/4/6.
 */

public class Week_Forecast_Function extends AppCompatActivity {

    Toolbar toolbar;

    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    Typeface weatherFont;
    EditText edTxtCity_dialobBox, edTxtCountry_dialobBox;
    TextView[] txtTemp_day,txtTemp_min,txtTemp_max;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_forecast);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //Today
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        cityField = (TextView)findViewById(R.id.city);
        updatedField = (TextView)findViewById(R.id.time_today);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView)findViewById(R.id.temp_current);
        humidity_field = (TextView)findViewById(R.id.humidity_field);
        pressure_field = (TextView)findViewById(R.id.pressure_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon_today);
        weatherIcon.setTypeface(weatherFont);


        txtTemp_day = new TextView[6];
        txtTemp_max = new TextView[6];
        txtTemp_min = new TextView[6];

        //TODO: Loop the following textView
        //TODO: Use DateFormat to display the dates of the week instead of dummy dates
        txtTemp_day[0] = (TextView)findViewById(R.id.day_2);
        txtTemp_day[1] = (TextView)findViewById(R.id.day_3);
        txtTemp_day[2] = (TextView)findViewById(R.id.day_4);
        txtTemp_day[3] = (TextView)findViewById(R.id.day_5);
        txtTemp_day[4] = (TextView)findViewById(R.id.day_6);
        txtTemp_day[5] = (TextView)findViewById(R.id.day_7);

        txtTemp_max[0] = (TextView)findViewById(R.id.temp_max_day_2);
        txtTemp_max[1] = (TextView)findViewById(R.id.temp_max_day_3);
        txtTemp_max[2] = (TextView)findViewById(R.id.temp_max_day_4);
        txtTemp_max[3] = (TextView)findViewById(R.id.temp_max_day_5);
        txtTemp_max[4] = (TextView)findViewById(R.id.temp_max_day_6);
        txtTemp_max[5] = (TextView)findViewById(R.id.temp_max_day_7);

        txtTemp_min[0] = (TextView)findViewById(R.id.temp_max_day_2);
        txtTemp_min[1] = (TextView)findViewById(R.id.temp_max_day_3);
        txtTemp_min[2] = (TextView)findViewById(R.id.temp_max_day_4);
        txtTemp_min[3] = (TextView)findViewById(R.id.temp_max_day_5);
        txtTemp_min[4] = (TextView)findViewById(R.id.temp_max_day_6);
        txtTemp_min[5] = (TextView)findViewById(R.id.temp_max_day_7);


        //TODO:Fix dulpicated code Function.placeIdtask in displayWeather
        Function.placeIdTask asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature,
                                      String weather_humidity, String weather_pressure, String weather_updatedOn,
                                      String weather_iconText, String sun_rise, JSONArray jsonArr_week) {

                cityField.setText(weather_city);
                updatedField.setText(weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidity: "+weather_humidity);
                pressure_field.setText("Pressure: "+weather_pressure);
                weatherIcon.setText(Html.fromHtml(weather_iconText));

                if(jsonArr_week != null){
                    try {
                        //List contains lists to store week's name, max_temp, and min_temp
                        ArrayList<ArrayList<String>> list_week_data = new ArrayList<>(6);


                        //Loop thru each day in the week
                        for(int i = 1; i < jsonArr_week.length(); i++){
                            JSONObject jsonObj_temp_week = jsonArr_week.getJSONObject(i);
                            JSONObject jsonObj_temp_tempurature = jsonObj_temp_week.getJSONObject("temp");

                            ArrayList<String> arrList_day_data = new ArrayList<>();

                            String temp_day = "Day " + String.valueOf(i + 1);
                            String temp_max = String.valueOf(jsonObj_temp_tempurature.getInt("max"))+ "°";
                            String temp_min = String.valueOf(jsonObj_temp_tempurature.getInt("min"))+ "°";

                            arrList_day_data.add(temp_day);
                            arrList_day_data.add(temp_max);
                            arrList_day_data.add(temp_min);

                            list_week_data.add(arrList_day_data);
                        }

                        for(int i = 0; i<txtTemp_day.length; i++) {
                            txtTemp_day[i].setText(list_week_data.get(i).get(0));
                            txtTemp_max[i].setText(list_week_data.get(i).get(1)+
                                    "/" + list_week_data.get(i).get(2));

                        }

                    }catch(JSONException e){
                    }
                }
            }
        });
        asyncTask.execute("Tokyo", "jp");
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.action_bar_menu,menu);
        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.icon_today:
                switchToday();
                return true;
            case  R.id.icon_search:
                display_dialogBox();
                return true;
            default:
                return true;
        }
    }

    public void switchToday(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void display_dialogBox(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Week_Forecast_Function.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialogbox_search, null);

        mBuilder.setView(mView)
                //TODO: Include invalid string handler
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                        edTxtCity_dialobBox = (EditText)mView.findViewById(R.id.dBox_city);
                        edTxtCountry_dialobBox = (EditText)mView.findViewById(R.id.dBox_country);

                        display_weather();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Return to the activity layout
                    }
                });

        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    public void display_weather(){
        String city = edTxtCity_dialobBox.getText().toString();
        String country = edTxtCountry_dialobBox.getText().toString();
        Function.placeIdTask asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {

            public void processFinish(String weather_city, String weather_description, String weather_temperature,
                                      String weather_humidity, String weather_pressure, String weather_updatedOn,
                                      String weather_iconText, String sun_rise, JSONArray jsonArr_week) {

                cityField.setText(weather_city);
                updatedField.setText(weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidity: "+weather_humidity);
                pressure_field.setText("Pressure: "+weather_pressure);
                weatherIcon.setText(Html.fromHtml(weather_iconText));

                if(jsonArr_week != null){
                    try {
                        //List contains lists to store week's name, max_temp, and min_temp
                        ArrayList<ArrayList<String>> list_week_data = new ArrayList<>(6);


                        //Loop thru each day in the week
                        for(int i = 1; i < jsonArr_week.length(); i++){
                            JSONObject jsonObj_temp_week = jsonArr_week.getJSONObject(i);
                            JSONObject jsonObj_temp_tempurature = jsonObj_temp_week.getJSONObject("temp");

                            ArrayList<String> arrList_day_data = new ArrayList<>();

                            String temp_day = "Day " + String.valueOf(i + 1);
                            String temp_max = String.valueOf(jsonObj_temp_tempurature.getInt("max"))+ "°";
                            String temp_min = String.valueOf(jsonObj_temp_tempurature.getInt("min"))+ "°";

                            arrList_day_data.add(temp_day);
                            arrList_day_data.add(temp_max);
                            arrList_day_data.add(temp_min);

                            list_week_data.add(arrList_day_data);
                        }

                        for(int i = 0; i<txtTemp_day.length; i++) {
                            txtTemp_day[i].setText(list_week_data.get(i).get(0));
                            txtTemp_max[i].setText(list_week_data.get(i).get(1)+
                                    "/" + list_week_data.get(i).get(2));

                        }

                    }catch(JSONException e){
                    }
                }
            }
        });
        asyncTask.execute(city, country);
    }



}
