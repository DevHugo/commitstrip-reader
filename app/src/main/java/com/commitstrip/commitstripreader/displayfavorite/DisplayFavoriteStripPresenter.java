package com.commitstrip.commitstripreader.displayfavorite;


import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripAbstractPresenter;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripContract;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripPresenterModule;
import com.commitstrip.commitstripreader.common.displaystrip.FetchStripType;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.util.di.ActivityScoped;
import com.commitstrip.commitstripreader.util.di.FragmentScoped;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Listens to user actions from the UI ({@link DisplayFavoriteStripComponent}), retrieves the data and updates the
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
public class DisplayFavoriteStripPresenter extends DisplayStripAbstractPresenter {

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     *
     * @param stripId strip id
     * @param stripRepository strip repository
     * @param stripView
     */
    @Inject
    DisplayFavoriteStripPresenter(
            FetchStripType fetchStripType,
            Long stripId,
            StripRepository stripRepository,
            DisplayStripContract.View stripView) {
        super(fetchStripType, stripId, stripRepository, stripView);
    }

    @Override
    public void onSwipeLeft() {

        if (mCurrentStrip != null) {

            mSubscriptions.add(
                    mStripRepository
                            .fetchNextFavoriteStrip(mCurrentStrip.getReleaseDate())
                            .subscribeWith(new DisposableSingleObserver<StripDto>() {
                                @Override
                                public void onSuccess(StripDto nextStrip) {
                                    if (nextStrip != null) {
                                        passToStrip (nextStrip.getNext());
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {}
                            }));
        }
    }

    @Override
    public void onSwipeRight() {
        mSubscriptions.add(
                mStripRepository
                        .fetchPreviousFavoriteStrip(mCurrentStrip.getReleaseDate())
                        .subscribeWith(new DisposableSingleObserver<StripDto>() {
                            @Override
                            public void onSuccess(StripDto previousStrip) {
                                if (previousStrip != null) {
                                    passToStrip (previousStrip.getPrevious());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {}
                        }));
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
