package com.redfox.nimbustodo.util.common_util;

import android.util.Log;

import com.redfox.nimbustodo.data.model.NoteModel;

public class UtilLogger {

    private static final boolean LOG_DEBUG = true;

    public static void showLogInsert(String TAG, NoteModel noteModel) {

        if (LOG_DEBUG) {
            Log.w(TAG, " insertNote() : " + "\n" +
                    " , _id :" + String.valueOf(noteModel.get_id()) + "\n" +
                    " , title : " + noteModel.getTitle() + "\n" +
                    " , imageUriPath :" + noteModel.getImgUriPath() + "\n" +
                    " , rowClickedPos : " + noteModel.getRow_pos() + "\n" +
                    " , subText : " + noteModel.getSub_text() + "\n" +
                    " , createDate : " + noteModel.getCreateDate() + " == " + UtilCal.formatDate(noteModel.getCreateDate()) + "\n" +
                    " , updateDate : " + noteModel.getUpdateDate() + " == " + UtilCal.formatDate(noteModel.getUpdateDate()) + "\n" +
                    " , scheduledTimeLong : " + noteModel.getScheduleTimeLong() + "\n" +
                    " , scheduledWhenLong : " + noteModel.getScheduledWhenLong() + " == " + UtilCal.formatDate(noteModel.getScheduledWhenLong()) + "\n" +
                    " , scheduledTitle : " + noteModel.getScheduledTitle() + "\n" +
                    " , isAlarmScheduled : " + noteModel.getIsAlarmScheduled() +
                    " , isTaskDone : " + noteModel.getIsTaskDone() +
                    " , isArchived : " + noteModel.getIsArchived() + "\n" +

                    "\n");
        }
    }


    public static void showLogUpdate(String TAG, NoteModel noteModel, int whichOne) {
        if (LOG_DEBUG) {
            Log.w(TAG, " updateNote() : " + "\n" +
                    " , _id :" + String.valueOf(noteModel.get_id()) + "\n" +
                    " , title : " + noteModel.getTitle() + "\n" +
                    " , imageUriPath :" + noteModel.getImgUriPath() + "\n" +
                    " , rowClickedPos : " + noteModel.getRow_pos() + "\n" +
                    " , subText : " + noteModel.getSub_text() + "\n" +
                    " , createDate : " + noteModel.getCreateDate() + " == " + UtilCal.formatDate(noteModel.getCreateDate()) + "\n" +
                    " , updateDate : " + noteModel.getUpdateDate() + " == " + UtilCal.formatDate(noteModel.getUpdateDate()) + "\n" +
                    " , scheduledTimeLong : " + noteModel.getScheduleTimeLong() + "\n" +
                    " , scheduledWhenLong : " + noteModel.getScheduledWhenLong() + " == " + UtilCal.formatDate(noteModel.getScheduledWhenLong()) + "\n" +
                    " , scheduledTitle : " + noteModel.getScheduledTitle() + "\n" +
                    " , isAlarmScheduled : " + noteModel.getIsAlarmScheduled() +
                    " , isTaskDone : " + noteModel.getIsTaskDone() +
                    " , isArchived : " + noteModel.getIsArchived() + "\n" +
                    "\n");
        }
    }

    public static void logWhatWeHave(String TAG, int recordPosId, String title, int imageUriPath, String subText
            , String dateCreation, String dateUpdated, long scheduledTimeLong, long scheduledWhenLong
            , String scheduleTitle, int alarmScheduled, int isTaskDone, int isArchived) {
        if (LOG_DEBUG) Log.v(TAG, " let's see what we have stored " + "\n" +
                recordPosId + "\n" +
                title + "\n" +
                imageUriPath + "\n" +
                subText + "\n" +
                dateCreation + "\n" +
                dateUpdated + "\n" +
                scheduledTimeLong + "\n" +
                scheduledWhenLong + "\n" +
                scheduleTitle + "\n" +
                alarmScheduled + "\n" +
                isTaskDone + "\n" +
                isArchived);

    }

    public static void logAMgr(String TAG, String title, long fireAt, long whenScheduled, int recordID) {
        if (LOG_DEBUG) {
            Log.e(TAG, " \n" +
                    " _schedule_alarm() : " + "\n" +
                    " title : " + title + "\n" +
                    " fireAt : " + fireAt + "\n" +
                    " was scheduled : " + whenScheduled + "\n" + UtilCal.formatDatePattern4(whenScheduled) + "\n" +
                    " record_id " + recordID);
        }
    }

    public static void logReceiver(String TAG, String title, long whenScheduled, int recordId) {
        if (LOG_DEBUG) {
            Log.d(TAG, "  \n" +
                    "_inside Receiver :" +
                    " title : " + title + " \n " +
                    " was scheduled : " + whenScheduled + "\n" + UtilCal.formatDatePattern4(whenScheduled) + "\n" +
                    " record_id : " + recordId);
        }
    }

    public static void logNotification(String TAG, String title, long whenScheduled, int recordId) {
        if (LOG_DEBUG) {
            Log.v(TAG, " _create_notification() :" +
                    " title : " + title + " \n " +
                    " was scheduled : " + whenScheduled + "\n" + UtilCal.formatDatePattern4(whenScheduled) + "\n" +
                    " record_id : " + recordId);
        }
    }

    public static void logDummy(String TAG, String title, long whenScheduled, int recordId) {
        if (LOG_DEBUG) {
            Log.i(TAG, " _inside dummy :" +
                    " title : " + title + " \n " +
                    " was scheduled : " + whenScheduled + "\n" + UtilCal.formatDatePattern4(whenScheduled) + "\n" +
                    " record_id : " + recordId);
        }
    }


}
