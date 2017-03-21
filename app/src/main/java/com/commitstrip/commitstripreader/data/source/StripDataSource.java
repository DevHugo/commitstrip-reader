package com.commitstrip.commitstripreader.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.commitstrip.commitstripreader.common.dto.DisplayStripDto;
import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.reactivestreams.Publisher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.requery.util.function.Supplier;
import okhttp3.Response;

public interface StripDataSource {

    /**
     * Fetch all strips from backend
     *
     * @return all strips from the backend
     */
    Flowable<List<StripDto>> fetchAllStrip();

    /**
     * Fetch strip from local database or network.
     *
     * @param id strip id
     * @param forceNetwork true, fetch will prioritize the network.
     * @return
     */
    Maybe<StripDto> fetchStrip(Long id, boolean forceNetwork);

    /**
     * Get a list of strip from the page to page + numberOfStripPerPage.
     * The chronological order must be respected when returning the data.
     *
     * @param numberOfStripPerPage Number of strip per page
     * @param page The wanted page number
     * @param forceNetwork prioritize network
     * @return list of strip
     */
    Flowable<StripDto> fetchListStrip(Integer numberOfStripPerPage, int page,
            boolean forceNetwork);

    /**
     * Fetch image from memory, disk or network, whichever is available first.
     *
     * @return Request object, call method into as described in the picasso library fon binding the
     * image to an ImageView.
     */
    RequestCreator fetchImageStrip(@NonNull Long id, @NonNull String url);

    /**
     * Fetch from disk, if the strip is in user favorite.
     *
     * @param id strip id
     * @return whenever the strip is in user favorite.
     */
    boolean isFavorite(@NonNull Long id);

    /**
     * Add the strip pass in parameter in the favorite
     *
     * @param mCurrentStrip The strip to save in favorite.
     * @param byteArrayOutputStream an array of image representation
     * @return strip added as favorite
     */
    Single<StripDto> addFavorite(@NonNull StripDto mCurrentStrip,
            @NonNull ByteArrayOutputStream byteArrayOutputStream);

    /**
     * Delete the favorite strip pass in parameter.
     *
     * @param id delete the strip as one of his user favorite
     * @return strip id
     */
    Maybe<Integer> deleteFavorite(@NonNull Long id);

    /**
     * Fetch all favorite strip.
     *
     * @return strips
     */
    Flowable<DisplayStripDto> fetchFavoriteStrip();

    /**
     * Fetch next favorite strip in time according to the date pass in parameter.
     *
     * @param date
     * @return the most recent strip according to the date.
     */
    Single<StripDto> fetchNextFavoriteStrip(Date date);

    /**
     * Fetch previous favorite strip in time according to the date pass in parameter.
     *
     * @param date
     * @return previous favorite strip in time
     */
    Single<StripDto> fetchPreviousFavoriteStrip(Date date);

    /**
     * Save in cache the strip image pass in parameter.
     *  @param id strip id
     * @param url image url*/
    Flowable<Integer> saveImageStripInCache(Long id, String url);

    /**
     * Save strips in local database.
     *
     * @param strips insert or update strips
     * @return strips added or update
     */
    Flowable<Iterable<StripDaoEntity>> upsertStrip(List<StripDto> strips);

    /**
     * @param id strip id
     * @param outputStream a strip image.
     * @return
     */
    File saveImageStripInCache(@NonNull Long id, @NonNull ByteArrayOutputStream outputStream);

    /**
     * Fetch random list of strip.
     *
     * @param numberOfStripPerPage number of strip wanted
     * @param alreadySeenStrip strip already seen, that we must select.
     * @return strips
     */
    Flowable<StripDto> fetchRandomListStrip(Integer numberOfStripPerPage,
            List<Long> alreadySeenStrip);

    /**
     * Fetch older strip synchronously.
     *
     * @return older strip
     */
    Maybe<StripDto> fetchOlderStrip();

    /**
     * Fetch number of strip between two date
     *  @param from start date
     * @param to end date
     */
    Flowable<StripDto> fetchNumberOfStrip(Date from, Date to);

    /**
     * Schedule strips for download.
     *
     * @param strips
     * @return strips scheduled  strips to download
     */
    Single<Integer> scheduleStripForDownload(List<StripDto> strips);

    Flowable<StripDto> fetchToDownloadImageStrip();

    void clearCacheStripForDownload();

    int fetchCompressionLevelImages();

    void saveLastReadIdFromTheBeginningMode(Long id);

    void saveLastReadDateFromTheBeginningMode(Long date);

    long fetchLastReadDateFromTheBeginningMode();

    /* (no-Javadoc) */
    boolean fetchPriorityForUseVolumeKey();

    Long fetchLastReadIdFromTheBeginningMode();

    /* (no-Javadoc) */
    void savePriorityForUseVolumeKey(boolean useVolumeKey);

    File saveSharedImageInSharedFolder(@NonNull Long id,
            @NonNull ByteArrayOutputStream outputStream);
    


    interface RemoteDataSource {

        /**
         * Fetch a strip with the id given in parameter.
         *
         * @param id strip id
         * @return strip corresponding to the id pass in parameter.
         */
        Maybe<StripDto> fetchStrip(Long id);

        /**
         * Fetch an image strip.
         *
         * @param url url where the method can fetch the image.
         * @return an picasso instance
         */
        RequestCreator fetchImageStrip(String url);

        /**
         * Fetch all strips in the remote database.
         *
         * @return all strips
         */
        Flowable<List<StripDto>> fetchAllStrip();

        /**
         * @param numberOfStripPerPage number of wanted strip
         * @param page page number
         * @return strips according to the two parameters
         */
        Flowable<StripDto> fetchListStrip(Integer numberOfStripPerPage, int page);
    }

    interface LocalDataSource {

        /**
         * Fetch a strip with the id specified in parameter in local database.
         *
         * Throw a NotSynchronizedException, if the local database have not been synchronized with the
         * org.commitstrip.commistripreader.data.source.remote yet and can not be found in local
         * database.
         *
         * @param id null id will return the most recent strip
         */
        Maybe<StripDto> fetchStrip(@Nullable Long id);

        /**
         * Fetch a list of strips.
         *
         * @param numberOfStripPerPage number of wanted strip
         * @param page page number
         * @return list strips
         */
        Flowable<StripDto> fetchListStrip(
                @NonNull Integer numberOfStripPerPage,
                @NonNull Integer page);

        /**
         * @param listId Fetch a list of strip corresponding to ids passed in parameter.
         * @return strip
         */
        Flowable<StripDto> fetchListStrip(@NonNull Iterable<Long> listId);

        /**
         * Fetch a random list of strips.
         *
         * @param numberOfStripPerPage number of wanted strip.
         * @param alreadySeenStrip list of strip that we should not select
         * @return strip list
         */
        Flowable<StripDto> fetchRandomListStrip(
                @NonNull Integer numberOfStripPerPage,
                @NonNull List<Long> alreadySeenStrip);

        /**
         * Return method for existing strip.
         *
         * @param id strip id
         * @return
         */
        Maybe<StripDaoEntity> existStrip(@NonNull Long id);

        boolean isFavorite(@NonNull Long id);

        /**
         * Add a strip as a favorite.
         *
         * @param mCurrentStrip the strip to add as favorite
         * @return the strip added as favorite
         */
        Single<StripDto> addFavorite(@NonNull StripDto mCurrentStrip);

        /**
         * Delete strip as a favorite.
         *
         * @param id strip id
         * @return the strip which is not in favorite
         */
        Maybe<Integer> deleteFavorite(@NonNull Long id);

        /**
         * Fetch all strips from the favorite list.
         *
         * @return all strips added as favorite
         */
        Flowable<StripDto> fetchFavoriteStrip();

        /**
         * Fetch the most previous strip in time.
         *
         * @param date
         * @return the most previous strip in time.
         */
        Single<StripDto> fetchPreviousFavoriteStrip(@NonNull Date date);

        /**
         * Fetch the most recent strip in time.
         *
         * @param date
         * @return the most recent in time according to the date in parameter
         */
        Single<StripDto> fetchNextFavoriteStrip(@NonNull Date date);

        /**
         * Insert or update strip according to the id.
         *
         * @param strips strip to insert or update.
         * @return added strip
         */
        Flowable<Iterable<StripDaoEntity>> upsertStrip(@NonNull Iterable<StripDto> strips);

        /**
         * Fetch most recent strip in the local database.
         *
         * @return most recent strip
         */
        Single<StripDto> fetchMostRecentStrip();

        /**
         * Fetch older strip synchronously.
         *
         * @return older strip
         */
        Maybe<StripDto> fetchOlderStrip();

        /**
         * Fetch number of strip.
         *  @param from start date
         * @param to end date*/
        Flowable<StripDto> fetchNumberOfStrip(Date from, Date to);

        /**
         * Schedule strips for download.
         *
         * @param strips
         * @return strips scheduled  strips to download
         */
        Single<Integer> scheduleStripForDownload(List<StripDto> strips);

        /**
         * Fetch every strip that are flagged for downloading their image
         *
         * @return every strip flagged for download
         */
        Flowable<StripDto> fetchToDownloadImageStrip();

        /**
         * Flag a strip as be done downloading
         *
         * @param id
         */
        Single<Integer> flagAsDownloadedImage(Long id);
    }

    interface StripImageCacheDataSource {

        /**
         * Get image from local disk. You should call {@code isImageCacheForStripExist} before using
         * this function.
         *
         * @param id strip id
         * @return an file instance where the supposed file is. If no file exist on network it will
         * return a File instance pointing to a non existing file
         */
        File getImageCacheForStrip(@NonNull Long id);

        /**
         * Try to fetch image strip from local disk, it will not try to load with network and will
         * not save it.
         *
         * @param id strip id
         * @return Picasso instance call {@code into}, if you want to display the strip.
         */
        RequestCreator fetchImageStrip(@NonNull Long id);

        /**
         * Return true if image strip is on local disk.
         *
         * @param id strip id
         * @return true if an image exist, false otherwise.
         */
        boolean isImageCacheForStripExist(@NonNull Long id);

        /**
         * Delete strip image from local disk.
         *
         * @param id strip id
         * @return true, the file have been successfully deleted, false otherwise.
         */
        boolean deleteImageStripInCache(@NonNull Long id);

        /**
         * @param id strip id
         * @param compression compression level of the image, between 1 and 100.
         * @param requestCreator picasso instance
         */
        void saveImageStripInCache(@NonNull Long id, @NonNull RequestCreator requestCreator, int compression);

        /**
         * @param id strip id
         * @param url picasso instance
         * @param compression compression level of the image, between 1 and 100.
         */
        Flowable<Long> saveImageStripInCache(@NonNull Long id, @NonNull String url, int compression);

        /**
         * @param id strip id
         * @param outputStream bytes image representation
         * @return path to saved file
         */
        File saveImageStripInCache(@NonNull Long id, @NonNull ByteArrayOutputStream outputStream);

        /**
         * List all cached image on local disk
         * @return list of cached image id
         */
        List<Long> getCachedImagesId();

        /**
         * Use it for saving the image by using method {@code into} on an {@code picasso instance}.
         * @param id strip id
         * @param compression compression
         * @return target instance for saving the file
         */
        Target getTarget(@NonNull Long id, int compression);

        /**
         * Clear cache, skips strips pass in parameter
         *
         * @param strips skip strip pass in parameter.
         * @return
         */
        Flowable<File> clearCache(List<StripDto> strips);

        File saveSharedImageInSharedFolder(Long id, ByteArrayOutputStream outputStream);
    }

    interface StripSharedPreferencesDataSource {

        /**
         * Get compression level of images from the user shared preferences repository.
         *
         * @return compression level between 1 and 100 (top quality image)
         */
        int fetchCompressionLevelImages ();

        /**
         * Get compression level of images from the user shared preferences repository.
         *
         * @param compression level between 1 and 100 (top quality image)
         */
        void saveCompressionLevelImages (int compression);

        /**
         * Return last id read from the beginning mode.
         *
         * @return last id read by user
         */
        long fetchLastReadIdFromTheBeginningMode();

        /**
         * Set the last id read from the beginning mode.
         *
         * @return last id read by user
         */
        void saveLastReadIdFromTheBeginningMode(Long lastId);

        /**
         * Return last date read from the beginning mode.
         *
         * @return last date read by user
         */
        long fetchLastReadDateFromTheBeginningMode();

        /* (no-Javadoc) */
        boolean fetchPriorityForUseVolumeKey();

        /**
         * Set the last date read from the beginning mode.
         *
         * @return last vate read by user
         */
        void saveLastReadDateFromTheBeginningMode(Long date);

        /* (no-Javadoc) */
        void savePriorityForUseVolumeKey(boolean useVolumeKey);
    }
}
