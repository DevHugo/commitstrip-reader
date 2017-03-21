package com.commitstrip.commitstripreader.fullscreen;

import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.fullscreen.FullScreenStripContrat.View;

import dagger.Module;
import dagger.Provides;

@Module
class FullScreenStripPresenterModule {

    private final FullScreenStripContrat.View mView;
    private final boolean mReadFrom0Mode;
    private final StripDto mCurrentStrip;

    public FullScreenStripPresenterModule(StripDto currentStripDto, boolean readFrom0Mode,
          View view) {

        mView = view;
        mCurrentStrip = currentStripDto;
        mReadFrom0Mode = readFrom0Mode;
    }

    @Provides
    FullScreenStripContrat.View provideListStripView() {
    return mView;
    }

    @Provides
    boolean provideReadFrom0Mode() {
        return mReadFrom0Mode;
    }

    @Provides
    StripDto provideCurrentStrip () {
        return mCurrentStrip;
    }

}
