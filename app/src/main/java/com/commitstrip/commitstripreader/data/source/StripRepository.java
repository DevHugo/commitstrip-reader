package com.commitstrip.commitstripreader.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pair;
import android.util.Log;

import com.commitstrip.commitstripreader.common.dto.DisplayStripDto;
import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.module.DataSourceModule;
import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.util.CheckInternetConnection;
import com.commitstrip.commitstripreader.util.Preconditions;
import com.commitstrip.commitstripreader.util.converter.StripDtoToDisplayStripDto;
import com.squareup.picasso.RequestCreator;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

public class StripRepository implements StripDataSource {

    private final StripDataSource.LocalDataSource mStripLocalDataSource;
    private final StripDataSource.RemoteDataSource mStripRemoteDataSource;
    private final StripDataSource.StripImageCacheDataSource mStripImageCacheDataSource;
    private final StripDataSource.StripSharedPreferencesDataSource mSharedPreferences;

    /**
     * The number of minutes before refreshing the data.
     */
    private final Integer SHOULD_REFRESH_DATA = 60;

    @VisibleForTesting
    private Long mLastTimeRefreshData = 0L;

    /**
     * By marking the constructor with {@code @Inject}, Dagger will try to inject the dependencies
     * required to create an instance of the TasksRepository. Because {@link StripDataSource} is an
     * interface, we must provide to Dagger a way to build those arguments, this is done in
     * {@link DataSourceModule}.
     * <P>
     * When two arguments or more have the same type, we must provide to Dagger a way to
     * differentiate them. This is done using a qualifier.
     * <p>
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    StripRepository(StripDataSource.RemoteDataSource stripRemoteDataSource,
            StripDataSource.LocalDataSource stripLocalDataSource,
            StripDataSource.StripImageCacheDataSource stripImageCacheDataSource,
            StripDataSource.StripSharedPreferencesDataSource sharedPreferences) {
        mStripRemoteDataSource = stripRemoteDataSource;
        mStripLocalDataSource = stripLocalDataSource;
        mStripImageCacheDataSource = stripImageCacheDataSource;
        mSharedPreferences = sharedPreferences;
    }

    /* (no-Javadoc) */
    @Override
    public Maybe<StripDto> fetchStrip(@Nullable Long id, boolean forceNetwork) {

        Maybe<StripDto> localStrip = mStripLocalDataSource.fetchStrip(id);
        Maybe<StripDto> remoteStrip = mStripRemoteDataSource.fetchStrip(id);

        if (Configuration.OFFLINE_MODE) {
            return localStrip;
        }

        Long timestamp = System.currentTimeMillis() / 1000;

        // If the cache is dirty or the last call was too far ago, we need to fetch data from the
        // org.commitstrip.commistripreader.data.source.remote.
        if (forceNetwork || (timestamp > (mLastTimeRefreshData + (SHOULD_REFRESH_DATA * 60)))) {

            return remoteStrip
                    .onErrorResumeNext(localStrip)
                    .doOnSuccess(onSuccess -> mLastTimeRefreshData = timestamp);
        }

        return localStrip.onErrorResumeNext(remoteStrip);
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<StripDto> fetchListStrip(Integer numberOfStripPerPage, int page,
            boolean forceNetwork) {

        Flowable<StripDto> localStrip =
                mStripLocalDataSource.fetchListStrip(numberOfStripPerPage, page);

        Flowable<StripDto> remoteStrip =
                mStripRemoteDataSource.fetchListStrip(numberOfStripPerPage, page);

        Long timestamp = System.currentTimeMillis() / 1000;

        if (Configuration.OFFLINE_MODE) {
            return localStrip;
        }

        // If the cache is dirty or the last call was too far ago, we need to fetch data from the
        // org.commitstrip.commistripreader.data.source.remote.
        if (forceNetwork || (timestamp > (mLastTimeRefreshData + (SHOULD_REFRESH_DATA * 60)))) {

            return remoteStrip
                    .onErrorResumeNext(throwable -> localStrip)
                    .doOnTerminate(() -> mLastTimeRefreshData = timestamp);
        }

        return localStrip.onErrorResumeNext(throwable -> {
            if (page == 0) {
                return remoteStrip
                        .onErrorResumeNext(
                                fetchStripWithImageThatAlreadyInCache()
                                        .toSortedList((strip, other) ->
                                                strip.getReleaseDate().compareTo(other.getReleaseDate()))
                                        .flattenAsFlowable(strips -> strips));
            }

            return remoteStrip;
        });
    }

    /**
     * Fetch a list of strip which image cache exist.
     *
     * @return strips
     */
    private Flowable<StripDto> fetchStripWithImageThatAlreadyInCache() {

        Iterable<Long> listId = mStripImageCacheDataSource.getCachedImagesId();

        return mStripLocalDataSource.fetchListStrip(listId);
    }

    /* (no-Javadoc) */
    @Override
    public RequestCreator fetchImageStrip(@NonNull Long id, @NonNull String url) {

        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(url);

        if (mStripImageCacheDataSource.isImageCacheForStripExist(id)) {
            return mStripImageCacheDataSource.fetchImageStrip(id);
        } else {
            return mStripRemoteDataSource.fetchImageStrip(url);
        }
    }

    /* (no-Javadoc) */
    @Override
    public boolean isFavorite(@NonNull Long id) {
        Preconditions.checkNotNull(id);

        return mStripLocalDataSource.isFavorite(id);
    }

    /* (no-Javadoc) */
    @Override
    public Single<StripDto> addFavorite(@NonNull StripDto mCurrentStrip,
            @NonNull ByteArrayOutputStream byteArrayOutputStream) {

        Preconditions.checkNotNull(mCurrentStrip);
        Preconditions.checkNotNull(byteArrayOutputStream);

        // Add in local favorite
        return Single.just(mCurrentStrip)
                .flatMap(mStripLocalDataSource::addFavorite)
                // We should fetch the image to add it in cache
                .doOnSuccess(strip -> {
                    if (!mStripImageCacheDataSource.isImageCacheForStripExist(strip.getId())) {

                        if (byteArrayOutputStream.size() != 0) {

                            mStripImageCacheDataSource.saveImageStripInCache(
                                    mCurrentStrip.getId(),
                                    byteArrayOutputStream
                            );

                        } else {
                            RequestCreator requestCreator =
                                    mStripRemoteDataSource.fetchImageStrip(
                                            mCurrentStrip.getContent());

                            mStripImageCacheDataSource.saveImageStripInCache(
                                    mCurrentStrip.getId(),
                                    requestCreator,
                                    fetchCompressionLevelImages());
                        }
                    }
                });

        // TODO Schedule a job, to retry if there is no internet connexion available at this moment.
    }

    /* (no-Javadoc) */
    @Override
    public Maybe<Integer> deleteFavorite(@NonNull Long id) {

        Preconditions.checkNotNull(id);

        return Maybe.just(id)
                .flatMap(mStripLocalDataSource::deleteFavorite)
                // Delete image
                .doOnSuccess(numberRowAffected -> {
                    if (numberRowAffected > 0) {
                        if (mStripImageCacheDataSource.isImageCacheForStripExist(id)) {
                            mStripImageCacheDataSource.deleteImageStripInCache(id);
                        }
                    }
                });
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<DisplayStripDto> fetchFavoriteStrip() {
        return mStripLocalDataSource.fetchFavoriteStrip()
                .map(new StripDtoToDisplayStripDto())
                .map(strip -> {

                    RequestCreator requestCreator = fetchImageStrip(strip.getId(), strip.getContent());
                    strip.setImageRequestCreator(requestCreator);

                    return strip;
                });
    }

    /* (no-Javadoc) */
    @Override
    public Single<StripDto> fetchNextFavoriteStrip(@NonNull Date date) {
        Preconditions.checkNotNull(date);

        return mStripLocalDataSource.fetchNextFavoriteStrip(date);
    }

    /* (no-Javadoc) */
    @Override
    public Single<StripDto> fetchPreviousFavoriteStrip(@NonNull Date date) {
        Preconditions.checkNotNull(date);

        return mStripLocalDataSource.fetchPreviousFavoriteStrip(date);
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<Integer> saveImageStripInCache(@NonNull Long id, @NonNull String url) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(url);

        return Flowable.just(new Pair<>(id, url))
                .filter(pair -> !mStripImageCacheDataSource.isImageCacheForStripExist(pair.first))
                .flatMap(pair -> mStripImageCacheDataSource
                        .saveImageStripInCache(pair.first, pair.second, fetchCompressionLevelImages()))
                .flatMapSingle(mStripLocalDataSource::flagAsDownloadedImage);
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<List<StripDto>> fetchAllStrip() {
        return mStripRemoteDataSource.fetchAllStrip();
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<Iterable<StripDaoEntity>> upsertStrip(@NonNull List<StripDto> strips) {
        Preconditions.checkNotNull(strips);

        return mStripLocalDataSource.upsertStrip(strips);
    }

    /* (no-Javadoc) */
    @Override
    public File saveImageStripInCache(@NonNull Long id,
            @NonNull ByteArrayOutputStream outputStream) {

        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(outputStream);

        return mStripImageCacheDataSource.saveImageStripInCache(id, outputStream);
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<StripDto> fetchRandomListStrip(Integer numberOfStripPerPage,
            List<Long> alreadySeenStrip) {

        Preconditions.checkNotNull(numberOfStripPerPage);
        Preconditions.checkNotNull(alreadySeenStrip);

        // We got a internet connexion, we should use the local database otherwise we should get only
        // displayable image
        if (CheckInternetConnection.isOnline() && !Configuration.OFFLINE_MODE) {

            return mStripLocalDataSource.fetchRandomListStrip(numberOfStripPerPage,
                    alreadySeenStrip);
        } else {
            List<Long> id = mStripImageCacheDataSource.getCachedImagesId();

            List<Long> selectedId = new ArrayList<>();
            Long proposedId;
            Random randomGenerator = new Random();

            if (id.size() > (numberOfStripPerPage + alreadySeenStrip.size())) {

                while (selectedId.size() <= numberOfStripPerPage) {
                    proposedId = id.get(randomGenerator.nextInt(id.size()));

                    if (!alreadySeenStrip.contains(proposedId) &&
                            !selectedId.contains(proposedId)) {
                        selectedId.add(proposedId);
                    }
                }

                return mStripLocalDataSource.fetchListStrip(selectedId);
            } else {
                return mStripLocalDataSource.fetchListStrip(id);
            }
        }
    }

    /* (no-Javadoc) */
    @Override
    public Maybe<StripDto> fetchOlderStrip() {
        return mStripLocalDataSource.fetchOlderStrip();
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<StripDto> fetchNumberOfStrip(Date from, Date to) {
        return mStripLocalDataSource.fetchNumberOfStrip(from, to);
    }

    /* (no-Javadoc) */
    @Override
    public Single<Integer> scheduleStripForDownload(List<StripDto> strips) {
        return mStripLocalDataSource.scheduleStripForDownload(strips);
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<StripDto> fetchToDownloadImageStrip() {
        return mStripLocalDataSource.fetchToDownloadImageStrip();
    }

    /* (no-Javadoc) */
    @Override
    public void clearCacheStripForDownload() {
        mStripLocalDataSource
                .fetchFavoriteStrip()
                .toList()
                .flatMap(strips -> mStripImageCacheDataSource.clearCache(strips).toList());
    }

    /* (no-Javadoc) */
    @Override
    public int fetchCompressionLevelImages() {
        return mSharedPreferences.fetchCompressionLevelImages();
    }

    /* (no-Javadoc) */
    @Override
    public void saveLastReadIdFromTheBeginningMode (Long id) {
        mSharedPreferences.saveLastReadIdFromTheBeginningMode(id);
    }

    /* (no-Javadoc) */
    @Override
    public void saveLastReadDateFromTheBeginningMode(Long date) {
        mSharedPreferences.saveLastReadDateFromTheBeginningMode(date);
    }

    /* (no-Javadoc) */
    @Override
    public long fetchLastReadDateFromTheBeginningMode() {
        return mSharedPreferences.fetchLastReadDateFromTheBeginningMode();
    }

    /* (no-Javadoc) */
    @Override
    public Long fetchLastReadIdFromTheBeginningMode() {
        return mSharedPreferences.fetchLastReadIdFromTheBeginningMode();
    }

    /* (no-Javadoc) */
    @Override
    public boolean fetchPriorityForUseVolumeKey() {
        return mSharedPreferences.fetchPriorityForUseVolumeKey();
    }

    /* (no-Javadoc) */
    @Override
    public void savePriorityForUseVolumeKey(boolean useVolumeKey) {
        mSharedPreferences.savePriorityForUseVolumeKey(useVolumeKey);
    }

    @Override
    public File saveSharedImageInSharedFolder(@NonNull Long id,
            @NonNull ByteArrayOutputStream outputStream) {
        return mStripImageCacheDataSource.saveSharedImageInSharedFolder(id, outputStream);
    }
}
