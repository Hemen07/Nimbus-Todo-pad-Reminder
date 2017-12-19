package com.redfox.nimbustodo.data.db;

public class DBSchema {

    public final static String DB_NAME = "note_db";
    public final static int DB_VERSION = 10;
    public final static String DB_TABLE_NAME = "note_table";
    //columns
    public final static String DB_ROW_ID = "_id";
    public final static String DB_TITLE = "_title";
    public final static String DB_IMAGE_PATH = "_imgUri";
    public final static String DB_SUB_TEXT = "_subText";
    public final static String DB_CREATE_DATE = "_createDate";
    public final static String DB_UPDATE_DATE = "_updateDate";
    public final static String DB_SCHEDULED_TIME_LONG = "_scheduledLong";
    public final static String DB_SCHEDULED_TIME_WHEN = "_scheduledWhen";
    public final static String DB_SCHEDULED_TITLE = "_scheduledTitle";
    public final static String DB_IS_ALARM_SCHEDULED = "_isAlarmScheduled";
    public final static String DB_IS_TASK_DONE = "_isTaskDone";
    public final static String DB_IS_ARCHIVED = "_isArchived";


    public final static String DB_STATEMENT = " CREATE TABLE " + DB_TABLE_NAME +
            "(" +
            DB_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DB_TITLE + " TEXT NOT NULL, " +
            DB_IMAGE_PATH + " TEXT NOT NULL, " +
            DB_SUB_TEXT + " TEXT NOT NULL, " +
            DB_CREATE_DATE + " TEXT NOT NULL, " +
            DB_UPDATE_DATE + " TEXT NOT NULL, " +
            DB_SCHEDULED_TIME_LONG + " TEXT NOT NULL, " +
            DB_SCHEDULED_TIME_WHEN + " TEXT NOT NULL, " +
            DB_SCHEDULED_TITLE + " TEXT NOT NULL, " +
            DB_IS_ALARM_SCHEDULED + " TEXT NOT NULL, " +
            DB_IS_TASK_DONE + " TEXT NOT NULL, " +
            DB_IS_ARCHIVED + " TEXT NOT NULL" + ");";


    public final static String DB_SELECT_ALL = " SELECT * FROM " + DB_TABLE_NAME;
}
