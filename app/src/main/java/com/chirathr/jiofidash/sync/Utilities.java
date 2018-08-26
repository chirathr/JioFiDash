package com.chirathr.jiofidash.sync;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

public class Utilities {
    private static final int BATTERY_INTERVAL_MINUTES = 6;
    private static final int BATTERY_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(BATTERY_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = BATTERY_INTERVAL_SECONDS;

    private static final String BATTERY_JOB_TAG = "battery_reminder_tag";

    private static boolean sInitialized;

    // Checks and show a notification if battery is low.
    synchronized public static void scheduleBatteryJob(@NonNull final Context context) {
        if (sInitialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job batteryJob = dispatcher.newJobBuilder()
                .setService(BatteryReminderJobService.class)
                .setTag(BATTERY_JOB_TAG)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setTrigger(Trigger.executionWindow(
                        BATTERY_INTERVAL_SECONDS,
                        BATTERY_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(batteryJob);


        sInitialized = true;
    }
}
