package com.commitstrip.commitstripreader.common.liststrip;

import android.support.annotation.NonNull;
import android.util.Log;

import com.commitstrip.commitstripreader.common.dto.StripWithImageDto;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

/**
 * Listens to user actions from the UI ({@link ListStripFragment}), retrieves the data and updates
 * the UI as required.
 * <p/>
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the StripPresenter (if it fails, it emits a compiler error).  It uses
 * {@link ListStripPresenterModule} to do so.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 **/
public abstract class ListStripAbstractPresenter implements ListStripContract.Presenter {

    private String TAG = "ListStripPresenter";

    protected CompositeDisposable mSubscriptions;

    protected ListStripContract.View mListStripView;
    protected StripRepository mStripRepository;

    protected List<StripDto> mListStripCurrentUpdate;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    public ListStripAbstractPresenter(
            @NonNull StripRepository stripRepository,
            @NonNull ListStripContract.View stripView) {
        mStripRepository = stripRepository;
        mListStripView = stripView;

        mListStripView.setAbstractPresenter(this);
    }

    /* (no-Javadoc) */
    @Override
    public void subscribe() {
        mSubscriptions = new CompositeDisposable();
    }

    /* (no-Javadoc) */
    public abstract void fetchStrip(Integer numberOfStripPerPage, int page);

    /* (no-Javadoc) */
    public abstract void refreshStrip();

    /* (no-Javadoc) */
    @Override
    public void addFavorite(StripWithImageDto strip, ByteArrayOutputStream bytes) {

        mSubscriptions.add(
                mStripRepository.addFavorite(strip, bytes)
                        .subscribeWith(new DisposableSingleObserver<StripDto>() {

                            @Override
                            public void onSuccess(StripDto strip) {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Could not add in favorite", e);

                                mListStripView.displayErrorAddFavorite();
                            }
                        }));
    }

    /* (no-Javadoc) */
    @Override
    public void deleteFavorite(Long id) {

        mSubscriptions.add(mStripRepository.deleteFavorite(id).subscribeWith(
                new DisposableMaybeObserver<Integer>() {

                    @Override
                    public void onSuccess(Integer numberRow) {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Could not delete in favorite", e);

                        mListStripView.displayErrorAddFavorite();
                    }

                    @Override
                    public void onComplete() {}
                }));
    }

    /* (no-Javadoc) */
    @Override
    public File saveSharedImage(Long id, ByteArrayOutputStream stream) {
        return mStripRepository.saveImageStripInCache(id, stream);
    }

    /* (no-Javadoc) */
    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    protected StripWithImageDto convertStripDtoToStripWithImageDto(StripDto from) {

        StripWithImageDto to = new StripWithImageDto();
        to.setId(from.getId());
        to.setTitle(from.getTitle());
        to.setReleaseDate(from.getReleaseDate());
        to.setContent(from.getContent());
        to.setNext(from.getNext());
        to.setPrevious(from.getPrevious());
        to.setThumbnail(from.getThumbnail());
        to.setUrl(from.getUrl());

        Boolean isFav = mStripRepository.isFavorite(from.getId());
        to.setFavorite(isFav);

        to.setImageRequestCreator(
                mStripRepository.fetchImageStrip(
                        from.getId(),
                        from.getContent()
                )
        );

        return to;
    }
}
