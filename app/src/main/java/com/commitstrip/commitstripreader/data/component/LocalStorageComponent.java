package com.commitstrip.commitstripreader.data.component;

import com.commitstrip.commitstripreader.data.module.LocalStorageModule;
import com.commitstrip.commitstripreader.util.di.CacheStorage;
import com.commitstrip.commitstripreader.util.di.ExternalStorage;
import com.commitstrip.commitstripreader.util.di.InternalStorage;

import java.io.File;

import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link com.commitstrip.commitstripreader.data} for the list
 * of Dagger components used in this application.
 * <P>
 * Because this component depends on the
 * {@link com.commitstrip.commitstripreader.data.source.DataSourceComponent},
 * which is a singleton, a scope must be specified.
 */
@Component(modules = {LocalStorageModule.class})
public interface LocalStorageComponent {

    /**
     * Provide external storage.
     *
     * @return File instance pointing to the main directory picture.
     */
    @ExternalStorage
    File provideExternalStorage();

    /**
     * Provide a file pointing to internal storage.
     *
     * @return File instance pointing to the main folder internal storage.
     */
    @InternalStorage
    File provideInternalStorage();

    /**
     * Provide a file pointing to cache storage.
     *
     * @return File instance pointing to the cache internal storage.
     */
    @CacheStorage
    File provideCacheDir();

}
