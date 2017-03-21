package com.commitstrip.commitstripreader.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.commitstrip.commitstripreader.data.source.StripDataSource;
import com.commitstrip.commitstripreader.data.source.local.converter.ListStripDtoToListStripDao;
import com.commitstrip.commitstripreader.data.source.local.converter.StripDaoToStripDto;
import com.commitstrip.commitstripreader.data.source.local.converter.StripDtoToStripDao;
import com.commitstrip.commitstripreader.dto.StripDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

@Singleton
public class StripLocalDataSource implements StripDataSource.LocalDataSource {

    private final ReactiveEntityStore<Persistable> mLocalDatabase;

    private static String TAG = "StripLocalDataSource";

    private Date dateLongTimeAgo;

    public StripLocalDataSource(ReactiveEntityStore<Persistable> localDatabase) {

        mLocalDatabase = localDatabase;

        dateLongTimeAgo = new Date(1000);
    }

    /* (no-Javadoc) */
    @Override
    public Maybe<StripDto> fetchStrip(@Nullable Long id) {

        if (id != null) {

            return fetchStripDao(id).map(new StripDaoToStripDto());
        } else {

            return mLocalDatabase.select(StripDaoEntity.class)
                    .from(StripDaoEntity.class)
                    .orderBy(StripDaoEntity.RELEASE_DATE.desc())
                    .limit(1)
                    .get()
                    .maybe()
                    .map(new StripDaoToStripDto());
        }
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<StripDto> fetchListStrip(
            @NonNull Integer numberOfStripPerPage,
            @NonNull Integer page) {

        if (numberOfStripPerPage == null && page == null) {
            throw new IllegalArgumentException();
        }

        Integer from, to;
        if (page != 0 && page >= 0) {
            from = (page - 1) * numberOfStripPerPage;
            to = page * numberOfStripPerPage;
        } else {
            from = 0;
            to = numberOfStripPerPage;
        }

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .orderBy(StripDaoEntity.RELEASE_DATE.desc())
                .limit(to)
                .get()
                .flowable()
                .skip(from)
                .map(new StripDaoToStripDto());
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<StripDto> fetchListStrip(@NonNull Iterable<Long> listId) {

        if (listId == null) {
            throw new IllegalArgumentException();
        }

        Iterator<Long> iteratorListId = listId.iterator();

        if (!iteratorListId.hasNext()) {
            return Flowable.empty();
        }

        List<Long> target = new ArrayList<>();
        Long id;
        while (iteratorListId.hasNext()) {
            id = iteratorListId.next();

            target.add(id);
        }

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.ID.in(target))
                .get()
                .flowable()
                .map(new StripDaoToStripDto());
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<StripDto> fetchRandomListStrip(
            Integer numberOfStripPerPage, List<Long> alreadySeenStrip) {

        if (numberOfStripPerPage == null ||
                numberOfStripPerPage <= 0 ||
                alreadySeenStrip == null) {
            throw new IllegalArgumentException();
        }

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.ID.notIn(alreadySeenStrip))
                .orderBy(new io.requery.query.function.Random())
                .limit(numberOfStripPerPage)
                .get()
                .flowable()
                .map(new StripDaoToStripDto());
    }

    /* (no-Javadoc) */
    @Override
    public Maybe<StripDaoEntity> existStrip(@NonNull Long id) {

        if (id == null) {
            return Maybe.empty();
        }

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.ID.equal(id))
                .get()
                .maybe();
    }

    /* (no-Javadoc) */
    @Override
    public boolean isFavorite(@NonNull Long id) {

        if (id == null) {
            throw new IllegalArgumentException();
        }

        return mLocalDatabase.count(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.ID.equal(id)).and(StripDaoEntity.IS_FAVORITE.equal(true))
                .get()
                .value() >= 1;
    }

    /* (no-Javadoc) */
    @Override
    public Single<StripDto> addFavorite(@NonNull StripDto mCurrentStrip) {

        if (mCurrentStrip == null) {
            throw new IllegalArgumentException();
        }

        return Single.just(mCurrentStrip)
                .map(new StripDtoToStripDao())
                .map(strip -> {
                    strip.setIsFavorite(true);
                    return strip;
                })
                .flatMap(strip -> mLocalDatabase.upsert(strip).toFlowable().firstOrError())
                .map(new StripDaoToStripDto());
    }

    /* (no-Javadoc) */
    @Override
    public Maybe<Integer> deleteFavorite(@NonNull Long id) {

        if (id == null) {
            throw new IllegalArgumentException();
        }

        return mLocalDatabase.update(StripDaoEntity.class)
                .set(StripDaoEntity.IS_FAVORITE, false)
                .where(StripDaoEntity.ID.equal(id)).and(StripDaoEntity.IS_FAVORITE.eq(true))
                .get()
                .single()
                .filter(numberRow -> numberRow != 0);
    }

    /* (no-Javadoc) */
    @Override
    public Single<StripDto> fetchNextFavoriteStrip(@NonNull Date date) {

        if (date == null) {
            throw new IllegalArgumentException();
        }

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.RELEASE_DATE.between(date, new Date())).and(
                        StripDaoEntity.IS_FAVORITE.eq(true)).and(
                        StripDaoEntity.RELEASE_DATE.ne(date))
                .orderBy(StripDaoEntity.RELEASE_DATE.asc())
                .limit(1)
                .get()
                .flowable()
                .singleOrError()
                .map(new StripDaoToStripDto());
    }

    /* (no-Javadoc) */
    @Override
    public Single<StripDto> fetchPreviousFavoriteStrip(@NonNull Date date) {

        if (date == null) {
            throw new IllegalArgumentException();
        }

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.RELEASE_DATE.between(dateLongTimeAgo, date)).and(
                        StripDaoEntity.IS_FAVORITE.eq(true)).and(
                        StripDaoEntity.RELEASE_DATE.ne(date))
                .orderBy(StripDaoEntity.RELEASE_DATE.desc())
                .limit(1)
                .get()
                .flowable()
                .singleOrError()
                .map(new StripDaoToStripDto());
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<Iterable<StripDaoEntity>> upsertStrip(@NonNull Iterable<StripDto> strips) {

        if (strips == null) {
            throw new IllegalArgumentException();
        }

        List<StripDaoEntity> stripDao = new ListStripDtoToListStripDao().apply(strips);
        return mLocalDatabase.upsert(stripDao).toFlowable();
    }

    /* (no-Javadoc) */
    @Override
    public Single<StripDto> fetchMostRecentStrip() {
        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .orderBy(StripDaoEntity.RELEASE_DATE.desc())
                .limit(1)
                .get()
                .flowable()
                .firstOrError()
                .map(new StripDaoToStripDto());
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<StripDto> fetchFavoriteStrip() {

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.IS_FAVORITE.equal(true))
                .get()
                .flowable()
                .map(new StripDaoToStripDto());
    }

    /* (no-Javadoc) */
    @Override
    public Maybe<StripDto> fetchOlderStrip() {

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.RELEASE_DATE.in(mLocalDatabase.select(StripDaoEntity.RELEASE_DATE.min())))
                .get()
                .maybe()
                .map(new StripDaoToStripDto());
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<StripDto> fetchNumberOfStrip(Date from, Date to) {
         return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.RELEASE_DATE.between(from, to))
                .get()
                .flowable()
                .map(new StripDaoToStripDto());
    }

    /* (no-Javadoc) */
    @Override
    public Single<Integer> scheduleStripForDownload(List<StripDto> param) {

        return Single
                .just(param)
                .flatMap(strips -> {

                    List<Long> ids = new ArrayList<>();

                    for (StripDto strip : strips){
                        ids.add(strip.getId());
                    }

                     return mLocalDatabase
                            .update(StripDaoEntity.class)
                            .set(StripDaoEntity.HAVE_TO_DOWNLOAD, true)
                            .where(StripDaoEntity.ID.in(ids))
                            .get().single();
                });
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<StripDto> fetchToDownloadImageStrip() {
        return mLocalDatabase
                .select(StripDaoEntity.class)
                //.where(StripDaoEntity.HAVE_TO_DOWNLOAD.eq(true))
                .get().flowable()
                .map(new StripDaoToStripDto());
    }

    /* (no-Javadoc) */
    @Override
    public Single<Integer> flagAsDownloadedImage(Long id) {
        return mLocalDatabase
                .update(StripDaoEntity.class)
                .set(StripDaoEntity.HAVE_TO_DOWNLOAD, false)
                .where(StripDaoEntity.ID.eq(id))
                .get().single();
    }

    private Maybe<StripDaoEntity> fetchStripDao(Long id) {

        if (id == null) {
            throw new IllegalArgumentException();
        }

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.ID.equal(id))
                .limit(1)
                .get()
                .maybe();
    }
}
