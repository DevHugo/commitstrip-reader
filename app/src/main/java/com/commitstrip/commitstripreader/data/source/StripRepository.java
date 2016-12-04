package com.commitstrip.commitstripreader.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.data.source.local.exception.NotSynchronizedException;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.listfavorite.ListFavoriteDto;
import com.squareup.picasso.RequestCreator;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Singleton
public class StripRepository implements StripDataSource {

    private String TAG = "StripRepository";

    private final StripDataSource.LocalDataSource mStripLocalDataSource;
    private final StripDataSource.RemoteDataSource mStripRemoteDataSource;
    private final StripDataSource.StripImageCacheDataSource mStripImageCacheDataSource;

    /**
     * The number of minutes before refreshing the data.
     */
    private final Integer SHOULD_REFRESH_DATA = 60;

    @VisibleForTesting
    private Long mLastTimeRefreshData = 0L;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    @VisibleForTesting
    boolean mCacheIsDirty = false;

    /**
     * By marking the constructor with {@code @Inject}, Dagger will try to inject the dependencies
     * required to create an instance of the TasksRepository. Because {@link StripDataSource} is an
     * interface, we must provide to Dagger a way to build those arguments, this is done in
     * {@link StripRepositoryModule}.
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
                    StripDataSource.StripImageCacheDataSource stripImageCacheDataSource) {
        mStripRemoteDataSource = stripRemoteDataSource;
        mStripLocalDataSource = stripLocalDataSource;
        mStripImageCacheDataSource = stripImageCacheDataSource;
    }

    /**
     * Get the strip with the id from local or org.commitstrip.commistripreader.data.source.remote, whichever is available first.
     *
     * @param id Id send by the server
     * @return The strip with the id specified in parameter. If id is null, the strip with the more recent date is returned.
     */
    @Override
    public Single<StripDto> fetchStrip(@NonNull Long id) {

        Single<StripDto> localStrip = mStripLocalDataSource.fetchStrip(id);
        Single<StripDto> remoteStrip = mStripRemoteDataSource.fetchStrip(id);

        Long timestamp = System.currentTimeMillis() / 1000;

        // If the cache is dirty or the last call was too far ago, we need to fetch data from the org.commitstrip.commistripreader.data.source.remote.
        if(mCacheIsDirty || timestamp > mLastTimeRefreshData+(SHOULD_REFRESH_DATA*60)) {

            return remoteStrip
                    .onErrorResumeNext(localStrip)
                    .doOnSuccess(converter -> mLastTimeRefreshData = timestamp);
        }

        return localStrip.onErrorResumeNext(remoteStrip);
    }

    /**
     * Fetch image from memory, disk or network, whichever is available first.
     *
     * @param url
     * @return Request object, call method into as described in the picasso library fon binding the image to an ImageView.
     */
    public RequestCreator fetchImageStrip (@NonNull Long id, @NonNull String url) {

        if (mStripImageCacheDataSource.isImageCacheForStripExist(id)) {
            return mStripImageCacheDataSource.fetchImageStrip(id);
        }

        return mStripRemoteDataSource.fetchImageStrip(url);
    }

    /**
     * Fetch from disk, if the strip is in user favorite.
     *
     * @param id
     * @return whenever the strip is in user favorite.
     */
    public boolean isFavorite (@NonNull Long id) {
        return mStripLocalDataSource.isFavorite(id);
    }

    /**
     * Add the strip pass in parameter in the favorite
     *
     * @param mCurrentStrip The strip to save in favorite.
     */
    public Single<StripDto> addFavorite(@NonNull StripDto mCurrentStrip) {
        // Add in local favorite
        return Single.just(mCurrentStrip)
                .flatMap(mStripLocalDataSource::addFavorite)
                // We should fetch the image to add it in cache
                .doOnSuccess(strip -> {
                    if (!mStripImageCacheDataSource.isImageCacheForStripExist(strip.getId())) {
                        RequestCreator requestCreator = mStripRemoteDataSource.fetchImageStrip(mCurrentStrip.getContent());
                        mStripImageCacheDataSource.saveImageStripInCache(mCurrentStrip.getId(), requestCreator);
                    }
                });

        // TODO Schedule a job, to retry if there is no internet connexion available at this moment.
    }

    /**
     * Delete the favorite strip pass in parameter.
     *
     * @param mCurrentStrip
     */
    public Single<StripDto> deleteFavorite(@NonNull StripDto mCurrentStrip) {
        return Single.just(mCurrentStrip)
                .flatMap(mStripLocalDataSource::deleteFavorite)
                // Delete image
                .doOnSuccess(strip -> {
                    if (mStripImageCacheDataSource.isImageCacheForStripExist(mCurrentStrip.getId()))
                        mStripImageCacheDataSource.deleteImageStripInCache(mCurrentStrip.getId());
                });
    }

    /**
     * Fetch all favorite strip.
     *
     * @return
     */
    @Override
    public Flowable<ListFavoriteDto> fetchFavoriteStrip() {
        return mStripLocalDataSource.fetchFavoriteStrip()
                .map(strip -> {

                    ListFavoriteDto favorite = new ListFavoriteDto();

                    favorite.setId(strip.getId());
                    favorite.setTitle(strip.getTitle());

                    RequestCreator requestCreator = fetchImageStrip (strip.getId(), strip.getContent());
                    favorite.setImageRequestCreator(requestCreator);

                    return favorite;
                });
    }

    /**
     * Fetch next favorite strip according to the date pass in parameter.
     *
     * @param date
     * @return
     */
    @Override
    public Single<StripDto> fetchNextFavoriteStrip(@NonNull Date date) {
        return mStripLocalDataSource.fetchNextFavoriteStrip(date);
    }

    /**
     * Fetch previous favorite strip according to the date pass in parameter
     *
     * @param date
     * @return
     */
    @Override
    public Single<StripDto> fetchPreviousFavoriteStrip(@NonNull Date date) {
        return mStripLocalDataSource.fetchPreviousFavoriteStrip(date);
    }

    /**
     * Save in cache the strip image pass in parameter
     *
     * @param id
     * @param url
     */
    @Override
    public void saveImageStripInCache(@NonNull Long id, @NonNull String url) {
        mStripImageCacheDataSource.saveImageStripInCache(id, url);
    }

    /**
     * Synchronize all metadata from the org.commitstrip.commistripreader.data.source.remote to the local database.
     */
    public void syncFromRemote () {

       /*mStripRemoteDataSource
                .fetchFirstThousandStrip()
                // Delete from the stream all strip which are before the most recent strip save in local database.
                .flatMap(
                        strip ->
                            mStripLocalDataSource.fetchMostRecentStrip()
                                .filter(mostRecentStrip -> strip.getDate().after(mostRecentStrip.getDate()))
                                .map(mostRecentStrip -> strip)
                )
                .map(mStripLocalDataSource::saveStrip)
                .subscribe(strip -> Log.d(TAG, "Saved strip in localdatabase"));*/

    }

    /**
     * Fetch all strips from backend
     *
     * @return all strips from the backend
     */
    @Override
    public Flowable<List<StripDto>> fetchAllStrip() {
        return mStripRemoteDataSource.fetchAllStrip();
    }

    /**
     * Save strips in local database
     *
     * @param strips
     */
    @Override
    public Flowable<Iterable<StripDaoEntity>> saveStrips(@NonNull List<StripDto> strips) {
       return mStripLocalDataSource.saveStrip(strips);
    }

    /**
     * Fetch the strip with id pass in parameter in local database.
     *
     * @return all strips from the backend
     */
    @Override
    public boolean existStripInCache(@NonNull Long id) {
        return mStripLocalDataSource.existStrip(id);
    }
}