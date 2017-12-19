package com.redfox.nimbustodo.util.alarm_util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.redfox.nimbustodo.data.db.DBMgr;
import com.redfox.nimbustodo.data.db.DBSchema;
import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.util.common_util.UtilLogger;


public class DummyBroadcast extends BroadcastReceiver {

    /* PURPOSE :===
    * Upon clicking Notification Panel or Action :
    * Updates the DB value and
    * Redirect the user based on Pending Intent Settings
     */

    private final static String TAG = DummyBroadcast.class.getSimpleName();
    private final static boolean LOG_DEBUG = true;
    private NotificationManager notificationManager;
    private int NOTIFICATION_ID;

    private int recordPosId; //id
    private long scheduledWhenLong;
    private String scheduleTitle;
    private NoteModel noteModel;
    private DBMgr dbMgr;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (LOG_DEBUG) Log.i(TAG, " onReceive()");

        if (intent != null && intent.getAction() != null) {
            dbMgr = new DBMgr(context);
            dbMgr.openDataBase();
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


            if (intent.getAction().equalsIgnoreCase(UtilNotification.BTN_ACTION_NAME)) {
                if (LOG_DEBUG) Log.i(TAG, "  DISMISS ACTION CLICKED..update DB : ");

                if (intent.getAction().equalsIgnoreCase(UtilNotification.BTN_ACTION_NAME)) {
                    if (LOG_DEBUG) Log.i(TAG, "  DISMISS ACTION CLICKED..update DB : ");

                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {

                        scheduleTitle = bundle.getString(UtilNotification.BTN_ACTION_TITLE, "Default");
                        scheduledWhenLong = bundle.getLong(UtilNotification.BTN_ACTION_WHENSCHEDULED, 0);
                        recordPosId = bundle.getInt(UtilNotification.BTN_ACTION_RECORD_ID, 0);
                        NOTIFICATION_ID = bundle.getInt(UtilNotification.BTN_ACTION_NOTIFICATION_KEY, 0);
                        cancelIndividual(NOTIFICATION_ID);

                        if (LOG_DEBUG)
                            UtilLogger.logDummy(TAG, scheduleTitle, scheduledWhenLong, recordPosId);

                        pickEntryById(true);
                        updateEntry();

                    }

                }
            } else if (intent.getAction().equalsIgnoreCase(UtilNotification.DISMISS_ACTION_NAME)) {
                if (LOG_DEBUG) Log.i(TAG, "  DISMISSED PANEL ..swiped..update DB : ");

                Bundle bundle = intent.getExtras();
                if (bundle != null) {

                    scheduleTitle = bundle.getString(UtilNotification.DISMISS_ACTION_TITLE, "Default");
                    scheduledWhenLong = bundle.getLong(UtilNotification.DISMISS_ACTION_WHENSCHEDULED, 0);
                    recordPosId = bundle.getInt(UtilNotification.DISMISS_ACTION_RECORD_ID, 0);
                    NOTIFICATION_ID = bundle.getInt(UtilNotification.DISMISS_ACTION_NOTIFICATION_KEY, 0);
                    cancelIndividual(NOTIFICATION_ID);

                    if (LOG_DEBUG)
                        UtilLogger.logDummy(TAG, scheduleTitle, scheduledWhenLong, recordPosId);

                    pickEntryById(false);
                    updateEntry();
                    dbMgr.closeDataBase();

                }
            }
            dbMgr.closeDataBase();

        }
    }

    private void cancelIndividual(int ID) {
        if (notificationManager != null) {
            notificationManager.cancel(ID);
        }
    }

    private void pickEntryById(boolean isTaskDone) {
        if (LOG_DEBUG) Log.i(TAG, "pickEntryById()");

        if (dbMgr != null) {
            Cursor cursor = dbMgr.getCursorSearch(String.valueOf(recordPosId));
            cursorData(cursor, isTaskDone);
        }
    }

    private void cursorData(Cursor cursor, boolean isTaskDoneBoolean) {
        if (LOG_DEBUG) Log.w(TAG, " inside cursorData");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    noteModel = new NoteModel();

                    noteModel.set_id(recordPosId);
                    noteModel.setTitle(cursor.getString(cursor.getColumnIndex(DBSchema.DB_TITLE)));
                    noteModel.setImgUriPath(cursor.getInt(cursor.getColumnIndex(DBSchema.DB_IMAGE_PATH)));
                    noteModel.setSub_text(cursor.getString(cursor.getColumnIndex(DBSchema.DB_SUB_TEXT)));
                    noteModel.setCreateDate(cursor.getLong(cursor.getColumnIndex(DBSchema.DB_CREATE_DATE)));
                    noteModel.setUpdateDate(cursor.getLong(cursor.getColumnIndex(DBSchema.DB_UPDATE_DATE)));
                    long fireAt = cursor.getLong(cursor.getColumnIndex(DBSchema.DB_SCHEDULED_TIME_LONG));
                    if (LOG_DEBUG) Log.w(TAG, " previous fireAt " + fireAt);
                    noteModel.setScheduleTimeLong(0);

                    noteModel.setScheduledWhenLong(cursor.getLong(cursor.getColumnIndex(DBSchema.DB_SCHEDULED_TIME_WHEN)));
                    noteModel.setScheduledTitle(cursor.getString(cursor.getColumnIndex(DBSchema.DB_SCHEDULED_TITLE)));
                    int prevAlarmStatus = cursor.getInt(cursor.getColumnIndex(DBSchema.DB_IS_ALARM_SCHEDULED));
                    if (LOG_DEBUG) Log.w(TAG, " previous AlarmStatus " + prevAlarmStatus);
                    noteModel.setIsAlarmScheduled(0);
                    int isTaskDone = cursor.getInt(cursor.getColumnIndex(DBSchema.DB_IS_TASK_DONE));
                    System.out.println(" isTaskDone " + isTaskDone);
                    noteModel.setIsArchived(cursor.getInt(cursor.getColumnIndex(DBSchema.DB_IS_ARCHIVED)));

                    //if isTaskDone== 0 then do operation or else ignore
                    if (isTaskDone == 0) {

                        if (isTaskDoneBoolean == true) {
                            noteModel.setIsTaskDone(1);
                        } else {
                            noteModel.setIsTaskDone(0);
                        }
                    } else {
                        noteModel.setIsTaskDone(isTaskDone);
                    }

                    if (LOG_DEBUG) Log.i(TAG, " current NoteModel " + noteModel);

                } while (cursor.moveToNext());
            }
            cursor.close();

        }
    }

    private void updateEntry() {
        if (dbMgr != null && noteModel != null) {

            long updateNote = dbMgr.updateNote(noteModel);
            if (updateNote == 1) {
                if (LOG_DEBUG) Log.i(TAG, " successfully.. updated");
            } else {
                if (LOG_DEBUG) Log.i(TAG, " whoa whoa.....force is strong in here");

            }
        }
    }


}
