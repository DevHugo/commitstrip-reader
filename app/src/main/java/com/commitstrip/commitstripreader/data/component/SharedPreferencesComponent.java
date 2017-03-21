package com.commitstrip.commitstripreader.data.component;

import android.content.SharedPreferences;

import com.commitstrip.commitstripreader.data.module.SharedPreferencesModule;

import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link com.commitstrip.commitstripreader.data} for the list of Dagger components
 * used in this application.
 * <P>
 * Because this component depends on the {@link com.commitstrip.commitstripreader.data.source.DataSourceComponent}, which is a singleton, a
 * scope must be specified.
 */
@Component(modules = {SharedPreferencesModule.class})
public interface SharedPreferencesComponent {

    SharedPreferences getSharedPreferences();
}
