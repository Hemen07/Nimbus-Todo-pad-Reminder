package com.redfox.nimbustodo.weather.weather_util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UtilNetworkDetect {


    private final static String TAG = UtilNetworkDetect.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;


    public static boolean isOnline(Context context) {
        boolean isNetTHere = false;

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null) {

                    isNetTHere = netInfo.isConnected();

                    if (LOG_DEBUG) {
                        System.out.println(TAG + " net available :-  " + netInfo.isAvailable());
                        System.out.println(TAG + " net connected :-  " + netInfo.isConnected());
                        System.out.println(TAG + " net type      :-  " + netInfo.getTypeName());
                    }
                }
            }

            return isNetTHere;

        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

}
