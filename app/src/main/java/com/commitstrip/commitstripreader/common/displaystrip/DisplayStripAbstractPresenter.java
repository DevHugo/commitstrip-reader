package com.commitstrip.commitstripreader.common.displaystrip;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.squareup.picasso.RequestCreator;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Listens to user actions from the UI ({@link DisplayStripFragment}), retrieves the data
 * and updates the UI as required.
 */
public abstract class DisplayStripAbstractPresenter implements DisplayStripContract.Presenter {

    private String TAG = "AbstractStripPresenter";

    @NonNull protected final StripRepository mStripRepository;
    @NonNull protected final DisplayStripContract.View mStripView;

    @NonNull protected CompositeDisposable mSubscriptions;
    protected StripDto mCurrentStrip;

    protected Long mStripId;
    protected FetchStripType mFetchStripType;

    /**
     * @param stripRepository use injection dependency framework to get strip repository instance
     * @param stripView a non null instance of a view that will react to the presenter event
     */
    public DisplayStripAbstractPresenter(
            @NonNull FetchStripType fetchStripType,
            @Nullable Long stripId,
            @NonNull StripRepository stripRepository,
            @NonNull DisplayStripContract.View stripView) {

        mStripRepository = stripRepository;
        mStripView = stripView;

        mStripId = stripId;
        mFetchStripType = fetchStripType;

        mSubscriptions = new CompositeDisposable();
    }

    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        mStripView.setAbstractPresenter(this);
    }

    @Override
    public void subscribe() {
        fetchStrip();
    }

    /* (no-Javadoc) */
    @Override
    public abstract void onSwipeLeft();

    /* (no-Javadoc) */
    @Override
    public abstract void onSwipeRight();

    /* (no-Javadoc) */
    @Override
    public abstract void onStripDisplayed(StripDto strip);

    /* (no-Javadoc) */
    @Override
    public void addFavorite(@NonNull ByteArrayOutputStream byteArrayOutputStream) {

        // Strip repository doesn't accept null value.
        if (mCurrentStrip != null && byteArrayOutputStream != null) {

            // Add strip in favorite
            mSubscriptions.add(mStripRepository
                    .addFavorite(mCurrentStrip, byteArrayOutputStream)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<StripDto>() {

                        @Override
                        public void onSuccess(StripDto value) {
                            mStripView.displayIconIsFavorite(true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mStripView.displayErrorAddFavorite();

                            Log.e(TAG, "Could not add favorite: ", e);
                        }
                    }));

        } else {
            mStripView.displayErrorAddFavorite();
        }
    }

    /* (no-Javadoc) */
    @Override
    public void deleteFavorite() {

        if (mCurrentStrip != null && mCurrentStrip.getId() != null) {

            mSubscriptions.add(
                    mStripRepository
                            .deleteFavorite(mCurrentStrip.getId())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableMaybeObserver<Integer>() {

                                @Override
                                public void onSuccess(Integer numberRowAffected) {
                                    if (numberRowAffected > 0) {
                                        mStripView.displayIconIsFavorite(false);
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    mStripView.displayErrorAddFavorite();

                                    Log.e(TAG, "Could not delete favorite: ", e);
                                }

                                @Override
                                public void onComplete() {}
                            }));

        } else {
            mStripView.displayErrorAddFavorite();
        }
    }

    /* (no-Javadoc) */
    @Override
    public void unsubscribe() {

        mSubscriptions.clear();
    }

    /* (no-Javadoc) */
    protected void fetchStrip() {

        Maybe<StripDto> maybeStrip;
        if (mFetchStripType == FetchStripType.ID && mStripId != -1) {

            maybeStrip = mStripRepository.fetchStrip(mStripId, false);
        } else if (mFetchStripType == FetchStripType.OLDER) {

            maybeStrip = mStripRepository.fetchOlderStrip();
        } else {

            maybeStrip = mStripRepository.fetchStrip(null, false);
        }

        Maybe<StripDto> strip = maybeStrip
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        askViewDisplayStrip(strip);
    }

    private void askViewDisplayStrip(@NonNull Maybe<StripDto> strip) {

        mSubscriptions.add(
                strip.subscribeWith(new DisposableMaybeObserver<StripDto>() {

                    @Override
                    public void onSuccess(StripDto strip) {

                        if (strip != null) {

                            RequestCreator image =
                                    mStripRepository
                                            .fetchImageStrip(strip.getId(), strip.getContent());

                            mStripView.displayStrip(strip, image);

                            onStripDisplayed(strip);

                            mCurrentStrip = strip;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mStripView.displayErrorFetchStrip();

                        Log.e(TAG, "Error fetching strip: ", e);
                    }

                    @Override
                    public void onComplete() {
                        mStripView.displayErrorFetchStrip();
                    }
                }));
    }

    public void passToStrip (Long nextId) {

        // Update current strip info in presenter
        mCurrentStrip = null;
        mStripId = nextId;
        mFetchStripType = FetchStripType.ID;

        // Interrupt old request
        mSubscriptions.clear();

        // Fetch new strip info
        fetchStrip();

        // Update fav icon
        mStripView.displayIconIsFavorite(isFav());
    }

    @Override
    public File saveSharedImageInSharedDirectory(Long id, ByteArrayOutputStream bos) {
        return mStripRepository.saveSharedImageInSharedFolder(id, bos);
    }

    public Long getStripId() {
        return mStripId;
    }

    /* (no-Javadoc) */
    @Override
    public boolean isFav() {
        return mStripRepository.isFavorite(mStripId);
    }

    /* (no-Javadoc) */
    @Override
    public int getCompressionLevelImages() {
        return mStripRepository.fetchCompressionLevelImages();
    }

}
