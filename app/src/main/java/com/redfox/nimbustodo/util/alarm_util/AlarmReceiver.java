package com.redfox.nimbustodo.util.alarm_util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

import com.redfox.nimbustodo.data.db.DBHelperSingleton;
import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.util.common_util.UtilDBOperation;
import com.redfox.nimbustodo.util.common_util.UtilExtra;
import com.redfox.nimbustodo.util.common_util.UtilLogger;

import java.util.List;


public class AlarmReceiver extends BroadcastReceiver {


    private final static String TAG = AlarmReceiver.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;

    private String title;
    private long whenScheduledLong;
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

                workerThreadOperation(context);
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
            UtilExtra.playSound(context);
            UtilExtra.doVibrate(context);

        }
    }

    private void workerThreadOperation(final Context context) {
        //Now You have the node model list ,iterate through all entry and schedule alarms
        Thread myWorkerThread = new Thread(new Runnable() {
            @Override
            public void run() {


                Cursor cursor = DBHelperSingleton.getDbInstance(context).getCursorForAlarmScheduled("1");
                noteModelList = UtilDBOperation.extractCommonData(cursor, noteModelList);


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

                noteModelList.clear();


            }
        });
        myWorkerThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        myWorkerThread.start();

    }

}
