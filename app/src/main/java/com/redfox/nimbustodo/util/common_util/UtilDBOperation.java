package com.redfox.nimbustodo.util.common_util;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.data.db.DBMgr;
import com.redfox.nimbustodo.data.db.DBSchema;
import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.ui.activity.NoteUpdateActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class UtilDBOperation {

    private final static boolean LOG_DEBUG = true;
    private final static String TAG = UtilDBOperation.class.getSimpleName();

    public static int updateEntry(DBMgr dbMgr, int isAlarmScheduled, long scheduledTimeLong, int recordPosId,
                                  String titleNote, int imageUriPath, String noteExtra, long dateCreation,
                                  int isTaskDone, int isArchived, String alarmTimSet) {
        if (LOG_DEBUG)
            Log.e(TAG, " updateEntry() : " + isAlarmScheduled + " LONG " + scheduledTimeLong);

        NoteModel noteModel = new NoteModel();
        noteModel.set_id(recordPosId);
        String noteTitle = titleNote;
        noteModel.setTitle(noteTitle);
        if (imageUriPath == 0) {
            noteModel.setImgUriPath(R.drawable.tag_default);
        } else {
            noteModel.setImgUriPath(imageUriPath);
        }
        String extra = noteExtra;
        if (extra.length() < 1 || TextUtils.isEmpty(extra)) {
            noteModel.setSub_text("");
        } else {
            noteModel.setSub_text(extra);
        }
        noteModel.setCreateDate(dateCreation);
        //take the current time and set it to last update
        noteModel.setUpdateDate(System.currentTimeMillis());
        noteModel.setScheduleTimeLong(scheduledTimeLong);

        long currentTime;
        if (isAlarmScheduled == 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            currentTime = calendar.getTimeInMillis();
            noteModel.setScheduledWhenLong(currentTime); //when was scheduled
            noteModel.setScheduledTitle(noteTitle);
        } else {
            currentTime = 0;
            noteModel.setScheduledWhenLong(currentTime);
            noteModel.setScheduledTitle("notSet");
        }
        noteModel.setIsAlarmScheduled(isAlarmScheduled);
        noteModel.setIsTaskDone(isTaskDone);
        noteModel.setIsArchived(isArchived);

        if (LOG_DEBUG) Log.e(TAG, " Before Updation : Note Model Has " + noteModel);

        if (isAlarmScheduled == 1) {
            if (LOG_DEBUG) {
                Log.v(TAG, " currentTime  " + currentTime);
                Log.v(TAG, " alarmSetTime  " + alarmTimSet);
            }
        }

        if (noteTitle.length() < 1 | noteTitle.isEmpty()) {
            dbMgr.closeDataBase();
            return 0;
            //   etxTitle.setError("Can't left Empty..");
        } else {

            long updateNote = dbMgr.updateNote(noteModel);
            if (updateNote == 1) {
                if (LOG_DEBUG) Log.v(TAG, " Updated..." + updateNote);
            } else {
                if (LOG_DEBUG) Log.v(TAG, " Unusual Happened..");
            }
            dbMgr.closeDataBase();
            return 1;
        }
    }


    //common operation for all,
    public static List<NoteModel> extractCommonData(Cursor cursor, List<NoteModel> noteModelList) {
        noteModelList = new ArrayList<>();

        if (LOG_DEBUG) Log.i(TAG, "inside extractCommonData()");
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
        return noteModelList;
    }

}
