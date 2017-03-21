package com.commitstrip.commitstripreader.util.di.module;

import com.commitstrip.commitstripreader.util.ImageUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to transform drawable to array of bitmap
 */
@Module
public class ImageUtilsModule {

    @Provides
    public ImageUtils providesImageUtils() {
        return new ImageUtils();
    }
}
