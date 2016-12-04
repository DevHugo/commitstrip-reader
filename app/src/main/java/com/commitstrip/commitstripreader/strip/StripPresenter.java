package com.commitstrip.commitstripreader.strip;

import com.commitstrip.commitstripreader.common.AbstractDisplayStripPresenter;
import com.commitstrip.commitstripreader.data.source.StripRepository;

import javax.inject.Inject;

/**
 * Listens to user actions from the UI ({@link StripFragment}), retrieves the data and updates the
 * UI as required.
 * <p />
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the StripPresenter (if it fails, it emits a compiler error).  It uses
 * {@link StripPresenterModule} to do so.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 **/
public class StripPresenter extends AbstractDisplayStripPresenter {

    private String TAG = "StripPresenter";

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     *
     * @param stripRepository
     * @param stripView
     */
    @Inject
    StripPresenter(StripRepository stripRepository, StripContract.View stripView) {
        super(stripRepository, stripView);
    }

    @Override
    public Long askForNextIdStrip() {
        return mCurrentStrip != null ? mCurrentStrip.getNext() : null;
    }

    @Override
    public Long askForPreviousIdStrip() {
        return mCurrentStrip != null ? mCurrentStrip.getPrevious() : null;
    }
}
