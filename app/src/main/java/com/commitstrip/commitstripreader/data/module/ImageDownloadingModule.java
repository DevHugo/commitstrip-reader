package com.commitstrip.commitstripreader.data.module;

import android.content.Context;

import com.commitstrip.commitstripreader.BuildConfig;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

/**
 * This is a Dagger module. We use this to pass the image downloading module to the repository.
 */
@Module
public class ImageDownloadingModule {

    private Context mContext;

    public ImageDownloadingModule(Context context) {
        this.mContext = context;
    }

    @Provides
    public Picasso providesPicasso() {
        Picasso picasso = Picasso.with(mContext);

        if (BuildConfig.DEBUG) {
            picasso.setLoggingEnabled(true);
        }

        return picasso;
    }
}
