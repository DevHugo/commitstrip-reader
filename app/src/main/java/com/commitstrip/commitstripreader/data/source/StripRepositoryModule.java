package com.commitstrip.commitstripreader.data.source;

import android.content.SharedPreferences;

import com.commitstrip.commitstripreader.data.source.local.StripImageCacheDataSource;
import com.commitstrip.commitstripreader.data.source.local.StripLocalDataSource;
import com.commitstrip.commitstripreader.data.source.remote.StripRemoteDataSource;
import com.commitstrip.commitstripreader.util.ExternalStorage;
import com.commitstrip.commitstripreader.util.InternalStorage;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import retrofit2.Retrofit;

@Module
public class StripRepositoryModule {

    @Singleton
    @Provides
    StripDataSource.LocalDataSource provideStripLocalDataSource(ReactiveEntityStore<Persistable> localDatabase, SharedPreferences sharedPreferences) {
        return new StripLocalDataSource(localDatabase, sharedPreferences);
    }

    @Singleton
    @Provides
    StripDataSource.RemoteDataSource provideStripRemoteDataSource(Retrofit retrofit, Picasso picasso) {
        return new StripRemoteDataSource(retrofit, picasso);
    }

    @Singleton
    @Provides
    StripDataSource.StripImageCacheDataSource provideStripImageCacheDataSource(Picasso picasso, @InternalStorage File internalStorage, @ExternalStorage File externalStorage) {
        return new StripImageCacheDataSource(picasso, internalStorage, externalStorage);
    }

}
