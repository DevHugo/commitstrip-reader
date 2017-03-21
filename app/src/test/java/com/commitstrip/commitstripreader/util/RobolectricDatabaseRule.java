package com.commitstrip.commitstripreader.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.data.module.LocalDatabaseModule;
import com.commitstrip.commitstripreader.data.source.StripRepository;

import org.junit.rules.ExternalResource;
import org.robolectric.RuntimeEnvironment;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * A small JUnit Rule, to inject RepositoryComponent in test class.
 */
public class RobolectricDatabaseRule extends ExternalResource {

    @NonNull private ReactiveEntityStore<Persistable> mLocalDatabase;

    @Override
    protected void before() throws Throwable {
        super.before();

        MyApp myApp = (MyApp) RuntimeEnvironment.application;

        mLocalDatabase = myApp
                .getLocalDatabaseComponent()
                .provideLocalDatabase();
    }

    public @NonNull ReactiveEntityStore<Persistable> getLocalDatabase() {
        return mLocalDatabase;
    }

}
