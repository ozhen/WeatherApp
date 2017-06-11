package com.oz.weather_sample;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.oz.weather_sample.Function.setWeatherIcon;

public class MainActivity extends AppCompatActivity {


    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    TextView txtTmrdate, txtTmrmax, txtTmrmin, txtTmrdetails, txtTmrweatherIcon;
    Button btnShow;
    EditText edTxtCity, edTxtCountry,edTxtCity_dialobBox, edTxtCountry_dialobBox;
    Typeface weatherFont;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trial);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //Today
        weatherFont = Typeface.createFromAsset(getAssets(),"fonts/weathericons-regular-webfont.ttf");
        cityField = (TextView)findViewById(R.id.city);
        updatedField = (TextView)findViewById(R.id.time_today);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView)findViewById(R.id.temp_current);
        humidity_field = (TextView)findViewById(R.id.humidity_field);
        pressure_field = (TextView)findViewById(R.id.pressure_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon_today);
        weatherIcon.setTypeface(weatherFont);

        //Tomorrow
        txtTmrdate = (TextView)findViewById(R.id.date_tmr);
        txtTmrweatherIcon = (TextView)findViewById(R.id.weather_icon_tmr);
        txtTmrdetails = (TextView)findViewById(R.id.tmr_detail);
        txtTmrmax = (TextView)findViewById(R.id.temp_tmr_max);
        txtTmrmin = (TextView)findViewById(R.id.temp_tmr_min);

        //Test tools
        btnShow = (Button) findViewById(R.id.btnShow);
        edTxtCity = (EditText) findViewById(R.id.edTxtCity);
        edTxtCountry = (EditText) findViewById(R.id.edTxtCountry);

        //Default weather
        display_weather("Tokyo","jp");

        //Testing code
/*
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_weather();
            }
        });
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.action_bar_menu,menu);
        return true;
    }

    public void switchToweek(){
        Intent intent = new Intent(this,Week_Forecast_Function.class);

        //TODO: Use Inten to pass jsonObject to Week_Forecast_Function

        //intent.putString("jsonObj_week_data", jsonObj.toString);

        //Then receive the JSON data at Week_Forecast_Function.
        //And use the data for default temperature when the app started

        //JSONObject jsonObj = new JSONObject(getIntent().getStringExtra("jsonObj_week_data"));


        startActivity(intent);
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.icon_week:
                switchToweek();
                return true;
            case R.id.icon_search:
                display_dialogBox();

                return true;
            default:
                return true;
        }
    }

    public void display_weather(){
        String city = edTxtCity.getText().toString();
        String country = edTxtCountry.getText().toString();
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

                //tmr
                if(jsonArr_week != null){
                    try {
                        JSONObject jsonObj_tmr = jsonArr_week.getJSONObject(1);
                        JSONObject tmr_temp = jsonObj_tmr.getJSONObject("temp");

                        String tmr_max ="Max: " + String.valueOf(tmr_temp.getInt("max"))+ "°";
                        String tmr_min ="Min: " + String.valueOf(tmr_temp.getInt("min"))+ "°";
                        String tmr_iconText = setWeatherIcon(jsonObj_tmr.getJSONArray("weather").getJSONObject(0).getInt("id"));
                        String tmr_detail = jsonObj_tmr.getJSONArray("weather").getJSONObject(0).getString("description").toUpperCase(Locale.US);

                        txtTmrdate.setText("TOMORROW");
                        txtTmrmax.setText(tmr_max);
                        txtTmrmin.setText(tmr_min);
                        //txtTmrweatherIcon.setText(Html.fromHtml(tmr_weather_Icon));
                        txtTmrweatherIcon.setText(Html.fromHtml(tmr_iconText));
                        txtTmrdetails.setText(tmr_detail);

                    }catch(JSONException e){
                    }
                }

            }
        });
        asyncTask.execute(city, country);
    }

    //Method which display default value("Tokyo","jp")
    public void display_weather(String city, String country){

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
            //tmr
            if(jsonArr_week != null){
                try {
                    JSONObject jsonObj_tmr = jsonArr_week.getJSONObject(1);
                    JSONObject tmr_temp = jsonObj_tmr.getJSONObject("temp");

                    String tmr_max ="Max: " + String.valueOf(tmr_temp.getInt("max"))+ "°";
                    String tmr_min ="Min: " + String.valueOf(tmr_temp.getInt("min"))+ "°";
                    String tmr_iconText = setWeatherIcon(jsonObj_tmr.getJSONArray("weather").getJSONObject(0).getInt("id"));
                    String tmr_detail = jsonObj_tmr.getJSONArray("weather").getJSONObject(0).getString("description").toUpperCase(Locale.US);

                    //Tomorrow
                    txtTmrdate.setText("TOMORROW");
                    txtTmrmax.setText(tmr_max);
                    txtTmrmin.setText(tmr_min);
                    txtTmrweatherIcon.setText(Html.fromHtml(tmr_iconText));
                    txtTmrdetails.setText(tmr_detail);

                }catch(JSONException e){
                }
            }
            }
        });
        asyncTask.execute(city, country);
    }

    public void display_dialogBox(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialogbox_search, null);

        mBuilder.setView(mView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    //TODO: Include invalid string handler
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                        edTxtCity_dialobBox = (EditText)mView.findViewById(R.id.dBox_city);
                        edTxtCountry_dialobBox = (EditText)mView.findViewById(R.id.dBox_country);

                        System.out.println("search");
                        String cty = edTxtCity_dialobBox.getText().toString();
                        String cntry = edTxtCountry_dialobBox.getText().toString();
                        display_weather(cty,cntry);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("cancelled");
                        finish();
                    }
                });

        final AlertDialog dialog = mBuilder.create();
        dialog.show();
   }

}

