package com.example.eric.mdbweather;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    public static double latitude;
    public static double longitude;

    private static int temp;
    private static String summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 0);
            return;
        }
        try {
            Location location2 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location2.getLongitude();
            latitude = location2.getLatitude();
        } catch (Exception e) {
            Log.d(getString(R.string.bad_log), getString(R.string.bad_location));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if(Geocoder.isPresent()){
                    String location = query;
                    Geocoder geoCoder = new Geocoder(getApplicationContext());
                    List<Address> addresses= null;
                    try {
                        addresses = geoCoder.getFromLocationName(location, 5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    latitude = addresses.get(0).getLatitude();
                    longitude = addresses.get(0).getLongitude();
                }

                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    public static class PlaceholderFragment extends Fragment implements View.OnClickListener{
        private static final String ARG_SECTION_NUMBER = "section_number";

        private static TextView current_temp;
        private static TextView current_time;
        private static TextView cityNameView;
        private static TextView current_summary;
        private static TextView current_date;
        private static TextView current_rain;
        private static TextView poweredBy;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.poweredBy:
                    Uri uri = Uri.parse(getString(R.string.dark_sky_powered));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
            int position = getArguments().getInt(ARG_SECTION_NUMBER);
            final View rootView;

            switch (position){
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    cityNameView = (TextView) rootView.findViewById(R.id.city_name);
                    current_time = (TextView) rootView.findViewById(R.id.current_time);
                    current_temp = (TextView) rootView.findViewById(R.id.current_temp);
                    current_summary = (TextView) rootView.findViewById(R.id.current_summary);
                    current_date = (TextView) rootView.findViewById(R.id.current_date);
                    current_rain = (TextView) rootView.findViewById(R.id.current_rain);
                    poweredBy = (TextView) rootView.findViewById(R.id.poweredBy);

                    poweredBy.setText(getString(R.string.powered_by));
                    poweredBy.setOnClickListener(this);

                    CountDownTimer newTimer = new CountDownTimer(2000000000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            Calendar c = Calendar.getInstance();
                            int am_pm =c.get(Calendar.AM_PM);
                            if (c.get(Calendar.MINUTE) < 10){
                                if (am_pm == Calendar.AM){
                                    current_time.setText(c.get(Calendar.HOUR)+":0"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND) + " " + getString(R.string.am));
                                } else {
                                    current_time.setText(c.get(Calendar.HOUR)+":0"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND) + " " + getString(R.string.pm));
                                }
                            } else {
                                if (am_pm == Calendar.AM){
                                    current_time.setText(c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND) + " " + getString(R.string.am));
                                } else {
                                    current_time.setText(c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND) + " " + getString(R.string.pm));
                                }
                            }
                        }
                        public void onFinish() {}
                    };
                    newTimer.start();

                    new AsyncTask<Void, Void, JSONObject>() {
                        protected JSONObject doInBackground(Void... voids) {
                            try {
                                String str_lat = Double.toString(latitude);
                                String str_lon = Double.toString(longitude);
                                URL url = new URL( getString(R.string.darksky_api_key) + str_lat + "," + str_lon);
                                Log.i(getString(R.string.location_log), str_lat + ", " + str_lon);
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod(getString(R.string.request_get));
                                InputStream in = new BufferedInputStream(conn.getInputStream());
                                String response = Utils.convertStreamToString(in);
                                JSONObject json = new JSONObject(response);

                                return json;
                            }
                            catch (Exception e) {return null;}
                        }

                        protected void onPostExecute(JSONObject json) {
                            try {

                                Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
                                List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                                if (addresses.size() > 0) {
                                    cityNameView.setText(addresses.get(0).getLocality());
                                }
                                else {
                                    cityNameView.setText(getString(R.string.city_not_found));
                                }
                                JSONObject current_weather = json.getJSONObject(getString(R.string.json_currently));
                                JSONObject hourly_weather = json.getJSONObject(getString(R.string.json_hourly));
                                JSONObject minutely_weather = json.getJSONObject(getString(R.string.json_minutely));

                                long time = current_weather.getLong(getString(R.string.json_currently_time));
                                temp = (int)(current_weather.getDouble(getString(R.string.json_currently_temp)));
                                summary = hourly_weather.getString(getString(R.string.json_hourly_summary));

                                JSONArray minutely_array = minutely_weather.getJSONArray(getString(R.string.json_minutely_data));
                                int minutely_array_len = minutely_array.length();
                                for(int i = 0; i < minutely_array_len; i++){
                                    JSONObject minute = minutely_array.getJSONObject(i);
                                    double rain_minute = minute.getDouble(getString(R.string.json_minutely_array_precipProb));
                                    if(rain_minute > 0){
                                        String rain_time = Utils.epocToDateWithTime(minute.getLong(getString(R.string.json_minutely_array_time)));
                                        String precip_type = minute.getString(getString(R.string.json_minutely_array_precipType));
                                        current_rain.setText("Incoming " + precip_type + " around " + rain_time);
                                        break;
                                    } else {
                                        current_rain.setText(getString(R.string.no_rain));
                                    }
                                }

                                String date = Utils.epocToDate(time);

                                current_date.setText(date);
                                current_temp.setText(Integer.toString(temp) + (char)0x00B0 + " F");
                                current_summary.setText(summary.substring(0, summary.length() - 1));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute();
                    return rootView;

                case 2:
                    rootView = inflater.inflate(R.layout.fragment_hours, container, false);
                    RecyclerView recyclerView2 = (RecyclerView) rootView.findViewById(R.id.recyclerView2);
                    ListAdapterHours adapter2 = new ListAdapterHours(latitude, longitude, getString(R.string.darksky_api_key));
                    recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView2.setAdapter(adapter2);
                    return rootView;

                case 3:
                    rootView = inflater.inflate(R.layout.fragment_days, container, false);
                    RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
                    ListAdapter adapter = new ListAdapter(latitude, longitude, getString(R.string.darksky_api_key));
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                    return rootView;
            }
            return null;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "0";
                case 1:
                    return "1";
                case 2:
                    return "2";
            }
            return null;
        }
    }
}
