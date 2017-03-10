package com.example.datarsd1.forecastapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    RecyclerView weatherResults;
    WeatherAdapter adapter;
    ArrayList<Weather> weatherList;
    String cityName;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = "";
        latitude = 0.0;
        longitude = 0.0;

        weatherResults = (RecyclerView) findViewById(R.id.recyclerView);
        weatherResults.setLayoutManager(new LinearLayoutManager(this));

        weatherList = new ArrayList<>();

        adapter = new WeatherAdapter(MainActivity.this, weatherList);
        weatherResults.setAdapter(adapter);

        addWeather("Berkeley");

        ((FloatingActionButton)findViewById(R.id.addWeatherButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater tli = LayoutInflater.from(getApplicationContext());
                View cityNameInputView = tli.inflate(R.layout.city_add, null);
                final EditText cityNameText = (EditText) cityNameInputView.findViewById(R.id.cityNameInput);

                android.app.AlertDialog.Builder typeAlertDialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                typeAlertDialogBuilder.setView(cityNameInputView);
                typeAlertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add City",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        addWeather(cityNameText.getText().toString());
                                    }})
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }});

                android.app.AlertDialog typeAlertDialog = typeAlertDialogBuilder.create();
                typeAlertDialog.show();
            }
        });
    }

    public void addWeather(String name) {
        cityName = name;

        Utils util = new Utils(getApplicationContext());
        Utils.LatLong l = util.geocodeName(name);

        if (l == null) {
            Log.d("LL", "null geocode");
            return;
        }

        latitude = l.lat;
        longitude = l.lon;

        Log.d("LL", "Lat : " + l.lat + " Lon : " + l.lon);

        new AsyncTask<Void, Void, JSONObject>() {
            protected JSONObject doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://api.darksky.net/forecast/8b3e49b86353762b232bb0ccf24f7fd7/" + latitude + "," + longitude);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = convertStreamToString(in);
                    JSONObject json = new JSONObject(response);
                    return json;
                } catch (Exception e) {
                    return null;
                }
            }

            protected void onPostExecute(JSONObject json) {
                try {
                    String temp = ""+Math.round(json.getJSONObject("currently").getDouble("temperature"));
                    String weatherSummary = ""+json.getJSONObject("currently").getString("summary");
                    String icon = ""+json.getJSONObject("currently").getString("icon");
                    String weatherPrediction = ""+json.getJSONObject("minutely").getString("summary");
                    String iconPrediction = ""+json.getJSONObject("minutely").getString("icon");

                    Calendar calendar = Calendar.getInstance();
                    int month = (calendar.get(Calendar.MONTH)+1)%12;
                    int numDate = calendar.get(Calendar.DAY_OF_MONTH);
                    int numDay = calendar.get(Calendar.DAY_OF_WEEK);

                    String date = "" + month + "/" + numDate;
                    String day = "Sunday";
                    switch (numDay) {
                        case Calendar.SUNDAY:
                            day = "Sun";
                            break;
                        case Calendar.MONDAY:
                            day = "Mon";
                            break;
                        case Calendar.TUESDAY:
                            day = "Tue";
                            break;
                        case Calendar.WEDNESDAY:
                            day = "Wed";
                            break;
                        case Calendar.THURSDAY:
                            day = "Thu";
                            break;
                        case Calendar.FRIDAY:
                            day = "Fri";
                            break;
                        case Calendar.SATURDAY:
                            day = "Sat";
                            break;
                    }

                    Weather newWeather = new Weather(temp+"°", cityName, day, date, weatherSummary, icon, weatherPrediction, iconPrediction);
                    weatherList.add(newWeather);
                    adapter.updateList(weatherList);
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}