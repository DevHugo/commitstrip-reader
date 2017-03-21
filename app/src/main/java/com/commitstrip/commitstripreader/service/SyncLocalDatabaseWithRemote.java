package com.commitstrip.commitstripreader.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.batch.SaveStripBatchTask;
import com.commitstrip.commitstripreader.common.JobListener;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;

import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SyncLocalDatabaseWithRemote {

    private Flowable<StripDto> flowableSyncDabatase;

    @NonNull
    private boolean shouldReschedule = true;

    public void startSynchronize(Context context, JobListener jobListener) {

        if (jobListener == null) {
            throw new IllegalArgumentException("JobListener should not be null.");
        }

        if (context == null) {
            throw new IllegalArgumentException("Context should not be null.");
        }

        // Trigger pre-execute
        jobListener.onPreExecute();

        // For avoiding memory leak, we have put a WeakReferences.
        WeakReference mWeakJobListener = new WeakReference<>(jobListener);

        // Get a repository instance
        MyApp myApp = ((MyApp) context.getApplicationContext());

        StripRepository stripRepository = myApp.getDataSourceComponent().getStripRepository();

        // Try to resume from the last sync
        Flowable<StripDto> strips =
                stripRepository
                        .fetchAllStrip()
                        .onErrorReturnItem(new ArrayList<>())
                        .flatMap(Flowable::fromIterable);

        // Save all item in strips flux
        SaveStripBatchTask syncLocalDatabase = new SaveStripBatchTask(stripRepository);
        flowableSyncDabatase = syncLocalDatabase.execute(strips);

        // Callback we should call during saving
        Consumer<StripDto> onNext = strip -> {
        };
        Consumer<Throwable> onError = error -> {

            // Time to finish the job
            jobListener.onCancelled();
        };
        Action onComplete = () -> {
            JobListener mJobListener = (JobListener) mWeakJobListener.get();

            if (mJobListener != null) {
                jobListener.onPostExecute();
            }
        };

        flowableSyncDabatase.blockingSubscribe(onNext, onError, onComplete);
    }

    public boolean stop() {
        if (flowableSyncDabatase != null) {
            flowableSyncDabatase.unsubscribeOn(Schedulers.newThread());
        }

        return shouldReschedule;
    }

}
