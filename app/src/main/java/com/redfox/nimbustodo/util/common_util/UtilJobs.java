package com.redfox.nimbustodo.util.common_util;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.redfox.nimbustodo.job.DbAutoDeleteJob;
import com.redfox.nimbustodo.job.MoveToArchiveJob;
import com.redfox.nimbustodo.job.WeatherJobService;


public class UtilJobs {

    private final static String TAG = UtilJobs.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;

    public static void setUpWeatherJob(Context context) {
        if (LOG_DEBUG) Log.e(TAG, "---------setUpWeatherJob----");
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job myJob = dispatcher.newJobBuilder()
                .setService(WeatherJobService.class)
                .setTag(WeatherJobService.class.getSimpleName())
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setTrigger(Trigger.executionWindow(5 * 60, 15 * 60))
                .addConstraint(Constraint.ON_ANY_NETWORK)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    public static void setUpAutoDeleteJob(Context context) {
        if (LOG_DEBUG) Log.e(TAG, "---------setUpAutoDeleteJob----");

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job myJob = dispatcher.newJobBuilder()
                .setService(DbAutoDeleteJob.class)
                .setTag(DbAutoDeleteJob.class.getSimpleName())
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setTrigger(Trigger.executionWindow(30 * 60 * 60, 32 * 60 * 60))
                .addConstraint(Constraint.DEVICE_CHARGING)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    public static void setUpMoveToArchiveJob(Context context) {
        if (LOG_DEBUG) Log.e(TAG, "---------setUpMoveToArchiveJob----");

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job myJob = dispatcher.newJobBuilder()
                .setService(MoveToArchiveJob.class)
                .setTag(MoveToArchiveJob.class.getSimpleName())
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setTrigger(Trigger.executionWindow(7 * 60 * 60, 9 * 60 * 60))
                .addConstraint(Constraint.DEVICE_CHARGING)
                .build();

        dispatcher.mustSchedule(myJob);
    }
}
