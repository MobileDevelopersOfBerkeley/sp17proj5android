package com.example.eric.mdbweather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by eric on 3/8/17.
 */

public class Utils {

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static String epocToDate(long time){
        Date date = new Date(time*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    static String epocToDateWithTime(long time){
        Date date = new Date(time*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
