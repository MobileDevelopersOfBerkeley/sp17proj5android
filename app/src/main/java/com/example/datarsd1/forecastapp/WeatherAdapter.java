package com.example.datarsd1.forecastapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.CustomViewHolder> {

    private Context context;
    ArrayList<Weather> weatherList;

    public WeatherAdapter(Context context, ArrayList<Weather> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Weather weather = weatherList.get(position);

        holder.weatherCity.setText(weather.city);
        holder.weatherDate.setText(weather.date);
        holder.weatherDay.setText(weather.day);
        holder.weatherTemp.setText(weather.temp);

        int resourceId = context.getResources().getIdentifier(weather.icon.replaceAll("-",""), "drawable", context.getPackageName());
        Drawable wIcon =  context.getResources().getDrawable(resourceId);
        holder.weatherIcon.setImageDrawable(wIcon);
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView weatherTemp, weatherCity, weatherDay, weatherDate;
        ImageView weatherIcon;

        public CustomViewHolder(View view) {
            super(view);

            this.weatherCity = (TextView) view.findViewById(R.id.cityCardText);
            this.weatherDate = (TextView) view.findViewById(R.id.dateCardText);
            this.weatherDay = (TextView) view.findViewById(R.id.dayCardText);
            this.weatherTemp = (TextView) view.findViewById(R.id.temperatureCardText);
            this.weatherIcon = (ImageView) view.findViewById(R.id.weatherCardIcon);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public void updateList(ArrayList<Weather> newList)
    {
        this.weatherList = newList;
    }

}
