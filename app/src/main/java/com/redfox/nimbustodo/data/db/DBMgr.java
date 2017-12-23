package com.redfox.nimbustodo.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.util.common_util.UtilLogger;

import java.util.ArrayList;
import java.util.List;


public class DBMgr {

    private final static String TAG = DBMgr.class.getSimpleName();
    private final static boolean LOG_DEBUG = true;

    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private List<NoteModel> noteModelList = new ArrayList<>();

    public DBMgr(Context context) {
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }


    public DBMgr openDataBase() {
        if (LOG_DEBUG) Log.w(TAG, "openDataBase()");
        try {
            sqLiteDatabase = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void closeDataBase() {
        if (LOG_DEBUG) Log.w(TAG, "closeDataBase()");
        try {
            dbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insertNote(NoteModel noteModel) {
        if (LOG_DEBUG) UtilLogger.showLogInsert(TAG, noteModel);

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBSchema.DB_TITLE, noteModel.getTitle());
            contentValues.put(DBSchema.DB_IMAGE_PATH, noteModel.getImgUriPath());
            contentValues.put(DBSchema.DB_SUB_TEXT, noteModel.getSub_text());
            contentValues.put(DBSchema.DB_CREATE_DATE, noteModel.getCreateDate());
            contentValues.put(DBSchema.DB_UPDATE_DATE, noteModel.getUpdateDate());
            contentValues.put(DBSchema.DB_SCHEDULED_TIME_LONG, noteModel.getScheduleTimeLong());
            contentValues.put(DBSchema.DB_SCHEDULED_TIME_WHEN, noteModel.getScheduledWhenLong());
            contentValues.put(DBSchema.DB_SCHEDULED_TITLE, noteModel.getScheduledTitle());
            contentValues.put(DBSchema.DB_IS_ALARM_SCHEDULED, noteModel.getIsAlarmScheduled());
            contentValues.put(DBSchema.DB_IS_TASK_DONE, noteModel.getIsTaskDone());
            contentValues.put(DBSchema.DB_IS_ARCHIVED, noteModel.getIsArchived());

            long rowId = sqLiteDatabase.insert(DBSchema.DB_TABLE_NAME, null, contentValues);
            if (LOG_DEBUG) Log.w(TAG, " insert Done :  at row Id: " + rowId);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long updateNote(NoteModel noteModel) {
        if (LOG_DEBUG) UtilLogger.showLogUpdate(TAG, noteModel, noteModel.getRow_pos());

        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(DBSchema.DB_TITLE, noteModel.getTitle());
            contentValues.put(DBSchema.DB_IMAGE_PATH, noteModel.getImgUriPath());
            contentValues.put(DBSchema.DB_SUB_TEXT, noteModel.getSub_text());
            contentValues.put(DBSchema.DB_CREATE_DATE, noteModel.getCreateDate());
            contentValues.put(DBSchema.DB_UPDATE_DATE, noteModel.getUpdateDate());
            contentValues.put(DBSchema.DB_SCHEDULED_TIME_LONG, noteModel.getScheduleTimeLong());
            contentValues.put(DBSchema.DB_SCHEDULED_TIME_WHEN, noteModel.getScheduledWhenLong());
            contentValues.put(DBSchema.DB_SCHEDULED_TITLE, noteModel.getScheduledTitle());
            contentValues.put(DBSchema.DB_IS_ALARM_SCHEDULED, noteModel.getIsAlarmScheduled());
            contentValues.put(DBSchema.DB_IS_TASK_DONE, noteModel.getIsTaskDone());
            contentValues.put(DBSchema.DB_IS_ARCHIVED, noteModel.getIsArchived());


            long updatedRow = sqLiteDatabase.updateWithOnConflict(
                    DBSchema.DB_TABLE_NAME,
                    contentValues,
                    DBSchema.DB_ROW_ID + " =?", new String[]{String.valueOf(noteModel.get_id())}, SQLiteDatabase.CONFLICT_REPLACE);

            return updatedRow;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int deleteNote(int _id) {
        if (LOG_DEBUG) Log.w(TAG, "deleteNote : passed id " + _id);
        int status = sqLiteDatabase.delete(DBSchema.DB_TABLE_NAME, DBSchema.DB_ROW_ID + "=?",
                new String[]{String.valueOf(_id)});
        return status;
    }

    //Getting Cursor : based on requirements
    //complete record
    public Cursor getCursor() {
        return sqLiteDatabase.rawQuery(DBSchema.DB_SELECT_ALL + " order by " + DBSchema.DB_CREATE_DATE + " DESC;", null);
    }

    //search/pick  with ID
    public Cursor getCursorSearch(String passedId) {
        if (LOG_DEBUG) Log.w(TAG, " passed ID : " + passedId);
        return sqLiteDatabase.rawQuery(DBSchema.DB_SELECT_ALL +
                " WHERE " + DBSchema.DB_ROW_ID + " = " + passedId, null);
    }

    //search/pick : by isArchived
    public Cursor getCursorForArchived(String isArchived) {
        if (LOG_DEBUG) Log.w(TAG, " isArchived " + isArchived);
        return sqLiteDatabase.rawQuery(DBSchema.DB_SELECT_ALL +
                " WHERE " + DBSchema.DB_IS_ARCHIVED + " = " + isArchived, null);
    }

    //search/pick : by isArchived
    public Cursor getCursorForTaskDone(String isTaskDone) {
        if (LOG_DEBUG) Log.w(TAG, " isTaskDone " + isTaskDone);
        return sqLiteDatabase.rawQuery(DBSchema.DB_SELECT_ALL +
                " WHERE " + DBSchema.DB_IS_TASK_DONE + " = " + isTaskDone, null);
    }

    //search/pick  with alarmScheduledStatus
    public Cursor getCursorForAlarmScheduled(String passAlarmScheduledStatus) {
        if (LOG_DEBUG)
            Log.w(TAG, " pick all record with alarmScheduled 1  : " + passAlarmScheduledStatus);
        return sqLiteDatabase.rawQuery(DBSchema.DB_SELECT_ALL +
                " WHERE " + DBSchema.DB_IS_ALARM_SCHEDULED + " = " + passAlarmScheduledStatus, null);
    }


}
