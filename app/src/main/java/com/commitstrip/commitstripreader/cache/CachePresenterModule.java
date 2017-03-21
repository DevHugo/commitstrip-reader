package com.commitstrip.commitstripreader.cache;

import com.commitstrip.commitstripreader.fullscreen.FullScreenStripContrat;

import dagger.Module;
import dagger.Provides;

@Module
public class CachePresenterModule {

    private final CacheContract.View mView;

    public CachePresenterModule(CacheContract.View view) {
        mView = view;
    }

    @Provides
    CacheContract.View provideCacheView() {
        return mView;
    }

}
