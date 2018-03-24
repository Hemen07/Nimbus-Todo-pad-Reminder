package com.redfox.nimbustodo.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.redfox.nimbustodo.weather.weather_util.UtilHttpClient;

public class NetworkAsync extends AsyncTask<Void, Integer, String> {

    private static final boolean LOG_DEBUG = false;
    private final static String TAG = NetworkAsync.class.getSimpleName();

    private String location;
    private Context mContext;//use only when you sure your app is in foreground else use thread


    private NetworkCallbacks mNetworkCallbacks;

    public NetworkAsync(Context mContext, NetworkCallbacks mNetworkCallbacks, String location) {
        this.mContext = mContext;
        this.mNetworkCallbacks = mNetworkCallbacks;
        this.location = location;

        if (LOG_DEBUG)
            Log.i(TAG, " Location passed : " + location);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mNetworkCallbacks.onPreExecuteInfY();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result = "";
        if (LOG_DEBUG)
            Log.i(TAG, " doInBackground");
        if (mContext != null) {
            result = UtilHttpClient.fetchWeatherData(location);
        }
        return result;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mNetworkCallbacks.onPostExecuteInfY(s);
    }

}