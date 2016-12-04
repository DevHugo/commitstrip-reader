package com.commitstrip.commitstripreader.data.component;

import com.commitstrip.commitstripreader.data.module.LocalStorageModule;
import com.commitstrip.commitstripreader.util.ExternalStorage;
import com.commitstrip.commitstripreader.util.InternalStorage;

import java.io.File;

import javax.inject.Singleton;

import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link com.commitstrip.commitstripreader.data} for the list of Dagger components
 * used in this application.
 * <P>
 * Because this component depends on the {@link com.commitstrip.commitstripreader.data.source.StripRepositoryComponent}, which is a singleton, a
 * scope must be specified.
 */
@Component (modules = {LocalStorageModule.class})
public interface LocalStorageComponent {

    /**
     * Provide external storage.
     *
     * @return File instance pointing to the main directory picture. Be warned, you have to check if you have an
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

}
