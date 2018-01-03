package com.redfox.nimbustodo.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.redfox.nimbustodo.data.model.NoteModel;
import com.redfox.nimbustodo.util.common_util.UtilLogger;


public class DBHelperSingleton extends SQLiteOpenHelper {

    private final static String TAG = DBHelperSingleton.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;


    private static DBHelperSingleton mDBDbHelperInstance;
    private static SQLiteDatabase mSqLiteDatabase;

    public static DBHelperSingleton getDbInstance(Context context) {
        Log.i(TAG, " getDbInstance()");

        if (mDBDbHelperInstance == null) {
            Log.e(TAG, " mDBDbHelperInstance == NULL : initialize it");
            mDBDbHelperInstance = new DBHelperSingleton(context.getApplicationContext());
            openDB();
        } else {
            //intentional
            Log.e(TAG, " mDBDbHelperInstance != NOT NULL : return existing one ");

        }
        return mDBDbHelperInstance;
    }

    private DBHelperSingleton(Context context) {
        super(context, DBSchema.DB_NAME, null, DBSchema.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(DBSchema.DB_STATEMENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + DBSchema.DB_TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
    //-----------------------------------------------------------------------------------------------------


    private static void openDB() {
        Log.i(TAG, " openDB() ");
        if (mSqLiteDatabase == null) {
            Log.v(TAG, " mSqLiteDatabase == NULL  : initialize");
            mSqLiteDatabase = mDBDbHelperInstance.getWritableDatabase();
        } else {
            //intentional
            Log.v(TAG, " mSqLiteDatabase != NOT NULL  : return existing one");

        }
    }

    public synchronized void closeDB() {
        Log.i(TAG, "closeDB()");
        if (mDBDbHelperInstance != null) {
            Log.v(TAG, " mDBDbHelperInstance != NOT NULL  : close it : purge it");
            mDBDbHelperInstance.close();
            mSqLiteDatabase.close();
            mDBDbHelperInstance = null;
            mSqLiteDatabase = null;
        } else {
            Log.v(TAG, " mDBDbHelperInstance == NULL  : leave it ");

        }
    }

    //-----------------------------------------------------------------------------------------------------
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

            long rowId = mSqLiteDatabase.insert(DBSchema.DB_TABLE_NAME, null, contentValues);
            if (LOG_DEBUG) Log.w(TAG, " insert Done :  at row Id: " + rowId);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long updateNote(NoteModel noteModel) {
        if (LOG_DEBUG) UtilLogger.showLogUpdate(TAG, noteModel, noteModel.getRow_pos());
        long updatedRow = 0;

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


            updatedRow = mSqLiteDatabase.updateWithOnConflict(
                    DBSchema.DB_TABLE_NAME,
                    contentValues,
                    DBSchema.DB_ROW_ID + " =?", new String[]{String.valueOf(noteModel.get_id())}, mSqLiteDatabase.CONFLICT_REPLACE);

            return updatedRow;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedRow;
    }


    public int deleteNote(int _id) {
        int status = 0;
        if (LOG_DEBUG) Log.w(TAG, "deleteNote : passed id " + _id);
        status = mSqLiteDatabase.delete(DBSchema.DB_TABLE_NAME, DBSchema.DB_ROW_ID + "=?",
                new String[]{String.valueOf(_id)});
        if (status == 1) {
            if (LOG_DEBUG) Log.w(TAG, " deleted Successfully :  " + status);
            return status;
        }
        return status;
    }

    //----------------------------------------------------------------------------------------------------------

    public Cursor getCursor() {
        return mSqLiteDatabase.rawQuery(DBSchema.DB_SELECT_ALL + " order by " + DBSchema.DB_CREATE_DATE + " DESC;", null);
    }

    public Cursor getCursorSearch(String passedId) {
        if (LOG_DEBUG) Log.w(TAG, " passed ID : " + passedId);
        return mSqLiteDatabase.rawQuery(DBSchema.DB_SELECT_ALL +
                " WHERE " + DBSchema.DB_ROW_ID + " = " + passedId, null);
    }

    public Cursor getCursorForArchived(String isArchived) {
        if (LOG_DEBUG) Log.w(TAG, " isArchived " + isArchived);
        return mSqLiteDatabase.rawQuery(DBSchema.DB_SELECT_ALL +
                " WHERE " + DBSchema.DB_IS_ARCHIVED + " = " + isArchived, null);
    }

    public Cursor getCursorForTaskDone(String isTaskDone) {
        if (LOG_DEBUG) Log.w(TAG, " isTaskDone " + isTaskDone);
        return mSqLiteDatabase.rawQuery(DBSchema.DB_SELECT_ALL +
                " WHERE " + DBSchema.DB_IS_TASK_DONE + " = " + isTaskDone, null);
    }

    public Cursor getCursorForAlarmScheduled(String passAlarmScheduledStatus) {
        if (LOG_DEBUG)
            Log.w(TAG, " pick all record with alarmScheduled 1  : " + passAlarmScheduledStatus);
        return mSqLiteDatabase.rawQuery(DBSchema.DB_SELECT_ALL +
                " WHERE " + DBSchema.DB_IS_ALARM_SCHEDULED + " = " + passAlarmScheduledStatus, null);
    }
}
