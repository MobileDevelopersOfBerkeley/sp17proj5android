package com.example.sarahtang.elementweatherapp;


/**
 * Created by sarahtang on 3/7/17.
 */

public class Utils {

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}

