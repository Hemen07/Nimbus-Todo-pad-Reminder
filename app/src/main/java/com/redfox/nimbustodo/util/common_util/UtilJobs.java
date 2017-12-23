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
import com.redfox.nimbustodo.ui.activity.MainActivity;

/**
 * Created by notTdar on 12/23/2017.
 */

public class UtilJobs {

    public static void setUpPeriodicJob(Context context) {
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
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job myJob = dispatcher.newJobBuilder()
                .setService(DbAutoDeleteJob.class)
                .setTag(DbAutoDeleteJob.class.getSimpleName())
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setTrigger(Trigger.executionWindow(16 * 60 * 60, 24 * 60 * 60))
                .addConstraint(Constraint.DEVICE_CHARGING | Constraint.ON_ANY_NETWORK)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    public static void setUpMoveToArchiveJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job myJob = dispatcher.newJobBuilder()
                .setService(MoveToArchiveJob.class)
                .setTag(MoveToArchiveJob.class.getSimpleName())
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setTrigger(Trigger.executionWindow(2 * 60 * 60, 3 * 60 * 60))
                .addConstraint(Constraint.ON_ANY_NETWORK | Constraint.DEVICE_CHARGING)
                .build();

        dispatcher.mustSchedule(myJob);
    }
}
