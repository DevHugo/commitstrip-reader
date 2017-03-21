package com.commitstrip.commitstripreader.random;

import com.commitstrip.commitstripreader.common.dto.StripWithImageDto;
import com.commitstrip.commitstripreader.common.liststrip.ListStripAbstractPresenter;
import com.commitstrip.commitstripreader.common.liststrip.ListStripContract;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class RandomStripPresenter extends ListStripAbstractPresenter {

    private List<Long> mAlreadyDisplayedId;
    private List<StripDto> mListCurrentDisplayStrip;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    public RandomStripPresenter(StripRepository stripRepository,
            ListStripContract.View stripView) {
        super(stripRepository, stripView);

        mAlreadyDisplayedId = new ArrayList<>();
        mListCurrentDisplayStrip = new ArrayList<>();
    }

    @Override
    public void fetchStrip(Integer numberOfStripPerPage, int page) {

        mSubscriptions.add(
                fetchRandomStrip(numberOfStripPerPage)
                        .subscribeWith(new DisposableSubscriber<StripDto>() {
                            @Override public void onNext(StripDto strip) {
                                mAlreadyDisplayedId.add(strip.getId());
                                mListCurrentDisplayStrip.add(strip);
                            }

                            @Override public void onError(Throwable e) {}

                            @Override public void onComplete() {
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
    public int fetchCompressionLevelImages() {
        return mStripRepository.fetchCompressionLevelImages();
    }

    @Override
    public File saveSharedImageInSharedFolder(Long id, ByteArrayOutputStream bos) {
        return mStripRepository.saveSharedImageInSharedFolder(id, bos);
    }

    @Override
    public void refreshStrip() {
        // Save all item in strips flux
        mListStripCurrentUpdate = new ArrayList<>();

        mSubscriptions.add(
                fetchRandomStrip(mListStripView.getNumberStripPerPage())
                        .subscribeWith(new DisposableSubscriber<StripDto>() {
                            @Override public void onNext(StripDto strip) {
                                mListStripCurrentUpdate.add(strip);
                            }

                            @Override public void onError(Throwable e) {
                                mListStripView.cancelRefreshStrip();
                            }

                            @Override public void onComplete() {
                                if (mListStripCurrentUpdate.size() >= 0) {

                                    mListStripView.clearStripDisplayed();

                                    List<StripWithImageDto> displayNewStrip = new ArrayList<>();
                                    for (int i = 0; i < mListStripCurrentUpdate.size(); i++) {
                                        displayNewStrip.add(
                                                convertStripDtoToStripWithImageDto(mListStripCurrentUpdate.get(i)));
                                    }

                                    mListStripView.addMoreStripsFromTheStart(displayNewStrip);
                                }

                                mListStripCurrentUpdate.clear();
                                mListStripView.cancelRefreshStrip();
                            }
                        }));
    }

    private Flowable<StripDto> fetchRandomStrip(Integer numberOfStripPerPage) {
        return mStripRepository.fetchRandomListStrip(numberOfStripPerPage, mAlreadyDisplayedId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
