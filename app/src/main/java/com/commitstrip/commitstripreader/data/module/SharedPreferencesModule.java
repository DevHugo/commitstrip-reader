package com.commitstrip.commitstripreader.data.module;

import android.content.Context;
import android.preference.PreferenceManager;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass the SharedPreferences instance to the repository.
 *
 * @see com.commitstrip.commitstripreader.data.component.SharedPreferencesComponent
 */
@Module
public class SharedPreferencesModule {

    private Context mContext;

    public SharedPreferencesModule (Context context) {
        this.mContext = context;
    }

    @Provides
    public android.content.SharedPreferences providesSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }
}
