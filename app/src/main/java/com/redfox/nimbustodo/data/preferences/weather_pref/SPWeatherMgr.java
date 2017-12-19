package com.redfox.nimbustodo.data.preferences.weather_pref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.redfox.nimbustodo.weather.weather_util.UtilWeatherConstants;


public class SPWeatherMgr {
    private final static String TAG = SPWeatherMgr.class.getSimpleName();
    private static final boolean LOG_DEBUG = false;

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @SuppressLint("CommitPrefEdits")
    public SPWeatherMgr(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferences = context.getSharedPreferences(UtilWeatherConstants.W_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (LOG_DEBUG)
            Log.i(TAG, "ctor");
    }

    //Configure Button visibility : Weather
    public void saveStatusForConfigureBtn(int status) {
        if (LOG_DEBUG)
            Log.i(TAG, " saveStatusForConfigureBtn() : " + status);
        editor.putInt(UtilWeatherConstants.W_PREF_BTN_CONFIGURE_STATUS, status);
        editor.commit();
    }

    public void saveDataToPreference(String city, String country, double temperature, String iCon) {
        if (LOG_DEBUG)
            Log.i(TAG, " saveDataToPreference():- " + "\n" + city + "\n" + country + "\n" + String.valueOf(temperature) +
                    "\n" + iCon);

        editor.putString(UtilWeatherConstants.W_PREF_CITY, city);
        editor.putString(UtilWeatherConstants.W_PREF_COUNTRY, country);
        editor.putLong(UtilWeatherConstants.W_PREF_TEMP, Double.doubleToRawLongBits(temperature));
        editor.putString(UtilWeatherConstants.W_PREF_ICON, iCon);
        editor.commit();
    }


    public Integer getSavedStatusForConfigureBtn() {
        return sharedPreferences.getInt(UtilWeatherConstants.W_PREF_BTN_CONFIGURE_STATUS, 0);
    }

    public String getPrefCity() {
        return sharedPreferences.getString(UtilWeatherConstants.W_PREF_CITY, "");
    }

    public String getPrefCountry() {
        return sharedPreferences.getString(UtilWeatherConstants.W_PREF_COUNTRY, "");
    }

    public Double getPrefTemp() {
        return Double.longBitsToDouble(sharedPreferences.getLong(UtilWeatherConstants.W_PREF_TEMP, 0));
    }


    public String getPrefIcon() {
        return sharedPreferences.getString(UtilWeatherConstants.W_PREF_ICON, "");
    }


    public void clearSaved() {
        if (LOG_DEBUG)
            Log.i(TAG, " clearSaved()");
        editor.clear();
        editor.apply();
    }


}
