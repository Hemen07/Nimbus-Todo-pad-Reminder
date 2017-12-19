package com.redfox.nimbustodo.util.alarm_util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.redfox.nimbustodo.data.db.DBMgr;
import com.redfox.nimbustodo.data.db.DBSchema;
import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.util.common_util.UtilLogger;

import java.util.ArrayList;
import java.util.List;



public class AlarmReceiver extends BroadcastReceiver {


    private final static String TAG = AlarmReceiver.class.getSimpleName();
    private final static boolean LOG_DEBUG = true;

    private String title;
    private long fireAt;
    private long whenScheduledLong;
    private int isAlarmScheduled;
    private int recordId;
    private List<NoteModel> noteModelList;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LOG_DEBUG) Log.w(TAG, "onReceive()");

        if (intent != null && intent.getAction() != null) {

            if (intent.getAction().equalsIgnoreCase(UtilAlarmConstants.ALARM_ACTION)) {
                if (LOG_DEBUG) Log.d(TAG, " coming from Alarm Receiver...");

                setUpNotification(context, intent);
            } else if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {


                if (LOG_DEBUG)
                    Log.d(TAG, "Boot Triggered................................................");
                //extract data

                DBMgr dbMgr = new DBMgr(context);
                dbMgr.openDataBase();
                extractData2(dbMgr);
                dbMgr.closeDataBase();

                if (LOG_DEBUG) {
                    Log.d(TAG, "Loaded data (entry only with alarmScheduled 1) to NoteModel Size : " + noteModelList.size());
                    Log.d(TAG, "Loaded data (entry only with alarmScheduled 1) to NoteModel List : " + noteModelList.toString());
                }
                //Now You have the node model list ,iterate through all entry and schedule alarms
                if (LOG_DEBUG) Log.d(TAG, " gonna iterate and schedule : ");
                for (int i = 0; i < noteModelList.size(); i++) {
                    UtilAlarmManager.scheduleAlarm(context, noteModelList.get(i).getTitle(),
                            noteModelList.get(i).getScheduleTimeLong(), noteModelList.get(i).getScheduledWhenLong()
                            , noteModelList.get(i).get_id());
                }
            }

        }
    }

    private void setUpNotification(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            title = bundle.getString(UtilAlarmConstants.ALARM_TITLE);
            whenScheduledLong = bundle.getLong(UtilAlarmConstants.ALARM_WHEN_SCHEDULED);
            recordId = bundle.getInt(UtilAlarmConstants.RECORD_ID);

            if (LOG_DEBUG) UtilLogger.logReceiver(TAG, title, whenScheduledLong, recordId);

            new UtilNotification().createNotification(context, title, whenScheduledLong, recordId);
            playSound(context);
            doVibrate(context);

        }
    }

    private void playSound(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
        r.play();

    }

    private void doVibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));

            } else {
                vibrator.vibrate(500);
            }
        }
    }


    //pick all entry based on alarmScheduled 1  and then add it to noteModelList
    private void extractData2(DBMgr dbMgr) {
        Cursor cursor = dbMgr.getCursorSearch2("1");
        noteModelList = new ArrayList<>();

        if (LOG_DEBUG) Log.i(TAG, "inside extractData2()");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    NoteModel noteModel = new NoteModel();

                    noteModel.set_id(cursor.getInt(cursor.getColumnIndex(DBSchema.DB_ROW_ID)));
                    noteModel.setTitle(cursor.getString(cursor.getColumnIndex(DBSchema.DB_TITLE)));
                    noteModel.setImgUriPath(cursor.getInt(cursor.getColumnIndex(DBSchema.DB_IMAGE_PATH)));
                    noteModel.setSub_text(cursor.getString(cursor.getColumnIndex(DBSchema.DB_SUB_TEXT)));
                    noteModel.setCreateDate(cursor.getLong(cursor.getColumnIndex(DBSchema.DB_CREATE_DATE)));
                    noteModel.setUpdateDate(cursor.getLong(cursor.getColumnIndex(DBSchema.DB_UPDATE_DATE)));
                    noteModel.setScheduleTimeLong(cursor.getLong(cursor.getColumnIndex(DBSchema.DB_SCHEDULED_TIME_LONG)));
                    noteModel.setScheduledWhenLong(cursor.getLong(cursor.getColumnIndex(DBSchema.DB_SCHEDULED_TIME_WHEN)));
                    noteModel.setScheduledTitle(cursor.getString(cursor.getColumnIndex(DBSchema.DB_SCHEDULED_TITLE)));
                    noteModel.setIsAlarmScheduled(cursor.getInt(cursor.getColumnIndex(DBSchema.DB_IS_ALARM_SCHEDULED)));
                    noteModel.setIsTaskDone(cursor.getInt(cursor.getColumnIndex(DBSchema.DB_IS_TASK_DONE)));
                    noteModel.setIsArchived(cursor.getInt(cursor.getColumnIndex(DBSchema.DB_IS_ARCHIVED)));
                    noteModelList.add(noteModel);

                } while (cursor.moveToNext());
            }
            cursor.close();

        }
    }

}
