package com.commitstrip.commitstripreader.data.source.local;

import android.content.SharedPreferences;

import com.commitstrip.commitstripreader.data.source.StripDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StripSharedPreferencesDataSource implements StripDataSource.StripSharedPreferencesDataSource {

    private final static String KEY_COMPRESSION_IMAGE
            = "COMPRESSION_IMAGE";

    public final static String KEY_CURRENT_ID_READ_FROM_BEGINNING
            = "CURRENT_ID_READ_FROM_BEGINNING";
    public final static String KEY_CURRENT_DATE_READ_FROM_BEGINNING
            = "CURRENT_DATE_READ_FROM_BEGINNING";

    public final static String KEY_SHOULD_USE_VOLUME_KEY = "USE_VOLUME_KEY";

    public final static String KEY_RECEIVE_NOTIFICATION = "RECEIVE_NOTIFICATION";


    public final SharedPreferences mSharedPreferences;

    @Inject
    public StripSharedPreferencesDataSource(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    /* (no-Javadoc) */
    @Override
    public int fetchCompressionLevelImages() {
        return mSharedPreferences.getInt(KEY_COMPRESSION_IMAGE, 100);
    }

    /* (no-Javadoc) */
    @Override
    public void saveCompressionLevelImages(int compression) {
        mSharedPreferences.edit().putInt(KEY_COMPRESSION_IMAGE, compression).apply();
    }

    /* (no-Javadoc) */
    @Override
    public long fetchLastReadIdFromTheBeginningMode() {
        return mSharedPreferences.getLong(KEY_CURRENT_ID_READ_FROM_BEGINNING, -1);
    }

    /* (no-Javadoc) */
    @Override
    public void saveLastReadIdFromTheBeginningMode(Long lastId) {
        mSharedPreferences.edit().putLong(KEY_CURRENT_ID_READ_FROM_BEGINNING, lastId).apply();
    }

    /* (no-Javadoc) */
    @Override
    public long fetchLastReadDateFromTheBeginningMode() {
        return mSharedPreferences.getLong(KEY_CURRENT_DATE_READ_FROM_BEGINNING, -1);
    }

    /* (no-Javadoc) */
    @Override
    public void saveLastReadDateFromTheBeginningMode(Long date) {
        mSharedPreferences.edit().putLong(KEY_CURRENT_DATE_READ_FROM_BEGINNING, date).apply();
    }

    /* (no-Javadoc) */
    @Override
    public boolean fetchPriorityForUseVolumeKey() {
        return mSharedPreferences.getBoolean(KEY_SHOULD_USE_VOLUME_KEY, false);
    }

    /* (no-Javadoc) */
    @Override
    public void savePriorityForUseVolumeKey(boolean useVolumeKey) {
        mSharedPreferences.edit()
                .putBoolean(KEY_CURRENT_DATE_READ_FROM_BEGINNING, useVolumeKey)
                .apply();
    }
}
