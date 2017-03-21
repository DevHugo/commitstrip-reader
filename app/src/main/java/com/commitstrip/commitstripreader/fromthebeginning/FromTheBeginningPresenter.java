package com.commitstrip.commitstripreader.fromthebeginning;

import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripAbstractPresenter;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripContract.View;
import com.commitstrip.commitstripreader.common.displaystrip.FetchStripType;
import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;

import javax.inject.Inject;

public class FromTheBeginningPresenter extends DisplayStripAbstractPresenter {

    /**
     * @param fetchStripType
     * @param stripId strip id
     * @param stripRepository use injection dependency framework to get strip repository instance
     * @param stripView a non null instance of a view that will react to the presenter event
     */
    @Inject
    public FromTheBeginningPresenter(
            FetchStripType fetchStripType,
            Long stripId,
            StripRepository stripRepository, View stripView) {
        super(fetchStripType, stripId, stripRepository, stripView);
    }

    @Override
    public void onSwipeLeft() {
        if (mCurrentStrip != null && FetchStripType.OLDER != mFetchStripType) {
            if (Configuration.isIdCorrect(mCurrentStrip.getPrevious())) {

                passToStrip(mCurrentStrip.getPrevious());
            }
        }
    }

    @Override
    public void onSwipeRight() {
        if (mCurrentStrip != null) {
            if (Configuration.isIdCorrect(mCurrentStrip.getNext())) {

                Long id = mCurrentStrip.getNext();

                passToStrip(id);
            }
        }
    }

    @Override
    public void onStripDisplayed(StripDto strip) {
        Long lastReadDate = mStripRepository.fetchLastReadDateFromTheBeginningMode();

        if (strip.getReleaseDate().getTime() > lastReadDate) {

            mStripRepository.saveLastReadIdFromTheBeginningMode(strip.getId());
            mStripRepository.saveLastReadDateFromTheBeginningMode(strip.getReleaseDate().getTime());
        }
    }

    @Override
    public boolean fetchPriorityForUseVolumeKey() {
        return mStripRepository.fetchPriorityForUseVolumeKey();
    }

    @Override
    public boolean shouldUpdateNextStripOnFullScreen() {
        return true;
    }

    public Long fetchLastReadIdFromTheBeginningMode() {
        return mStripRepository.fetchLastReadIdFromTheBeginningMode();
    }
}
