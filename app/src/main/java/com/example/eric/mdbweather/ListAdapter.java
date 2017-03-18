package com.example.eric.mdbweather;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by eric on 3/10/17.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.CustomViewHolder> {

    ArrayList<String> dayDateArray = new ArrayList<>();
    ArrayList<Integer> daytempArray = new ArrayList<>();
    ArrayList<String> daySummaryArray = new ArrayList<>();

    public ListAdapter(final double latitude, final double longitude, final String apiKey) {
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... voids) {
                try {
                    String str_lat = Double.toString(latitude);
                    String str_lon = Double.toString(longitude);
                    URL url = new URL(apiKey + str_lat + "," + str_lon);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = Utils.convertStreamToString(in);
                    JSONObject json = new JSONObject(response);

                    JSONObject daily_weather = json.getJSONObject("daily");
                    JSONArray daily_data = daily_weather.getJSONArray("data");
                    int daily_data_len = daily_data.length();
                    for(int i = 0; i < daily_data_len; i++) {
                        JSONObject day = daily_data.getJSONObject(i);
                        long day_time = day.getLong("time");
                        String day_summmary = day.getString("summary");
                        int day_temp_max = day.getInt("temperatureMax");
                        int day_temp_min = day.getInt("temperatureMin");
                        dayDateArray.add(Utils.epocToDate(day_time));
                        daytempArray.add((day_temp_max + day_temp_min) / 2);
                        daySummaryArray.add(day_summmary);
                        publishProgress();
                    }
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
            protected void onProgressUpdate(Void... values) {
                notifyDataSetChanged();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public int getItemCount() {
        return dayDateArray.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_row, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.dayDate.setText(dayDateArray.get(position));
        holder.dayTemp.setText(Integer.toString(daytempArray.get(position)) + (char)0x00B0 + " F");
        holder.daySummary.setText(daySummaryArray.get(position));
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView dayDate;
        TextView dayTemp;
        TextView daySummary;

        public CustomViewHolder (View view) {
            super(view);
            this.dayDate = (TextView) view.findViewById(R.id.day_date);
            this.dayTemp = (TextView) view.findViewById(R.id.day_temp);
            this.daySummary = (TextView) view.findViewById(R.id.day_summary);
        }
    }
}

