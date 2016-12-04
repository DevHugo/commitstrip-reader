package com.commitstrip.commitstripreader.data.source;

import android.content.Context;

import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.component.DaggerLocalDatabaseComponent;
import com.commitstrip.commitstripreader.data.component.DaggerLocalStorageComponent;
import com.commitstrip.commitstripreader.data.component.DaggerNetComponent;
import com.commitstrip.commitstripreader.data.component.DaggerSharedPreferencesComponent;
import com.commitstrip.commitstripreader.data.component.LocalDatabaseComponent;
import com.commitstrip.commitstripreader.data.component.LocalStorageComponent;
import com.commitstrip.commitstripreader.data.component.NetComponent;
import com.commitstrip.commitstripreader.data.component.SharedPreferencesComponent;
import com.commitstrip.commitstripreader.data.module.ImageDownloadingModule;
import com.commitstrip.commitstripreader.data.module.LocalDatabaseModule;
import com.commitstrip.commitstripreader.data.module.LocalStorageModule;
import com.commitstrip.commitstripreader.data.module.NetModule;
import com.commitstrip.commitstripreader.data.module.SharedPreferencesModule;

/**
 * A simple singleton for injecting the repository component and other component.
 */
public class StripRepositorySingleton {

    /** Instance unique non préinitialisée */
    private static StripRepositorySingleton INSTANCE = null;

    private LocalStorageComponent localStorageComponent;
    private SharedPreferencesComponent sharedPreferencesComponent;
    private StripRepositoryComponent stripRepositoryComponent;
    private NetComponent netModuleComponent;
    private LocalDatabaseComponent localDatabaseComponent;

    /**
     * Constructeur privé
     *
     * @param context android context
     */
    private StripRepositorySingleton(Context context) {

        sharedPreferencesComponent =
                DaggerSharedPreferencesComponent.builder()
                        .sharedPreferencesModule(new SharedPreferencesModule(context))
                        .build();

        localStorageComponent =
                DaggerLocalStorageComponent.builder()
                        .localStorageModule(new LocalStorageModule(context))
                        .build();

        netModuleComponent =
                DaggerNetComponent.builder()
                        .netModule(new NetModule(Configuration.URL_BACKEND, context))
                        .build();

        localDatabaseComponent =
                DaggerLocalDatabaseComponent.builder()
                        .localDatabaseModule(new LocalDatabaseModule(context))
                        .build();

        stripRepositoryComponent = DaggerStripRepositoryComponent.builder()
                .netComponent(netModuleComponent)
                .localDatabaseComponent(localDatabaseComponent)
                .localStorageComponent(localStorageComponent)
                .stripRepositoryModule(new StripRepositoryModule())
                .imageDownloadingModule(new ImageDownloadingModule(context))
                .sharedPreferencesComponent(sharedPreferencesComponent)
                .build();
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static StripRepositorySingleton getInstance(Context context)
    {
        if (INSTANCE == null)
        {
            synchronized(StripRepositorySingleton.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = new StripRepositorySingleton(context);
                }
            }
        }
        return INSTANCE;
    }

    public StripRepositoryComponent getStripRepositoryComponent() {
        return stripRepositoryComponent;
    }

    public SharedPreferencesComponent getSharedPreferencesComponent() {
        return sharedPreferencesComponent;
    }

    public LocalStorageComponent getLocalStorageComponent() {
        return localStorageComponent;
    }

    public NetComponent getNetModuleComponent() {
        return netModuleComponent;
    }

    public LocalDatabaseComponent getLocalDatabaseComponent() {
        return localDatabaseComponent;
    }
}
