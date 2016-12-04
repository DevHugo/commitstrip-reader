package com.commitstrip.commitstripreader.data.source;

import com.commitstrip.commitstripreader.data.component.LocalDatabaseComponent;
import com.commitstrip.commitstripreader.data.component.LocalStorageComponent;
import com.commitstrip.commitstripreader.data.component.NetComponent;
import com.commitstrip.commitstripreader.data.component.SharedPreferencesComponent;
import com.commitstrip.commitstripreader.data.module.ImageDownloadingModule;
import com.commitstrip.commitstripreader.data.module.LocalDatabaseModule;
import com.commitstrip.commitstripreader.data.module.NetModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * This is a Dagger component.
 */
@Singleton
@Component(modules = {
        StripRepositoryModule.class,
        ImageDownloadingModule.class},

        dependencies = {
                SharedPreferencesComponent.class,
                LocalStorageComponent.class,
                NetComponent.class,
                LocalDatabaseComponent.class
        })
public interface StripRepositoryComponent {

    StripRepository getStripRepository();

}
