package com.commitstrip.commitstripreader.data.source;

import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.data.source.local.exception.NotSynchronizedException;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.listfavorite.ListFavoriteDto;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface StripDataSource {
    Flowable<List<StripDto>> fetchAllStrip();
    Single fetchStrip(Long date) throws NotSynchronizedException;
    Flowable<ListFavoriteDto> fetchFavoriteStrip();

    Single<StripDto> fetchNextFavoriteStrip(Date date);
    Single<StripDto> fetchPreviousFavoriteStrip(Date date);

    void saveImageStripInCache(Long id, String url);
    Flowable<Iterable<StripDaoEntity>> saveStrips(List<StripDto> strips);

    boolean existStripInCache(Long id);

    interface LocalDataSource {
        Single<StripDto> fetchStrip(Long id);
        Single<StripDto> fetchPreviousFavoriteStrip(Date date);
        Flowable<StripDto> fetchFavoriteStrip();

        boolean cacheForStripShouldBeClean(Long id);

        boolean existStrip(Long id);

        boolean isFavorite(Long id);
        Single<StripDto> addFavorite(StripDto mCurrentStrip);
        Single<StripDto> deleteFavorite(StripDto mCurrentStrip);

        Single<StripDto> fetchNextFavoriteStrip(Date date);

        Single<StripDaoEntity> saveStrip(StripDto strip);
        Flowable<Iterable<StripDaoEntity>> saveStrip(List<StripDto> strips);

        Single<StripDto> fetchMostRecentStrip();

    }

    interface RemoteDataSource {

        Single<StripDto> fetchStrip(Long id);
        RequestCreator fetchImageStrip(String url);

        Flowable<List<StripDto>> fetchAllStrip();
    }

    interface StripImageCacheDataSource {

        File getImageCacheForStrip(Long id);

        RequestCreator fetchImageStrip(Long id);

        boolean isImageCacheForStripExist(Long id);

        boolean deleteImageStripInCache(Long id);

        void saveImageStripInCache(Long id, RequestCreator requestCreator);

        void saveImageStripInCache(Long id, String url);

        Target getTarget(Long id);
    }
}
