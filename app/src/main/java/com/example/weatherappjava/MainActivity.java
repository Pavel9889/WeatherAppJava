package com.example.weatherappjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GetData.AsyncResponce, SharedPreferences.OnSharedPreferenceChangeListener {


    private static final String TAG = "MainActivity";
    private Button searchButton;
    private EditText searchField;
    private TextView cityName;

    private static boolean showWind = true;
    private static boolean showPressure = true;
    private static String  color = "red";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        searchField = findViewById(R.id.search_field);
        cityName = findViewById(R.id.city_name);
        searchButton = findViewById(R.id.btn_search);
        searchButton.setOnClickListener(this);

        setupSharedPreferences();





    }

    private void setupSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        showPressure = sharedPreferences.getBoolean(getString(R.string.show_pressure_settings_key), true);
        showWind = sharedPreferences.getBoolean(getString(R.string.show_wind_settings_key), true);
        color = sharedPreferences.getString(getString(R.string.pref_color_key), getString(R.string.pref_color_red_value));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.show_pressure_settings_key))) {
            showPressure = sharedPreferences.getBoolean(getString(R.string.show_pressure_settings_key), true);

        } else if (key.equals(getString(R.string.show_wind_settings_key))) {
            showWind = sharedPreferences.getBoolean(getString(R.string.show_wind_settings_key), true);

        } else if (key.equals(getString(R.string.pref_color_key))) {
            color = sharedPreferences.getString(getString(R.string.pref_color_key), getString(R.string.pref_color_red_value));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      //  return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id== R.id.action_settings){
            Intent intent= new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
            //NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (TextUtils.isEmpty(searchField.getText().toString())){
            Toast toast = Toast.makeText(getApplicationContext(),"Вы не ввели данные",Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

            URL url = buildUrl(searchField.getText().toString());
            cityName.setText(searchField.getText().toString());
            new GetData(this).execute(url);



    }

    private URL buildUrl (String city){
        String BASE_URL="https://api.openweathermap.org/data/2.5/weather";
        String PARAM_CITY = "q";
        String PARAM_APPID= "appid";
        String appid_value = "459b0cf297dd5b3a20718e1798de23f4";

        Uri buildUri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter(PARAM_CITY, city).appendQueryParameter(PARAM_APPID, appid_value).build();
        URL url = null;

        try {
            url = new  URL(buildUri.toString());

        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;


    }

    @Override
    public void processFinish(String output) {
        Log.d(TAG, "processFinish: "+output);
        try {
            JSONObject resultJSON = new JSONObject(output);
            JSONObject weather = resultJSON.getJSONObject("main");
            JSONObject windObj = resultJSON.getJSONObject("wind");
            JSONObject sys = resultJSON.getJSONObject("sys");

            TextView temp = findViewById(R.id.tempValue);
            String temp_K = weather.getString("temp");
            float temp_C = Float.parseFloat(temp_K);
            temp_C = temp_C -(float) 273.15;
            String temp_C_string = Float.toString(temp_C);

            temp.setText(temp_C_string);

            TextView pressure = findViewById(R.id.pressureValue);
            if (this.showPressure) {
                pressure.setText(weather.getString("pressure"));
            } else {
                pressure.setText("");
            }



            TextView sunRise = findViewById(R.id.timeSynrise);
            String timeSunrise = sys.getString("sunrise");
            Locale myLocale = new Locale("ru", "RU");
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", myLocale);

            String dateString = formatter.format(new Date(Long.parseLong(timeSunrise)* 1000+(60*60*1000)*3));
            sunRise.setTextColor(Color.parseColor(color));
            sunRise.setText(dateString);


            TextView sunSet = findViewById(R.id.timeSunset);
            String timeSunSet = sys.getString("sunset");
            String sunSetString = formatter.format(new Date(Long.parseLong(timeSunSet)* 1000+(60*60*1000)*3));
            sunSet.setTextColor(Color.parseColor(color));
            sunSet.setText(sunSetString);


            TextView wind = findViewById(R.id.wind_speed);
            String speed = windObj.getString("speed");


            if (this.showWind){
                wind.setText(speed);
            } else {
                wind.setText("");
            }










        }catch (JSONException e){

        }

    }
}