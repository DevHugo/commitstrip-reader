package com.commitstrip.commitstripreader.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.util.InternalStorage;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import dagger.Component;
import io.reactivex.Flowable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Simple class to help to resume the local synchronization with the remote database.
 */
public class ResumeSyncLocalDatabase {

    private final String TAG = "ResumeSyncLocalDatabase";

    @NonNull
    private final String FILENAME_CACHE = Configuration.FILENAME_CACHE_SYNC_LOCAL_DATABASE;

    @NonNull
    private final WeakReference<File> mWeakInternalStorage;

    @NonNull
    private WeakReference<StripRepository> mWeakStripRepository;

    public ResumeSyncLocalDatabase (@NonNull StripRepository stripRepository, @NonNull File internalStorage) {

        if (stripRepository == null || internalStorage == null) {
            throw new IllegalArgumentException();
        }

        mWeakStripRepository = new WeakReference<>(stripRepository);
        mWeakInternalStorage = new WeakReference<>(internalStorage);
    }

    /**
     * Return a flux of strips to resume.
     *
     * If we can't resume from a previous sync. Return all strips from remote.
     */
    public Flowable<StripDto> resumeFromLastSync () {

        StripRepository stripRepository = mWeakStripRepository.get();

        // Fetch from the backend all strips
        Flowable<List<StripDto>> fetchFromBackend = Flowable.just(new ArrayList<>());
        if (stripRepository != null) {
            fetchFromBackend = stripRepository.fetchAllStrip();
        }

        // Try to resume from the last synchronization
        Flowable<List<StripDto>> resumeFromLastSync = Flowable
                .just(FILENAME_CACHE)
                // If we have the file, we should resume from it.
                .filter(fileName -> {
                    File internalStorage = mWeakInternalStorage.get();

                    return internalStorage != null && new File(internalStorage, FILENAME_CACHE).exists();
                })
                // Get the file content
                .flatMap(filename -> {
                    String content = "";

                    File internalStorage = mWeakInternalStorage.get();

                    if (internalStorage != null) {
                        File file = new File (internalStorage, FILENAME_CACHE);

                        try {
                            content = Files.toString(file, Charset.defaultCharset());
                        } catch (IOException e) {
                            throw Exceptions.propagate(e);
                        }
                    }

                    return Flowable.just(content);
                })
                // If it is not empty file
                .filter(content -> !content.equals(""))
                // Parse it and get all strips from it
                .flatMap(content -> {
                    Type listType = new TypeToken<ArrayList<StripDto>>(){}.getType();

                    Gson gson = new Gson();
                    List<StripDto> list =  gson.fromJson(content, listType);

                    return Flowable.just(list);
                });

        return resumeFromLastSync
                .onErrorResumeNext(fetchFromBackend)
                .switchIfEmpty(fetchFromBackend)
                .flatMap(new Function<List<StripDto>, Flowable<StripDto>>() {
                    @Override
                    public Flowable<StripDto> apply(List<StripDto> strips) throws Exception {
                        return Flowable.fromIterable(strips);
                    }
                });
    }

    public void saveCurrentProgression (List<StripDto> toResume) {

        Gson gson = new Gson();
        String content = gson.toJson(toResume);

        File internalStorage = mWeakInternalStorage.get();

        if (internalStorage != null) {
            File file = new File(internalStorage, FILENAME_CACHE);

            if (file.exists()) {
                file.delete();
            }

            try {
                file.createNewFile();
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(content);
                myOutWriter.close();
                fOut.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not create tempory file for saving the progress", e);
            }
        }
        else {
            Log.e(TAG, "Could not create tempory file for saving the progress");
        }
    }
}
