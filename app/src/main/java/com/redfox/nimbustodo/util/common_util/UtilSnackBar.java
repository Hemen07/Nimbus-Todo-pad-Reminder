package com.redfox.nimbustodo.util.common_util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.TextView;

import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.weather.weather_util.UtilWeatherConstants;


public class UtilSnackBar {

    private final static String TAG = UtilSnackBar.class.getSimpleName();

    public static void showSnackBar(Context context, String message, String whichOne) {
        View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, 2500);

        View sView = snackbar.getView();
        TextView textView = (TextView) sView.findViewById(android.support.design.R.id.snackbar_text);

        Typeface typeface = ResourcesCompat.getFont(context, R.font.caviar_dreams);


        if (whichOne.equalsIgnoreCase(UtilWeatherConstants.NO_NET)) {
            sView.setBackgroundColor(Color.parseColor("#E91E63"));
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(14);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTypeface(typeface);


        } else if (whichOne.equalsIgnoreCase(UtilWeatherConstants.NOT_FOUND)) {
            sView.setBackgroundColor(Color.parseColor("#FFEB3B"));
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(14);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTypeface(typeface);
        }

        snackbar.show();
    }

}
