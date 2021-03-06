package com.commitstrip.commitstripreader.strip;

import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripAbstractPresenter;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripContract;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripFragment;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripPresenterModule;
import com.commitstrip.commitstripreader.common.displaystrip.FetchStripType;
import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;

import javax.inject.Inject;

/**
 * Listens to user actions from the UI ({@link DisplayStripFragment}), retrieves the data and updates the
 * UI as required.
 * <p />
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the StripPresenter (if it fails, it emits a compiler error).  It uses
 * {@link DisplayStripPresenterModule} to do so.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 **/
public class StripPresenter extends DisplayStripAbstractPresenter {

    private String TAG = "StripPresenter";

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     *
     * @param fetchStripType
     * @param stripId
     * @param stripRepository
     * @param stripView
     */
    @Inject
    StripPresenter(FetchStripType fetchStripType,
            Long stripId, StripRepository stripRepository, DisplayStripContract.View stripView) {
        super(fetchStripType, stripId, stripRepository, stripView);
    }

    @Override
    public void onSwipeLeft() {
        if (mCurrentStrip != null) {
            if (Configuration.isIdCorrect(mCurrentStrip.getNext())) {
                passToStrip(mCurrentStrip.getNext());
            }
        }
    }

    @Override
    public void onSwipeRight() {
        if (mCurrentStrip != null) {
            if (Configuration.isIdCorrect(mCurrentStrip.getPrevious())) {
                passToStrip(mCurrentStrip.getPrevious());
            }
        }
    }

    @Override
    public void onStripDisplayed(StripDto strip) {}

    @Override
    public boolean fetchPriorityForUseVolumeKey() {
        return mStripRepository.fetchPriorityForUseVolumeKey();
    }

    @Override
    public boolean shouldUpdateNextStripOnFullScreen() {
        return false;
    }
}
