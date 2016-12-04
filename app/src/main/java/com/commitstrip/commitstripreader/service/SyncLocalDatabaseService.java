package com.commitstrip.commitstripreader.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;

import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.data.source.StripRepositorySingleton;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SyncLocalDatabaseService extends JobService {

    @NonNull private String TAG = "SyncLocalDatabase";

    @NonNull private boolean shouldReschedule = true;

    @NonNull private WeakReference<SyncLocalDatabaseTask> mWeakSyncLocalDatabase;

    @NonNull private JobParameters mJobParameters;
    @NonNull private WeakReference<Context> mWeakContext;
    private Flowable<StripDto> flowableSyncDabatase;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        if (jobParameters == null)
            throw new IllegalArgumentException();

        // Be warned job parameters are leaked in memory, it used in consumer callback.
        mJobParameters = jobParameters;

        // Not our first run now !
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putBoolean("firstrun", false).apply();

        // Get application context
        mWeakContext = new WeakReference<>(getApplicationContext());

        // Get a repository instance
        StripRepositorySingleton stripRepositorySingleton = StripRepositorySingleton.getInstance(getApplicationContext());
        StripRepository stripRepository = stripRepositorySingleton.getStripRepositoryComponent().getStripRepository();

        // Get a instance of an internal storage
        File internalStorage = stripRepositorySingleton.getLocalStorageComponent().provideInternalStorage();

        // Try to resume from the last sync
        ResumeSyncLocalDatabase resumeSyncLocalDatabase = new ResumeSyncLocalDatabase(stripRepository, internalStorage);
        Flowable<StripDto> strips = resumeSyncLocalDatabase.resumeFromLastSync();

        // Save all item in strips flux
        SyncLocalDatabaseTask syncLocalDatabase = new SyncLocalDatabaseTask(stripRepository);
        mWeakSyncLocalDatabase = new WeakReference<>(syncLocalDatabase);

        flowableSyncDabatase = mWeakSyncLocalDatabase.get().execute(strips);

        // Callback we should call during saving
        Consumer<StripDto> onNext = strip -> {};
        Consumer<Throwable> onError = error -> {

            // Try to save the current progress
            saveCurrentProgress();

            // Time to finish the job
            jobFinished(mJobParameters, shouldReschedule);
        };
        Action onComplete = () -> {
            Context context = mWeakContext.get();

            if (context != null) {
                SharedPreferences sharedPreferencesInComplete =
                        PreferenceManager.getDefaultSharedPreferences(context);

                sharedPreferencesInComplete
                        .edit()
                        .putBoolean(Configuration.SHAREDPREFERENCES_KEY_DATABASE_SYNC_OK, true)
                        .apply();

                shouldReschedule = false;

                jobFinished(mJobParameters, shouldReschedule);
            }
            else {
                jobFinished(mJobParameters, shouldReschedule);
            }
        };

        flowableSyncDabatase.subscribe(onNext, onError, onComplete);
        
        return true; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        if (shouldReschedule) {
            if (flowableSyncDabatase != null) {
                flowableSyncDabatase.unsubscribeOn(Schedulers.newThread());
            }
        }

        return shouldReschedule; // Answers the question: "Should this job be retried?"
    }

    private void saveCurrentProgress () {
        SyncLocalDatabaseTask weakSyncLocalDatabase = mWeakSyncLocalDatabase.get();

        if (weakSyncLocalDatabase != null) {
            LongSparseArray<StripDto> progress = weakSyncLocalDatabase.getStripToProcess();

            if (progress != null && progress.size() >= 1) {

                // Get application context
                Context context = mWeakContext.get();

                if (context != null) {
                    // Get a repository instance
                    StripRepositorySingleton stripRepositorySingleton = StripRepositorySingleton.getInstance(context);
                    StripRepository stripRepository = stripRepositorySingleton.getStripRepositoryComponent().getStripRepository();

                    // Get a instance of an internal storage
                    File internalStorage = stripRepositorySingleton.getLocalStorageComponent().provideInternalStorage();

                    // Get a new resume instance
                    ResumeSyncLocalDatabase resumeSyncLocalDatabase = new ResumeSyncLocalDatabase(stripRepository, internalStorage);
                    resumeSyncLocalDatabase.saveCurrentProgression(asList(progress));
                }
            }
        }
    }

    private static <C> List<C> asList(LongSparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }


}
