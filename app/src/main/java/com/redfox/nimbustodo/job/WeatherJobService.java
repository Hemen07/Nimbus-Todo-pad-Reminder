package com.redfox.nimbustodo.job;

import android.os.Process;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redfox.nimbustodo.data.preferences.weather_pref.SPWeatherMgr;
import com.redfox.nimbustodo.network.NetworkAsync;
import com.redfox.nimbustodo.network.NetworkCallbacks;
import com.redfox.nimbustodo.weather.model.OpenWeatherModel;


public class WeatherJobService extends JobService implements NetworkCallbacks {

    private final static String TAG = WeatherJobService.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;

    private Thread myWorker = null;
    private SPWeatherMgr spWeatherMgr = null;

    private String city;
    private String country;
    private double tempDouble;
    private String iCon;

    @Override
    public boolean onStartJob(JobParameters job) {
        spWeatherMgr = new SPWeatherMgr(WeatherJobService.this);
        String rcvLocation = spWeatherMgr.getPrefCity();
        if (LOG_DEBUG) Log.v(TAG, "onStartJob() : rcv City from SharedPref " + rcvLocation);
        checkWeatherInPeriodical(job, rcvLocation);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }


    private void checkWeatherInPeriodical(final JobParameters parameters, final String location) {
        myWorker = new Thread(new Runnable() {
            @Override
            public void run() {

                new NetworkAsync(WeatherJobService.this, WeatherJobService.this, location).execute();
                jobFinished(parameters, false);
                if (LOG_DEBUG) Log.v(TAG, " jobFinished() called");
            }
        });
        myWorker.start();
        myWorker.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (LOG_DEBUG) Log.v(TAG, "onDestroy()");

        if (myWorker != null) {
            Thread thread = myWorker;
            thread.interrupt();
            myWorker = null;
        }
    }


    @Override
    public void onPreExecuteInfY() {
        if (LOG_DEBUG) Log.v(TAG, "callback on Pre..");

    }

    @Override
    public void onPostExecuteInfY(String result) {

        if (LOG_DEBUG) {
            Log.v(TAG, "callback on Post..");
            Log.v(TAG, " ---- " + result);
        }

        if (result != null) {

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            OpenWeatherModel openWeatherModel = gson.fromJson(result, OpenWeatherModel.class);

            if (openWeatherModel.getName() != null) {
                city = openWeatherModel.getName();
            }
            if (openWeatherModel.getSys().getCountry() != null) {
                country = openWeatherModel.getSys().getCountry();
            }
            if (openWeatherModel.getMain().getTemp() != null) {
                tempDouble = openWeatherModel.getMain().getTemp();
            }
            if (openWeatherModel.getWeather().get(0).getIcon() != null) {
                iCon = openWeatherModel.getWeather().get(0).getIcon();
            }

            spWeatherMgr.saveDataToPreference(city, country, tempDouble, iCon);
        }

    }
}
