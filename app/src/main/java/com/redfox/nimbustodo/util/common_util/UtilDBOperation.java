package com.redfox.nimbustodo.util.common_util;

import android.text.TextUtils;
import android.util.Log;

import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.data.db.DBMgr;
import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.ui.activity.NoteUpdateActivity;

import java.util.Calendar;


public class UtilDBOperation {

    private final static boolean LOG_DEBUG = true;
    private final static String TAG = NoteUpdateActivity.class.getSimpleName();

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

        if (LOG_DEBUG) Log.e(TAG, " Before Updation : Notel Model Has " + noteModel);

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

}
