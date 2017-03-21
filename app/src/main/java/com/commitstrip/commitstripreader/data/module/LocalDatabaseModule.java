package com.commitstrip.commitstripreader.data.module;

import android.content.Context;

import com.commitstrip.commitstripreader.BuildConfig;
import com.commitstrip.commitstripreader.data.source.local.Models;

import dagger.Module;
import dagger.Provides;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;

/**
 * This is a Dagger module. We use this to pass the local database instance to the repository.
 */
@Module
public class LocalDatabaseModule {

    private final Context mContext;

    public LocalDatabaseModule(Context context) {
        this.mContext = context;
    }

    @Provides
    public ReactiveEntityStore<Persistable> provideLocalDatabase() {

        DatabaseSource source = new DatabaseSource(mContext, Models.DEFAULT,
                com.commitstrip.commitstripreader.configuration.Configuration.DATABASE_VERSION);

        if (BuildConfig.DEBUG) {
            // use this in development mode to drop and recreate the tables on every upgrade
            source.setTableCreationMode(TableCreationMode.DROP_CREATE);
        }

        Configuration configuration = source.getConfiguration();
        return ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));
    }
}
