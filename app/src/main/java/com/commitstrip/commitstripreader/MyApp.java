package com.commitstrip.commitstripreader;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.component.DaggerDataSourceComponent;
import com.commitstrip.commitstripreader.data.component.DaggerLocalDatabaseComponent;
import com.commitstrip.commitstripreader.data.component.DaggerLocalStorageComponent;
import com.commitstrip.commitstripreader.data.component.DaggerNetComponent;
import com.commitstrip.commitstripreader.data.component.DaggerSharedPreferencesComponent;
import com.commitstrip.commitstripreader.data.component.DataSourceComponent;
import com.commitstrip.commitstripreader.data.component.LocalDatabaseComponent;
import com.commitstrip.commitstripreader.data.component.LocalStorageComponent;
import com.commitstrip.commitstripreader.data.component.NetComponent;
import com.commitstrip.commitstripreader.data.component.SharedPreferencesComponent;
import com.commitstrip.commitstripreader.data.module.DataSourceModule;
import com.commitstrip.commitstripreader.data.module.ImageDownloadingModule;
import com.commitstrip.commitstripreader.data.module.LocalDatabaseModule;
import com.commitstrip.commitstripreader.data.module.LocalStorageModule;
import com.commitstrip.commitstripreader.data.module.NetModule;
import com.commitstrip.commitstripreader.data.module.SharedPreferencesModule;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.android.AndroidInjectionModule;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasDispatchingActivityInjector;

@Singleton
public class MyApp extends Application {

    private String TAG = "MyApp";

    private LocalStorageComponent localStorageComponent;
    private SharedPreferencesComponent sharedPreferencesComponent;
    private DataSourceComponent dataSourceComponent;
    private NetComponent netModuleComponent;

    private LocalDatabaseComponent localDatabaseComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {

            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }

            LeakCanary.install(this);

            Stetho.initializeWithDefaults(this);

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    //.penaltyDeath()
                    .build());
        }

        inject();
    }

    private void inject () {
        Context context = getApplicationContext();

        sharedPreferencesComponent =
                DaggerSharedPreferencesComponent.builder()
                        .sharedPreferencesModule(new SharedPreferencesModule(context))
                        .build();

        localStorageComponent =
                DaggerLocalStorageComponent.builder()
                        .localStorageModule(new LocalStorageModule(context))
                        .build();

        netModuleComponent = DaggerNetComponent.builder()
                .netModule(new NetModule(Configuration.URL_BACKEND, context))
                .build();

        localDatabaseComponent =
                DaggerLocalDatabaseComponent.builder()
                        .localDatabaseModule(new LocalDatabaseModule(context))
                        .build();

        dataSourceComponent = DaggerDataSourceComponent.builder()
                .localDatabaseComponent(localDatabaseComponent)
                .localStorageComponent(localStorageComponent)
                .imageDownloadingModule(new ImageDownloadingModule(context))
                .sharedPreferencesComponent(sharedPreferencesComponent)
                .netComponent(netModuleComponent)
                .dataSourceModule(new DataSourceModule())
                .build();

        DaggerMyAppComponent.builder().dataSourceComponent(dataSourceComponent).build().inject(this);
    }

    public DataSourceComponent getDataSourceComponent() {
        return dataSourceComponent;
    }

    public LocalDatabaseComponent getLocalDatabaseComponent() {
        return localDatabaseComponent;
    }
}
