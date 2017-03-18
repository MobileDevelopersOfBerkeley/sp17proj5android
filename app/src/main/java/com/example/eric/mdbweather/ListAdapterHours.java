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

public class ListAdapterHours  extends RecyclerView.Adapter<ListAdapterHours.CustomViewHolder> {
    ArrayList<String> hoursTimeArray = new ArrayList<>();
    ArrayList<Integer> hourstempArray = new ArrayList<>();
    ArrayList<String> hoursSummaryArray = new ArrayList<>();

    public ListAdapterHours(final double latitude, final double longitude, final String apiKey) {
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

                    JSONObject hourly_weather = json.getJSONObject("hourly");
                    JSONArray hourly_data = hourly_weather.getJSONArray("data");
                    int hourly_data_len = hourly_data.length();
                    for(int i = 0; i < hourly_data_len; i++) {
                        JSONObject hour = hourly_data.getJSONObject(i);
                        long hour_time = hour.getLong("time");
                        String hour_summmary = hour.getString("summary");
                        int hour_temp = hour.getInt("temperature");
                        hoursTimeArray.add(Utils.epocToDateWithTime(hour_time));
                        hourstempArray.add(hour_temp);
                        hoursSummaryArray.add(hour_summmary);
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
        return hoursTimeArray.size();
    }

    @Override
    public ListAdapterHours.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hours_row, parent, false);
        return new ListAdapterHours.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListAdapterHours.CustomViewHolder holder, int position) {
        holder.hourTime.setText(hoursTimeArray.get(position));
        holder.hourTemp.setText(Integer.toString(hourstempArray.get(position)) + (char)0x00B0 + " F");
        holder.hourSummary.setText(hoursSummaryArray.get(position));
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView hourTime;
        TextView hourTemp;
        TextView hourSummary;

        public CustomViewHolder (View view) {
            super(view);
            this.hourTime = (TextView) view.findViewById(R.id.hours_time);
            this.hourTemp = (TextView) view.findViewById(R.id.hours_temp);
            this.hourSummary = (TextView) view.findViewById(R.id.hours_summary);
        }
    }
}

