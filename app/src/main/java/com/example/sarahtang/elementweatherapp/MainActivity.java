package com.example.sarahtang.elementweatherapp;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.SearchView;

import com.thbs.skycons.library.CloudFogView;
import com.thbs.skycons.library.CloudHvRainView;
import com.thbs.skycons.library.CloudMoonView;
import com.thbs.skycons.library.CloudRainView;
import com.thbs.skycons.library.CloudSnowView;
import com.thbs.skycons.library.CloudSunView;
import com.thbs.skycons.library.CloudThunderView;
import com.thbs.skycons.library.CloudView;
import com.thbs.skycons.library.MoonView;
import com.thbs.skycons.library.SunView;
import com.thbs.skycons.library.WindView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Get weather info from Dark Sky API
 * Set Skycons based on summary from API
 */


public class MainActivity extends AppCompatActivity {
    String currSummary;
    String currTemperature;
    String currIcon;
    double precipProbability;
    int timeRain;
    TextView requiredInfo;
    static CloudFogView cloudFogView;
    static CloudHvRainView cloudHvRainView;
    static CloudMoonView cloudMoonView;
    static CloudRainView cloudRainView;
    static CloudSnowView cloudSnowView;
    static CloudSunView cloudSunView;
    static CloudThunderView cloudThunderView;
    static CloudView cloudView;
    static MoonView moonView;
    static SunView sunView;
    static WindView windView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainweather);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //OG = set at invisible
        cloudFogView = (CloudFogView) findViewById(R.id.cloudFogView);
        cloudHvRainView = (CloudHvRainView) findViewById(R.id.cloudHvRainView);
        cloudMoonView = (CloudMoonView) findViewById(R.id.cloudMoonView);
        cloudRainView = (CloudRainView) findViewById(R.id.cloudRainView);
        cloudSnowView = (CloudSnowView) findViewById(R.id.cloudSnowView);
        cloudSunView = (CloudSunView) findViewById(R.id.cloudSunView);
        cloudThunderView = (CloudThunderView) findViewById(R.id.cloudThunderView);
        cloudView = (CloudView) findViewById(R.id.cloudView);
        moonView = (MoonView) findViewById(R.id.moonView);
        sunView = (SunView) findViewById(R.id.sunView);
        windView = (WindView) findViewById(R.id.windView);
        requiredInfo = (TextView) findViewById(R.id.requiredText);


        cloudFogView.setVisibility(View.INVISIBLE);
        cloudHvRainView.setVisibility(View.INVISIBLE);
        cloudMoonView.setVisibility(View.INVISIBLE);
        cloudRainView.setVisibility(View.INVISIBLE);
        cloudSnowView.setVisibility(View.INVISIBLE);
        cloudSunView.setVisibility(View.INVISIBLE);
        cloudThunderView.setVisibility(View.INVISIBLE);
        cloudView.setVisibility(View.INVISIBLE);
        moonView.setVisibility(View.INVISIBLE);
        sunView.setVisibility(View.INVISIBLE);
        windView.setVisibility(View.INVISIBLE);

        //Display required text that links to website
        requiredInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://darksky.net/poweredby/"));
                startActivity(intent);
            }
        });

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        final double longitude = location.getLongitude();
        final double latitude = location.getLatitude();

        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... voids) {
                try {

                    URL url = new URL("https://api.darksky.net/forecast/a5adde6779a52fe39082489fe64cf57a/" + latitude + "," + longitude);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = Utils.convertStreamToString(in);
                    JSONObject json = new JSONObject(response);

                    /**
                     * Is it going to rain in the next hour?
                     * Use minutely from Dark Sky API
                     */
                    int i = 0;
                    while (i <= 60) {
                        //Within the hour
                        precipProbability = json.getJSONObject("minutely").getJSONArray("data").getJSONObject(i).getDouble("precipProbability");
                        if (precipProbability != 0) {
                            timeRain = i;
                            break;
                        }
                        i ++;
                    }

                    currSummary = json.getJSONObject("currently").getString("summary");
                    currTemperature = json.getJSONObject("currently").getString("temperature");
                    currIcon = json.getJSONObject("currently").getString("icon");

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("bad", "url");
                } catch (ProtocolException p) {
                    p.printStackTrace();
                    Log.e("bad", "protocol");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("bad", "io");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("bad", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    /**
                     * Set which Skycon using based on summary
                     */

                    cloudFogView.setVisibility(View.INVISIBLE);
                    cloudHvRainView.setVisibility(View.INVISIBLE);
                    cloudMoonView.setVisibility(View.INVISIBLE);
                    cloudRainView.setVisibility(View.INVISIBLE);
                    cloudSnowView.setVisibility(View.INVISIBLE);
                    cloudSunView.setVisibility(View.INVISIBLE);
                    cloudThunderView.setVisibility(View.INVISIBLE);
                    cloudView.setVisibility(View.INVISIBLE);
                    moonView.setVisibility(View.INVISIBLE);
                    sunView.setVisibility(View.INVISIBLE);
                    windView.setVisibility(View.INVISIBLE);

                    sunView.setBgColor(Color.TRANSPARENT);
                    moonView.setBgColor(Color.TRANSPARENT);
                    cloudRainView.setBgColor(Color.TRANSPARENT);
                    cloudSnowView.setBgColor(Color.TRANSPARENT);
                    cloudHvRainView.setBgColor(Color.TRANSPARENT);
                    windView.setBgColor(Color.TRANSPARENT);
                    cloudView.setBgColor(Color.TRANSPARENT);
                    cloudSunView.setBgColor(Color.TRANSPARENT);
                    cloudMoonView.setBgColor(Color.TRANSPARENT);
                    cloudSnowView.setBgColor(Color.TRANSPARENT);
                    cloudThunderView.setBgColor(Color.TRANSPARENT);
                    windView.setBgColor(Color.TRANSPARENT);
                    cloudFogView.setBgColor(Color.TRANSPARENT);

                    ((TextView) findViewById(R.id.summaryText)).setText(currSummary);
                    ((TextView) findViewById(R.id.temperatureText)).setText(currTemperature + " °F");

                    if (precipProbability == 0) {
                        ((TextView) findViewById(R.id.rainIng)).setText("No rain soon.");
                    }
                    else {
                        ((TextView) findViewById(R.id.rainIng)).setText("It's gonna rain in T - " + timeRain + "minutes");
                    }

                    switch (currIcon) {
                        case "clear-day":
                            sunView.setVisibility(View.VISIBLE);
                            break;
                        case "clear-night":
                            moonView.setVisibility(View.VISIBLE);
                            break;
                        case "rain":
                            cloudRainView.setVisibility(View.VISIBLE);
                            break;
                        case "snow":
                            cloudSnowView.setVisibility(View.VISIBLE);
                            break;
                        case "sleet":
                            cloudHvRainView.setVisibility(View.VISIBLE);
                            break;
                        case "wind":
                            windView.setVisibility(View.VISIBLE);
                            break;
                        case "fog":
                            cloudView.setVisibility(View.VISIBLE);
                            break;
                        case "cloudy":
                            cloudView.setVisibility(View.VISIBLE);
                            break;
                        case "partly-cloudy-day":
                            cloudSunView.setVisibility(View.VISIBLE);
                            break;
                        case "partly-cloudy-night":
                            cloudMoonView.setVisibility(View.VISIBLE);
                            break;
                        case "hail":
                            cloudSnowView.setVisibility(View.VISIBLE);
                            break;
                        case "thunderstorm":
                            cloudThunderView.setVisibility(View.VISIBLE);
                            break;
                        case "tornado":
                            windView.setVisibility(View.VISIBLE);
                            break;
                        default:
                            cloudView.setVisibility(View.VISIBLE);
                            break;

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);


        return true;
    }
}
