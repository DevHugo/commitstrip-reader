package com.commitstrip.commitstripreader.strip;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link StripPresenter}.
 */
@Module
public class StripPresenterModule {

    private final StripContract.View mView;

    public StripPresenterModule(StripContract.View view) {
        mView = view;
    }

    @Provides
    StripContract.View provideListStripContractView() {
        return mView;
    }

}