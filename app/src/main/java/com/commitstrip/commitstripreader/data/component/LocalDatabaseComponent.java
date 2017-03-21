package com.commitstrip.commitstripreader.data.component;

import com.commitstrip.commitstripreader.data.module.LocalDatabaseModule;

import dagger.Component;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * This is a Dagger component. Refer to {@link com.commitstrip.commitstripreader.data} for the list of Dagger components
 * used in this application.
 * <P>
 * Because this component depends on the {@link com.commitstrip.commitstripreader.data.source.DataSourceComponent}, which is a singleton, a
 * scope must be specified.
 */
@Component(modules = {LocalDatabaseModule.class})
public interface LocalDatabaseComponent {

    ReactiveEntityStore<Persistable> provideLocalDatabase();
}
