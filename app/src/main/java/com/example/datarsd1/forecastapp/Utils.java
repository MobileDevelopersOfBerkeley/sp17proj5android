package com.example.datarsd1.forecastapp;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adhiraj Datar on 3/6/2017.
 */

public class Utils
{

    Context context;

    public Utils(Context context)
    {
        this.context = context;
    }

    public LatLong geocodeName(String name)
    {
        List<LatLong> l = new ArrayList<LatLong>();
        if(Geocoder.isPresent()){
            try {
                Geocoder gc = new Geocoder(context);
                List<Address> addresses= gc.getFromLocationName(name, 5); // get the found Address Objects

                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        l.add(new LatLong(a.getLatitude(), a.getLongitude()));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (l.size()==0)
        {
            return null;
        }else
        {
            return l.get(0);
        }
    }

    class LatLong
    {
        double lat;
        double lon;

        public LatLong(double lat, double lon)
        {
            this.lat = lat;
            this.lon = lon;
        }
    }

    public String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
