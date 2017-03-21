package com.commitstrip.commitstripreader.util.di.component;

import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripFragment;
import com.commitstrip.commitstripreader.common.liststrip.ListStripFragment;
import com.commitstrip.commitstripreader.settings.CompressPreference;
import com.commitstrip.commitstripreader.util.di.module.ImageUtilsModule;

import dagger.Component;

/**
 * This is a Dagger component
 */
@Component(modules = {ImageUtilsModule.class})
public interface ImageUtilsComponent {

    void inject(DisplayStripFragment fragment);
}
