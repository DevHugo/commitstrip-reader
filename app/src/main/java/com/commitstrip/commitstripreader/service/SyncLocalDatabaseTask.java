package com.commitstrip.commitstripreader.service;

import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;

import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.data.source.local.converter.ListStripDaoToListStripDto;
import com.commitstrip.commitstripreader.dto.StripDto;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Permit to schedule a task that will try to synchronize all the remote database with the local database.
 *
 * This class doesn't handle the case where id have been changed on the server.
 * If something like that happen, you should erase all the local database and call SyncLocalDatabaseTask again.
 */
public class SyncLocalDatabaseTask {

    @NonNull private final String TAG = "SyncLocalDatabase";

    @NonNull private LongSparseArray<StripDto> stripToProcess;

    @NonNull
    private StripRepository mStripRepository;

    @Inject
    public SyncLocalDatabaseTask(StripRepository stripRepository) {
        mStripRepository = stripRepository;
        stripToProcess = new LongSparseArray<>();
    }

    /**
     * Start to save in local database the strip passed in parameter
     */
    public Flowable<StripDto> execute (Flowable<StripDto> flowable) {

        return flowable
                .subscribeOn(Schedulers.newThread())
                .map(strip -> {
                    stripToProcess.put(strip.getId(), strip);

                    return strip;
                })
                .filter(strip -> {
                    boolean shouldContinue = !mStripRepository.existStripInCache(strip.getId());

                    if (!shouldContinue) {
                        stripToProcess.remove(strip.getId());
                    }

                    return shouldContinue;
                })
                .buffer(20)
                .flatMap(strips -> {
                    for (StripDto strip : strips)
                        stripToProcess.remove(strip.getId());

                    return mStripRepository.saveStrips(strips);
                })
                .map(new ListStripDaoToListStripDto())
                .flatMap(new Function<Iterable<StripDto>, Flowable<StripDto>>() {
                    @Override
                    public Flowable<StripDto> apply(Iterable<StripDto> strips) throws Exception {
                        return Flowable.fromIterable(strips);
                    }
                });
    }

    @NonNull
    public LongSparseArray<StripDto> getStripToProcess() {
        return stripToProcess;
    }
}