package com.commitstrip.commitstripreader.fullscreen;

import android.util.Log;

import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.squareup.picasso.RequestCreator;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Listens to user actions from the UI ({@link FullScreenStripActivity}), retrieves the data and
 * updates the UI as required.
 * <p />
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the StripPresenter (if it fails, it emits a compiler error).  It uses
 * {@link FullScreenStripPresenterModule} to do so.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 **/
class FullScreenStripPresenter implements FullScreenStripContrat.Presenter {

    private String TAG = "FullScreenStripP";

    private final boolean mReadFrom0Mode;

    private final StripRepository mStripRepository;
    private final FullScreenStripContrat.View mFullScreenStripView;

    private CompositeDisposable mSubscriptions;
    private StripDto mCurrentStrip;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    FullScreenStripPresenter(StripDto currentStrip,
            boolean readFrom0Mode,
            StripRepository stripRepository,
            FullScreenStripContrat.View stripView) {

        mCurrentStrip = currentStrip;
        mReadFrom0Mode = readFrom0Mode;

        mStripRepository = stripRepository;
        mFullScreenStripView = stripView;
    }


    @Override
    public void subscribe() {
        mSubscriptions = new CompositeDisposable();
    }


    @Override
    public RequestCreator getImageStrip(Long id, String url) {
        return mStripRepository.fetchImageStrip(id, url);
    }

    @Override
    public void fetchStrip(Long id) {

        if (Configuration.isIdCorrect(id)) {

            mSubscriptions.add (
                mStripRepository.fetchStrip(id, false)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableMaybeObserver<StripDto>() {
                        @Override
                        public void onSuccess(StripDto strip) {

                            // Display image
                            mFullScreenStripView.askForDisplayImage(strip);

                            // Update current strip
                            mCurrentStrip = strip;

                            // If we were reading strip in from 0 mode,update last read id and date.
                            long lastReadDate
                                    = mStripRepository.fetchLastReadDateFromTheBeginningMode();

                            if (mReadFrom0Mode && strip.getReleaseDate().getTime() > lastReadDate) {
                                mStripRepository.saveLastReadIdFromTheBeginningMode(strip.getId());

                                Long date = strip.getReleaseDate().getTime();
                                mStripRepository.saveLastReadDateFromTheBeginningMode(date);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "Could not fetch strip: ", e);
                        }

                        @Override
                        public void onComplete() {}
                    }));
        }
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void onSwipeRight() {

        if (mReadFrom0Mode) {
            passToNextStrip();
        } else {
            passToPreviousStrip();
        }
    }

    @Override
    public void onSwipeLeft() {
        if (mReadFrom0Mode) {
            passToPreviousStrip();
        } else {
            passToNextStrip();
        }
    }

    private void passToPreviousStrip () {
        Long id = mCurrentStrip.getPrevious();

        if (Configuration.isIdCorrect(id)) {
            fetchStrip(id);
        }
    }

    private void passToNextStrip () {
        Long nextId = mCurrentStrip.getNext();

        if (Configuration.isIdCorrect(nextId)) {
            fetchStrip(nextId);
        }
    }

    public boolean fetchPriorityForUseVolumeKey() {
        return mStripRepository.fetchPriorityForUseVolumeKey();
    }
}
