package com.commitstrip.commitstripreader.data.module;

import android.content.Context;
import android.os.Environment;

import com.commitstrip.commitstripreader.util.di.CacheStorage;
import com.commitstrip.commitstripreader.util.di.ExternalStorage;
import com.commitstrip.commitstripreader.util.di.InternalStorage;

import java.io.File;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass the local storage instance to the repository.
 *
 * @see com.commitstrip.commitstripreader.data.component.LocalStorageComponent
 */
@Module
public class LocalStorageModule {

    private Context mContext;

    public LocalStorageModule(Context context) {
        this.mContext = context;
    }

    @Provides
    @ExternalStorage
    protected File provideExternalStorage() {
        return mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    @Provides
    @InternalStorage
    protected File provideInternalStorage() {
        return mContext.getFilesDir();
    }

    @Provides
    @CacheStorage
    protected File provideCacheDir() {
        return mContext.getCacheDir();
    }
}
