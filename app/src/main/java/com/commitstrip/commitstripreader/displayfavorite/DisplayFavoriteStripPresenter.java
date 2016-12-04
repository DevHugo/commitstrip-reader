package com.commitstrip.commitstripreader.displayfavorite;

import android.util.Log;

import com.commitstrip.commitstripreader.common.AbstractDisplayStripPresenter;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.strip.StripPresenterModule;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Listens to user actions from the UI ({@link DisplayFavoriteStripFragment}), retrieves the data and updates the
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
public class DisplayFavoriteStripPresenter extends AbstractDisplayStripPresenter {

    private String TAG = "DisplayFavoriteStripPresenter";

    private final StripRepository mStripRepository;
    private final CompositeDisposable mSubscriptions;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     *
     * @param stripRepository
     * @param stripView
     */
    @Inject
    DisplayFavoriteStripPresenter(StripRepository stripRepository, DisplayFavoriteStripContract.View stripView) {
        super(stripRepository, stripView);

        mStripRepository = stripRepository;
        mSubscriptions = new CompositeDisposable();

    }

    @Override
    public Long askForNextIdStrip() {
        Long nextId = null;

        if (mCurrentStrip != null) {

            StripDto nextStrip = null;
            try {
               nextStrip = mStripRepository.fetchNextFavoriteStrip(mCurrentStrip.getDate()).blockingGet();
            }
            catch (NoSuchElementException exception) {}

            if (nextStrip != null) {
                nextId = nextStrip.getId();
            }
        }

        return nextId;
    }

    @Override
    public Long askForPreviousIdStrip() {
        Long previousId = null;

        if (mCurrentStrip != null) {

            StripDto previousStrip = null;
            try {
                previousStrip = mStripRepository.fetchPreviousFavoriteStrip(mCurrentStrip.getDate()).blockingGet();
            }
            catch (NoSuchElementException exception) {}

            if (previousStrip != null) {
                previousId = previousStrip.getId();
            }
        }

        return previousId;
    }

}
