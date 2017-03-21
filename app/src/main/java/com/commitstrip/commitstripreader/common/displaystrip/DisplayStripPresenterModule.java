package com.commitstrip.commitstripreader.common.displaystrip;

import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripContract;
import com.commitstrip.commitstripreader.strip.StripPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link StripPresenter} or {@link DisplayFavoriteStripPresenter}.
 */
@Module
public class DisplayStripPresenterModule {

    private final DisplayStripContract.View mView;
    private final Long mStripId;
    private final FetchStripType mFetchStripType;

    public DisplayStripPresenterModule(FetchStripType fetchStripType, Long id, DisplayStripContract.View view) {
        mView = view;
        mStripId = id;
        mFetchStripType = fetchStripType;
    }

    @Provides
    DisplayStripContract.View provideDisplayStripContractView() {
        return mView;
    }

    @Provides
    Long provideStripId() { return mStripId; }

    @Provides
    FetchStripType provideFetchStripType() { return mFetchStripType; }
}
