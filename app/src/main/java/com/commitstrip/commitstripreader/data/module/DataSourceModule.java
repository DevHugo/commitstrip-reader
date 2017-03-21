package com.commitstrip.commitstripreader.data.module;

import android.content.SharedPreferences;

import com.commitstrip.commitstripreader.data.source.StripDataSource;
import com.commitstrip.commitstripreader.data.source.local.StripImageCacheDataSource;
import com.commitstrip.commitstripreader.data.source.local.StripLocalDataSource;
import com.commitstrip.commitstripreader.data.source.local.StripSharedPreferencesDataSource;
import com.commitstrip.commitstripreader.data.source.remote.StripRemoteDataSource;
import com.commitstrip.commitstripreader.util.ImageUtils;
import com.commitstrip.commitstripreader.util.di.CacheStorage;
import com.commitstrip.commitstripreader.util.di.ExternalStorage;
import com.commitstrip.commitstripreader.util.di.InternalStorage;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import retrofit2.Retrofit;

@Module
public class DataSourceModule {

    @Provides
    public StripDataSource.LocalDataSource provideStripLocalDataSource(
            ReactiveEntityStore<Persistable> localDatabase) {
        return new StripLocalDataSource(localDatabase);
    }

    @Provides
    public StripDataSource.RemoteDataSource provideStripRemoteDataSource(Retrofit retrofit,
            Picasso picasso) {
        return new StripRemoteDataSource(retrofit, picasso);
    }

    @Provides
    public StripDataSource.StripImageCacheDataSource provideStripImageCacheDataSource(
            Picasso picasso,
            @InternalStorage File internalStorage,
            @ExternalStorage File externalStorage,
            @CacheStorage File cacheDir,
            ImageUtils imageUtils) {

        return new StripImageCacheDataSource(picasso, internalStorage, externalStorage, cacheDir,
                imageUtils);
    }

    @Provides
    public StripDataSource.StripSharedPreferencesDataSource provideStripSharedPreferencesDataSource(
            SharedPreferences sharedPreferences) {

        return new StripSharedPreferencesDataSource(sharedPreferences);
    }

}
