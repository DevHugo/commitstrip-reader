package com.commitstrip.commitstripreader.data.component;

import android.content.SharedPreferences;

import com.commitstrip.commitstripreader.data.module.DataSourceModule;
import com.commitstrip.commitstripreader.data.module.ImageDownloadingModule;
import com.commitstrip.commitstripreader.data.source.StripDataSource;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.util.di.module.ImageUtilsModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link com.commitstrip.commitstripreader.data} for the list
 * of Dagger components
 * used in this application.
 * <P>
 * Because this component depends on the
 * {@link com.commitstrip.commitstripreader.data.source.DataSourceComponent},
 * which is a singleton, a
 * scope must be specified.
 */
@Component(modules = {
        DataSourceModule.class,
        ImageDownloadingModule.class,
        ImageUtilsModule.class
}, dependencies = {
        SharedPreferencesComponent.class,
        LocalStorageComponent.class,
        NetComponent.class,
        LocalDatabaseComponent.class
})
public interface DataSourceComponent {

    StripRepository getStripRepository();

    SharedPreferences sharedPreferences();
}
