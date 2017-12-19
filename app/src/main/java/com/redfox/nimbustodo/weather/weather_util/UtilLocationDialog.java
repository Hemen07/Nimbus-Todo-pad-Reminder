package com.redfox.nimbustodo.weather.weather_util;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.ui.interfaces.LocationCallBack;


public class UtilLocationDialog {

    private static AlertDialog alertDialog;
    private final static String TAG = UtilLocationDialog.class.getSimpleName();
    private static final boolean LOG_DEBUG = true;

    public static void locationPicker(final Context context, final LocationCallBack mLocationCallBack) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View customView = layoutInflater.inflate(R.layout.location_picker, null);
        builder.setView(customView);

        final TextInputLayout tlp = (TextInputLayout) customView.findViewById(R.id.location_picker_txt_input_layout);
        final EditText etx = (EditText) customView.findViewById(R.id.location_picker_etx);
        Button btnSubmit = (Button) customView.findViewById(R.id.location_picker_btn);
        Button btnCancel = (Button) customView.findViewById(R.id.location_picker_cancel_btn);

        alertDialog = builder.create();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String locationName = etx.getText().toString().trim();
                if (locationName.length() < 1 && TextUtils.isEmpty(locationName)) {
                    if (LOG_DEBUG)
                        Log.i(TAG, " typed in editText : " + locationName);
                    etx.setHint("empty !! type something");
                    etx.setHintTextColor(context.getResources().getColor(R.color.net_check_unavailable_color));

                } else {
                    mLocationCallBack.locationProvider(locationName);

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    dismissDialog();
                }
            }
        });

        alertDialog.show();

    }

    public static void dismissDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
