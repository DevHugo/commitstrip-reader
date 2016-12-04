package com.commitstrip.commitstripreader;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;

import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.service.SyncLocalDatabaseService;
import com.facebook.stetho.Stetho;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import com.codemonkeylabs.fpslibrary.*;

public class MyApp extends Application {

    private static final String JOB_ID = Configuration.JOB_ID_SYNC_LOCAL_DATABASE;

    private String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();

        // Ask Firebase to register the device in the topic. Used for receiving notification.
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseMessaging.getInstance().subscribeToTopic(Configuration.TOPIC_NAME);

        // Get user preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // If it our first run, we schedule a job for synchronize the database.
        if (!sharedPreferences.getBoolean("firstrun", false)) {

            // Create a new dispatcher using the Google Play driver.
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

            // Schedule the job
            Job myJob = dispatcher.newJobBuilder()
                    .setService(SyncLocalDatabaseService.class)
                    .setTag(JOB_ID)
                    .setRecurring(false)
                    .setLifetime(Lifetime.FOREVER)
                    .setTrigger(Trigger.executionWindow(0, 60))
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .build();

            dispatcher.mustSchedule(myJob);

            startService(new Intent(this, SyncLocalDatabaseService.class));
        }

        if (BuildConfig.DEBUG) {
            // Permit to use debug bridge for Android applications. For more detail, please see http://facebook.github.io/stetho/.
            Stetho.initializeWithDefaults(this);

            // Display FPS
            TinyDancer.create().show(this);
        }
    }

}