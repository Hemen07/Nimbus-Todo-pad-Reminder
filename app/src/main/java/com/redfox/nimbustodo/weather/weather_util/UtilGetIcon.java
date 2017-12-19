package com.redfox.nimbustodo.weather.weather_util;

import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.weather.DisplayImageCallback;

public class UtilGetIcon {

    public static void displayWeatherImage(String icon, DisplayImageCallback mDisplayImageCallback) {
        switch (icon) {
            case "01d":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_01d_clear_sky);
                break;
            case "01n":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_01n_clear_night);
                break;
            case "02d":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_02d_few_clouds);
                break;
            case "02n":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_02n_few_clouds);
                break;
            case "03d":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_03d_broken_cloudy);
                break;
            case "03n":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_03n_broken_clouds);
                break;
            case "04d":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_04d_scatttered_cloud);
                break;
            case "04n":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_04n_scatterd_clouds);
                break;
            case "09d":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_09d_shower_rain);
                break;
            case "09n":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_09n_shower_rain);
                break;
            case "10d":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_010d_day_rain);
                break;
            case "10n":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_010n_night_rain);
                break;
            case "11d":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_011d_thunder_storm);
                break;
            case "11n":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_011n_thunder_storm);
                break;
            case "13d":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_013d_snow);
                break;
            case "13n":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_013n_snow);
                break;
            case "50d":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_50_mist);
                break;
            case "50n":
                mDisplayImageCallback.loadToGlide(R.drawable.ic_50_mist);
                break;
            default:
                mDisplayImageCallback.loadToGlide(R.drawable.ic_weather_unknown);

        }
    }
}
