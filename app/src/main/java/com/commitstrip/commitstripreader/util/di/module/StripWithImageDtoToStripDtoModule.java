package com.commitstrip.commitstripreader.util.di.module;

import com.commitstrip.commitstripreader.util.ImageUtils;
import com.commitstrip.commitstripreader.util.converter.StripWithImageDtoToStripDto;

import dagger.Module;
import dagger.Provides;

@Module
public class StripWithImageDtoToStripDtoModule {

    @Provides
    public StripWithImageDtoToStripDto providesImageUtils() {
        return new StripWithImageDtoToStripDto();
    }
}
