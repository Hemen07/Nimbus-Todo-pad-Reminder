package com.redfox.nimbustodo.util.alarm_util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.data.db.DBHelperSingleton;
import com.redfox.nimbustodo.data.db.DBSchema;
import com.redfox.nimbustodo.util.common_util.UtilCal;
import com.redfox.nimbustodo.util.common_util.UtilLogger;


public class UtilNotification {

    private final static String TAG = UtilNotification.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;

    private NotificationManager notificationManager;
    private int NOTIFICATION_ID = 0;
    private Notification notification;

    //    Channel
    private static final String NOTIFICATION_CHANNEL_ID = "REMAINDER_CHANNEL_ID";
    private static final String NOTIFICATION_CHANNEL_NAME = "REMAINDER_CHANNEL_NAME";
    private static final String NOTIFICATION_CHANNEL_DESCRIPTION = "REMAINDER_CHANNEL_DESCRIPTION";

    //    Group
    private static final String NOTIFICATION_GROUP_ID = "REMAINDER_GROUP";
    private static final String NOTIFICATION_GROUP_NAME = "REMAINDER_GROUP_NAME";


    //btn action DONE Dismiss
    private int BTN_REQ_CODE = 0;
    public static final String BTN_ACTION_NAME = "BTN_ACTION_DISMISS";
    public static final String BTN_ACTION_TITLE = "BTN_ACTION_TITLE";
    public static final String BTN_ACTION_WHENSCHEDULED = "BTN_ACTION_WHENSCHEDULED";
    public static final String BTN_ACTION_RECORD_ID = "BTN_ACTION_RECORD_ID";
    public static final String BTN_ACTION_NOTIFICATION_KEY = "BTN_ACTION_NOTIFICATION_KEY";

    //dismiss event
    private int DISMISS_REQ_CODE = 0;
    public static final String DISMISS_ACTION_NAME = "ACTION_DISMISS";
    public static final String DISMISS_ACTION_TITLE = "DISMISS_ACTION_TITLE";
    public static final String DISMISS_ACTION_WHENSCHEDULED = "DISMISS_ACTION_WHENSCHEDULED";
    public static final String DISMISS_ACTION_RECORD_ID = "DISMISS_ACTION_RECORD_ID";
    public static final String DISMISS_ACTION_NOTIFICATION_KEY = "DISMISS_ACTION_NOTIFICATION_KEY";

    private int imageUriPath;

    public void createNotification(Context context, String noteTitle, long whenScheduled, int recordId) {
        if (LOG_DEBUG) UtilLogger.logNotification(TAG, noteTitle, whenScheduled, recordId);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NOTIFICATION_ID = NOTIFICATION_ID + recordId;
        BTN_REQ_CODE = BTN_REQ_CODE + recordId;
        DISMISS_REQ_CODE = DISMISS_REQ_CODE + recordId;

        pickEntryById(context, recordId);

        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.custom_expanded_notification);
        expandedView.setTextViewText(R.id.timestamp_notificationTv, "@ " + UtilCal.formatDatePattern4(whenScheduled));
        expandedView.setTextViewText(R.id.cE_noteMsg_tv, noteTitle);
        expandedView.setImageViewResource(R.id.big_icon_notificationImv, R.drawable.ic_notification);
        expandedView.setImageViewResource(R.id.small_icon_notificationImv, R.drawable.ic_info_small_notification);
        expandedView.setImageViewResource(R.id.cE_bg_imv, imageUriPath);
        expandedView.setImageViewResource(R.id.cE_action_imv, R.drawable.ic_check_notification);

        //CollapsedRemote

        RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.custom_collapsed_notification);
        collapsedView.setTextViewText(R.id.timestamp_notificationTv, "@ " + UtilCal.formatDatePattern4(whenScheduled));
        collapsedView.setImageViewResource(R.id.big_icon_notificationImv, R.drawable.ic_notification);
        collapsedView.setImageViewResource(R.id.small_icon_notificationImv, R.drawable.ic_info_small_notification);

        // action button : intent + pi
        setUpIntentActionBtn(context, noteTitle, whenScheduled, recordId, expandedView);

        PendingIntent piDismiss = setUpIntentDismissEvent(context, noteTitle, whenScheduled, recordId);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            if (LOG_DEBUG) Log.v(TAG, " : version : <=M ");

            //noinspection deprecation
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(context.getString(R.string.notification_subtext))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .setLights(Color.CYAN, 500, 1200)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDeleteIntent(piDismiss)
                    //.setContentIntent(pIpanel)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle());

            notification = builder.build();

            notification.contentView = collapsedView;
            notification.bigContentView = expandedView;

        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N | Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            if (LOG_DEBUG) Log.v(TAG, " : version : N| N1 - 24: 25 ");

            //noinspection deprecation
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(context.getString(R.string.notification_subtext))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .setLights(Color.CYAN, 500, 1200)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDeleteIntent(piDismiss)
                    //.setContentIntent(pIpanel)
                    .setCustomContentView(collapsedView)
                    .setCustomBigContentView(expandedView)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle());

            notification = builder.build();


        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (LOG_DEBUG) Log.v(TAG, " : version : >=O ");


            NotificationChannel mChannel = new NotificationChannel
                    (NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

            mChannel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.CYAN);
            mChannel.enableVibration(true);
            notificationManager.createNotificationChannel(mChannel);

            NotificationChannelGroup mGroup = new NotificationChannelGroup(NOTIFICATION_GROUP_ID, NOTIFICATION_GROUP_NAME);
            notificationManager.createNotificationChannelGroup(mGroup);

            NotificationCompat.Builder builder = new NotificationCompat.Builder
                    (context, NOTIFICATION_CHANNEL_ID)

                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(context.getString(R.string.notification_subtext))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .setDeleteIntent(piDismiss)
                    //.setContentIntent(pIpanel)
                    .setCustomContentView(collapsedView)
                    .setCustomBigContentView(expandedView)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle());

            notification = builder.build();
        }

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private void setUpIntentActionBtn(Context context, String noteTitle, long whenScheduled, int recordId, RemoteViews expandedView) {
        Intent actionIntent = new Intent(context, DummyBroadcast.class);
        actionIntent.setAction(BTN_ACTION_NAME);

        Bundle bundle = new Bundle();
        bundle.putString(BTN_ACTION_TITLE, noteTitle);
        bundle.putLong(BTN_ACTION_WHENSCHEDULED, whenScheduled);
        bundle.putInt(BTN_ACTION_RECORD_ID, recordId);
        bundle.putInt(BTN_ACTION_NOTIFICATION_KEY, NOTIFICATION_ID);
        actionIntent.putExtras(bundle);
        // action button : Pending Intent
        PendingIntent piAction = PendingIntent.getBroadcast(context, BTN_REQ_CODE, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        expandedView.setOnClickPendingIntent(R.id.cE_action_btn, piAction);

    }

    private PendingIntent setUpIntentDismissEvent(Context context, String noteTitle, long whenScheduled, int recordId) {
        Intent dismissIntent = new Intent(context, DummyBroadcast.class);
        dismissIntent.setAction(DISMISS_ACTION_NAME);

        Bundle bundle = new Bundle();
        bundle.putString(DISMISS_ACTION_TITLE, noteTitle);
        bundle.putLong(DISMISS_ACTION_WHENSCHEDULED, whenScheduled);
        bundle.putInt(DISMISS_ACTION_RECORD_ID, recordId);
        bundle.putInt(DISMISS_ACTION_NOTIFICATION_KEY, NOTIFICATION_ID);
        dismissIntent.putExtras(bundle);
        // action button : Pending Intent
        return PendingIntent.getBroadcast(context, DISMISS_REQ_CODE, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void pickEntryById(Context context, int recordId) {

        Cursor cursor = DBHelperSingleton.getDbInstance(context).getCursorSearch(String.valueOf(recordId));
        cursorData(cursor);

    }

    private void cursorData(Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    imageUriPath = cursor.getInt(cursor.getColumnIndex(DBSchema.DB_IMAGE_PATH));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }
}
