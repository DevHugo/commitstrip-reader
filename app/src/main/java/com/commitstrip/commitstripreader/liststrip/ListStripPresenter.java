package com.commitstrip.commitstripreader.liststrip;

import android.util.Log;

import com.commitstrip.commitstripreader.common.dto.StripWithImageDto;
import com.commitstrip.commitstripreader.common.liststrip.ListStripAbstractPresenter;
import com.commitstrip.commitstripreader.common.liststrip.ListStripContract;
import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.batch.SaveStripBatchTask;
import com.commitstrip.commitstripreader.util.CheckInternetConnection;
import com.commitstrip.commitstripreader.util.di.ActivityScoped;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Notification;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

class ListStripPresenter extends ListStripAbstractPresenter implements ListStripContract.Presenter {

    private List<StripDto> mListCurrentDisplayStrip;
    private String TAG = "ListStripPresenter";

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    public ListStripPresenter(StripRepository stripRepository, ListStripContract.View stripView) {
        super(stripRepository, stripView);

        mListCurrentDisplayStrip = new ArrayList<>();
    }

    @Override
    public void fetchStrip(Integer numberOfStripPerPage, int page) {
        if (Configuration.OFFLINE_MODE || !CheckInternetConnection.isOnline()) {

            mListStripView.disableRefreshStrip();
        }

        mSubscriptions.add(mStripRepository
                .fetchListStrip(numberOfStripPerPage, page, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<StripDto>() {
                    @Override
                    public void onNext(StripDto strip) {
                        mListCurrentDisplayStrip.add(strip);
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {
                        List<StripWithImageDto> displayStrips = new ArrayList<>();
                        for (int i = 0; i < mListCurrentDisplayStrip.size(); i++) {
                            displayStrips.add(
                                    convertStripDtoToStripWithImageDto(mListCurrentDisplayStrip.get(i)));
                        }

                        mListStripView.addMoreStrips(displayStrips);
                        mListCurrentDisplayStrip.clear();
                    }
                }));
    }

    @Override
    public void refreshStrip() {

        if (Configuration.OFFLINE_MODE || CheckInternetConnection.isOnline()){

            mListStripView.cancelRefreshStrip();
        } else {

            Flowable<StripDto> flowableStrip =
                    mStripRepository
                            .fetchAllStrip()
                            .flatMap(Flowable::fromIterable)
                            .subscribeOn(Schedulers.newThread());

            // Save all item in strips flux
            SaveStripBatchTask saveStripBatchTask = new SaveStripBatchTask(mStripRepository);
            Single<List<StripDto>> result = saveStripBatchTask.execute(flowableStrip).toList();

            // Callback we should call during saving
            Consumer<List<StripDto>> onNext = strips -> {

                if (strips.size() >= 0) {

                    Collections.sort(strips, (strip, other)
                            -> Long.valueOf(strip.getReleaseDate().getTime())
                            .compareTo(other.getReleaseDate().getTime()));


                    List<StripWithImageDto> displayNewStrip = new ArrayList<>();
                    StripWithImageDto strip;
                    for (int i = 0; i < strips.size(); i++) {

                        strip = convertStripDtoToStripWithImageDto(strips.get(i));

                        if (mListStripView.stripAlreadyDisplay(strip)) {
                            displayNewStrip.add(strip);
                        }
                    }

                    mListStripView.addMoreStripsFromTheStart(displayNewStrip);
                }

                mListStripView.cancelRefreshStrip();
            };
            Consumer<Throwable> onError = error -> {
                mListStripView.cancelRefreshStrip();

                Log.e(TAG, "", error);
            };

            result.subscribe(onNext, onError);
        }
    }

    @Override
    public int fetchCompressionLevelImages() {
        return mStripRepository.fetchCompressionLevelImages();
    }

    @Override
    public File saveSharedImageInSharedFolder(Long id, ByteArrayOutputStream bos) {
        return mStripRepository.saveSharedImageInSharedFolder(id, bos);
    }
}
