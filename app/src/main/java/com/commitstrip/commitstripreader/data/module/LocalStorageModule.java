package com.commitstrip.commitstripreader.data.module;

import android.content.Context;
import android.os.Environment;

import com.commitstrip.commitstripreader.util.ExternalStorage;
import com.commitstrip.commitstripreader.util.InternalStorage;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass the local storage instance to the repository.
 *
 * @see com.commitstrip.commitstripreader.data.component.LocalStorageComponent
 */
@Module
public class LocalStorageModule {

    private String TAG = "LocalStorageModule";

    private Context mContext;

    public LocalStorageModule(Context context) {
        this.mContext = context;
    }

    @Provides
    @ExternalStorage
    File provideExternalStorage() {
        return mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    @Provides
    @InternalStorage
    File provideInternalStorage() {
        return mContext.getFilesDir();
    }
}
