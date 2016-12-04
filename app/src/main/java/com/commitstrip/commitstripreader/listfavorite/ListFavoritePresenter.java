package com.commitstrip.commitstripreader.listfavorite;

import android.util.Log;

import com.commitstrip.commitstripreader.data.source.StripRepository;

import java.lang.ref.SoftReference;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Listens to user actions from the UI ({@link ListFavoriteFragment}), retrieves the data and updates the
 * UI as required.
 * <p />
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the ListFavoritePresenter (if it fails, it emits a compiler error).  It uses
 * {@link ListFavoritePresenterModule} to do so.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 **/
public class ListFavoritePresenter implements ListFavoriteContract.Presenter {

    private final CompositeDisposable mSubscriptions;

    private ListFavoriteContract.View mListFavoriteView;
    private final StripRepository mStripRepository;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    public ListFavoritePresenter(StripRepository stripRepository, ListFavoriteContract.View stripView) {
        mStripRepository = stripRepository;
        mListFavoriteView = stripView;

        mSubscriptions = new CompositeDisposable ();
        mListFavoriteView.setPresenter(this);
    }

    @Override
    public void subscribe() {}

    @Override
    public void fetchFavoriteStrip() {
        mSubscriptions.add(
            mStripRepository.fetchFavoriteStrip().subscribeWith(new FavoriteObserver<>(mListFavoriteView))
        );
    }

    public static class FavoriteObserver<T extends com.commitstrip.commitstripreader.listfavorite.ListFavoriteDto> extends DisposableSubscriber<T> {
        private String TAG = "FavoriteObserver";

        private SoftReference<ListFavoriteContract.View> mView;
        private boolean gotResult = false;

        public FavoriteObserver (ListFavoriteContract.View view) {
            mView = new SoftReference<>(view);
        }

        @Override
        public void onNext(ListFavoriteDto favorite) {

            ListFavoriteContract.View view = mView.get();

            if (view != null) {
                view.updateListFavorite( favorite);
            }

            gotResult = true;
        }

        @Override
        public void onError(Throwable e) {
            ListFavoriteContract.View view = mView.get();

            if (view != null)
                view.showError();

            Log.e(TAG, "", e);
        }

        @Override
        public void onComplete() {
            if (!gotResult) {
                ListFavoriteContract.View view = mView.get();

                if (view != null)
                    view.noResult();
            }
        }
    }

    @Override
    public void unsubscribe() {
        mListFavoriteView.setPresenter(null);
        mSubscriptions.clear();
    }
}
