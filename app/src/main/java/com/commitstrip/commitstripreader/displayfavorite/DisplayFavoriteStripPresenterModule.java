package com.commitstrip.commitstripreader.displayfavorite;

import com.commitstrip.commitstripreader.strip.StripContract;
import com.commitstrip.commitstripreader.strip.StripPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link DisplayFavoriteStripPresenter}.
 */
@Module
public class DisplayFavoriteStripPresenterModule {

    private final DisplayFavoriteStripContract.View mView;

    public DisplayFavoriteStripPresenterModule(DisplayFavoriteStripContract.View view) {
        mView = view;
    }

    @Provides
    DisplayFavoriteStripContract.View provideDisplayFavoriteStripView() {
        return mView;
    }

}