package com.redfox.nimbustodo.util.common_util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.redfox.nimbustodo.ui.interfaces.DateTimeCallback;

import java.util.Calendar;

public class UtilDateTimePicker {

    public static void datePicker(final Context context, final Calendar mCalendar, int mYear, int mMonth, int mDay, final DateTimeCallback mCallback) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCallback.dateCallBacks(year, monthOfYear, dayOfMonth);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mCallback.cancelCallBacks(true);
                dialog.dismiss();
            }
        });
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }


    public static void timePicker(Context context, int mHour, int mMinute, final DateTimeCallback mCallback) {


        TimePickerDialog mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                mCallback.timeCallBacks(selectedHour, selectedMinute);

            }
        }, mHour, mMinute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mCallback.cancelCallBacks(true);
                dialog.dismiss();
            }
        });
        mTimePicker.show();
    }
}
