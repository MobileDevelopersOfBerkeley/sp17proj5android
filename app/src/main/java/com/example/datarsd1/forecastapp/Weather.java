package com.example.datarsd1.forecastapp;

/**
 * Created by Adhiraj Datar on 3/6/2017.
 */

public class Weather
{

    String temp, city, day, date, weatherSummary, icon, weatherPrediction, iconPrediction;

    public Weather(String t, String c, String da, String dt, String ws, String i, String wp, String ip)
    {
        this.temp = t;
        this.city = c;
        this.day = da;
        this.date = dt;
        this.weatherSummary = ws;
        this.icon = i;
        this.weatherPrediction = wp;
        this.iconPrediction = ip;
    }

}
