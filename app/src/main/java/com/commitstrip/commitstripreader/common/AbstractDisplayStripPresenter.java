package com.commitstrip.commitstripreader.common;

import android.support.annotation.NonNull;
import android.util.Log;

import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.squareup.picasso.RequestCreator;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Listens to user actions from the UI ({@link AbstractDisplayStripFragment}), retrieves the data and updates the
 * UI as required.
 */
public abstract class AbstractDisplayStripPresenter implements AbstractDisplayStripContract.Presenter {

    private String TAG = "AbstractStripPresenter";

    @NonNull
    private final StripRepository mStripRepository;

    @NonNull
    private AbstractDisplayStripContract.View mStripView;

    private final CompositeDisposable mSubscriptions;
    protected StripDto mCurrentStrip;

    public AbstractDisplayStripPresenter(
            @NonNull StripRepository stripRepository,
            @NonNull AbstractDisplayStripContract.View stripView) {
        mStripRepository = stripRepository;
        mStripView = stripView;

        mSubscriptions = new CompositeDisposable ();
        mStripView.setAbstractPresenter(this);
    }

    @Override
    public void subscribe() {}

    @Override
    public void fetchStrip(Long id) {
        Single<StripDto> strip = mStripRepository.fetchStrip(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        askViewDisplayStrip(strip);
    }

    @Override
    public abstract Long askForNextIdStrip();

    @Override
    public abstract Long askForPreviousIdStrip();

    @Override
    public void addFavorite() {
        if (mCurrentStrip != null) {
            Single<StripDto> strips = mStripRepository.addFavorite(mCurrentStrip);

            DisposableSingleObserver singleObserver = new DisposableSingleObserver<StripDto>() {

                @Override
                public void onSuccess(StripDto value) {
                    mStripView.displayIconIsFavorite(true);
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "Could not add favorite: ", e);
                }
            };

            strips.subscribeWith(singleObserver);

            mSubscriptions.add(singleObserver);
        }
    }

    @Override
    public void deleteFavorite() {
        if (mCurrentStrip != null) {
            if (mCurrentStrip != null) {
                Single<StripDto> strips = mStripRepository.deleteFavorite(mCurrentStrip);

                DisposableSingleObserver singleObserver = new DisposableSingleObserver<StripDto>() {

                    @Override
                    public void onSuccess(StripDto value) {
                        mStripView.displayIconIsFavorite(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Could not add favorite: ", e);
                    }
                };

                strips.subscribeWith(singleObserver);

                mSubscriptions.add(singleObserver);
            }
        }
    }

    private void askViewDisplayStrip (Single<StripDto> strip) {

        DisposableSingleObserver singleObserver = new DisposableSingleObserver<StripDto>() {

            @Override
            public void onSuccess(StripDto strip) {
                // Handle the case where there is no internet connection and no cache available.
                if (strip != null) {

                    RequestCreator requestCreator =
                            mStripRepository.fetchImageStrip(strip.getId(), strip.getContent());

                    mStripView.setTitle(strip.getTitle());
                    mStripView.displayImage(requestCreator);
                    mStripView.displayIconIsFavorite(mStripRepository.isFavorite(strip.getId()));
                    
                    mCurrentStrip = strip;
                }
            }

            @Override
            public void onError(Throwable e) {

                Log.e(TAG, "Error fetching strip: ", e);
            }
        };

        strip.subscribeWith(singleObserver);

        mSubscriptions.add(singleObserver);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
