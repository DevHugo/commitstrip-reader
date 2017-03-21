package com.commitstrip.commitstripreader.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.commitstrip.commitstripreader.data.source.local.StripImageCacheDataSource;
import com.squareup.picasso.Picasso;

import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RuntimeEnvironment;

import java.io.File;

/**
 * A small JUnit Rule, to inject {@link StripImageCacheDataSource} in test class.
 */
public class RobolectricStripImageDataSourceRule extends ExternalResource {

    @NonNull private StripImageCacheDataSource mStripImageCacheDataSource;

    @NonNull private File mExternalDirectory;
    @NonNull private File mInternalDirectory;
    @NonNull private File mCache;

    @Mock private Picasso mPicasso;

    @Rule public MockitoRule rule = MockitoJUnit.rule();

    @Override
    protected void before() throws Throwable {
        super.before();

        Context context = RuntimeEnvironment.application;
        mExternalDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mInternalDirectory = context.getFilesDir();
        mCache = context.getCacheDir();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ImageUtils imageUtils = new ImageUtils();

        mStripImageCacheDataSource = new StripImageCacheDataSource(
                mPicasso, mInternalDirectory, mExternalDirectory, mCache, imageUtils
        );
    }

    @NonNull public StripImageCacheDataSource getStripImageCacheDataSource() {
        return mStripImageCacheDataSource;
    }

    @NonNull public File getExternalDirectory() { return mExternalDirectory; }
    @NonNull public File getInternalDirectory() {return mInternalDirectory; }
    @NonNull public File getCache() { return mCache; }
    @NonNull public Picasso getPicasso() { return mPicasso; }


}
