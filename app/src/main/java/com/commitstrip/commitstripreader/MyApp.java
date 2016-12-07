package com.commitstrip.commitstripreader;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
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

public class MyApp extends MultiDexApplication {

    private String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            // Permit to use debug bridge for Android applications. For more detail, please see http://facebook.github.io/stetho/.
            Stetho.initializeWithDefaults(this);

            // Display FPS
            TinyDancer.create().show(this);
        }
    }

}