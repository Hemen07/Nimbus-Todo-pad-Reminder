package com.redfox.nimbustodo.weather.weather_util;

import android.content.Context;
import android.util.Log;

import com.redfox.nimbustodo.ui.activity.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import static com.redfox.nimbustodo.weather.API.API_HOME;
import static com.redfox.nimbustodo.weather.API.API_KEY;


public class UtilHttpClient {

    private static final boolean LOG_DEBUG = true;
    private final static String TAG = UtilHttpClient.class.getSimpleName();

    public static String fetchWeatherData(Context context, HttpURLConnection httpURLConnection, String location) {

        if (LOG_DEBUG) {
            Log.d(TAG, " _ fetchWeatherData()");
        }
        StringBuilder resultBuilder = new StringBuilder();

        try {

            URL url = new URL(API_HOME + "/data/2.5/weather?q=" + location + "&type=accurate&appid=" + API_KEY + "&units=metric");
            if (LOG_DEBUG)
                Log.d(TAG, "URL : " + url.toString());
            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setRequestProperty("Accept-Encoding", "gzip"); //by default after ICS


            int responseCode = httpURLConnection.getResponseCode();


            if (responseCode == HttpURLConnection.HTTP_OK) {

                if (context != null && context instanceof MainActivity) {
                    MainActivity.cityFound(responseCode);
                }

                InputStream inputStream;

                if (httpURLConnection.getContentEncoding() != null && httpURLConnection.getContentEncoding().equalsIgnoreCase("gzip")) {
                    inputStream = new GZIPInputStream(httpURLConnection.getInputStream());
                } else {
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    resultBuilder.append(line);
                }
                bufferedReader.close();


            } else if (responseCode == 404) {
                if (context != null && context instanceof MainActivity) {
                    MainActivity.cityNotFound("404");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }

        return resultBuilder.toString();
    }
}
