package com.commitstrip.commitstripreader.settings;

import com.commitstrip.commitstripreader.util.di.FragmentScoped;
import com.commitstrip.commitstripreader.util.di.module.ImageUtilsModule;

import dagger.Component;

@FragmentScoped
@Component(modules = ImageUtilsModule.class)
public interface CompressPreferenceComponent {

    void inject(CompressPreference compressPreference);
}
