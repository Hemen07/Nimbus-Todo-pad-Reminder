package com.redfox.nimbustodo.util.alarm_util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.redfox.nimbustodo.data.db.DBMgr;
import com.redfox.nimbustodo.data.db.DBSchema;
import com.redfox.nimbustodo.util.common_util.UtilLogger;


public class UtilAlarmManager {

    private final static String TAG = UtilAlarmManager.class.getSimpleName();
    private final static boolean LOG_DEBUG = true;
    private static int ALARM_PI_REQ_CODE = 0; //dynamic
    private static PendingIntent pIBroadCast;
    private static AlarmManager alarmManager;


    public static void scheduleAlarm(Context context, String noteTitle, long fireAT, long whenScheduled, int recordId) {
        if (LOG_DEBUG) UtilLogger.logAMgr(TAG, noteTitle, fireAT, whenScheduled, recordId);

        ALARM_PI_REQ_CODE = recordId;

        Intent intent = commonIntent(context, noteTitle, whenScheduled, recordId);


        pIBroadCast = PendingIntent.getBroadcast(context, ALARM_PI_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);


        //AlarmClock for exact 21+
        if (alarmManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlarmManager.AlarmClockInfo alarmClockInfo
                        = new AlarmManager.AlarmClockInfo(System.currentTimeMillis() + fireAT, null);
                alarmManager.setAlarmClock(alarmClockInfo, pIBroadCast);
            }
            //setExact for 19
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, fireAT, pIBroadCast);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, fireAT, pIBroadCast);
            }
        }
    }

//Put all these in BG...
    public static void cancelAlarm(Context context, int recordId) {
        if (LOG_DEBUG) Log.e(TAG, " cancelAlarm() : ");

        if (alarmManager != null) {
            if (pIBroadCast != null) {
                if (LOG_DEBUG) Log.e(TAG, " gonna cancel..");
                alarmManager.cancel(pIBroadCast);
            } else {
                if (LOG_DEBUG) Log.e(TAG, " pIBroadcast null");
            }

        } else {
            if (LOG_DEBUG) Log.e(TAG, " alarmManager null");

            DBMgr dbMgr = new DBMgr(context);
            dbMgr.openDataBase();
            Cursor cursor = dbMgr.getCursorSearch(String.valueOf(recordId));

            if (LOG_DEBUG) Log.w(TAG, " inside cursorData");
            int id = 0;
            String title = "";
            long scheduledWhenLong = 0;
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {

                        id = cursor.getInt(cursor.getColumnIndex(DBSchema.DB_ROW_ID));
                        title = cursor.getString(cursor.getColumnIndex(DBSchema.DB_TITLE));
                        scheduledWhenLong = cursor.getLong(cursor.getColumnIndex(DBSchema.DB_SCHEDULED_TIME_WHEN));

                    } while (cursor.moveToNext());
                }
                cursor.close();

            }
            dbMgr.closeDataBase();

            System.out.println("-----------+++++" + recordId + "== " + id);

            ALARM_PI_REQ_CODE = 0;
            ALARM_PI_REQ_CODE = recordId;


            Intent intent = commonIntent(context, title, scheduledWhenLong, recordId);
            PendingIntent pIBroadCast = PendingIntent.getBroadcast(context, ALARM_PI_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.cancel(pIBroadCast);
            }
        }
    }

    private static Intent commonIntent(Context context, String title, long scheduledWhenLong, int recordId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(UtilAlarmConstants.ALARM_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString(UtilAlarmConstants.ALARM_TITLE, title);
        bundle.putLong(UtilAlarmConstants.ALARM_WHEN_SCHEDULED, scheduledWhenLong);
        bundle.putInt(UtilAlarmConstants.RECORD_ID, recordId);
        intent.putExtras(bundle);
        return intent;
    }

}
