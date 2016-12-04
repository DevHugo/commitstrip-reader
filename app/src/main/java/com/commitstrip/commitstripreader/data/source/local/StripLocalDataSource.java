package com.commitstrip.commitstripreader.data.source.local;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.StripDataSource;
import com.commitstrip.commitstripreader.data.source.local.converter.ListStripDtoToListStripDao;
import com.commitstrip.commitstripreader.data.source.local.converter.StripDaoToStripDto;
import com.commitstrip.commitstripreader.data.source.local.converter.StripDtoToStripDao;
import com.commitstrip.commitstripreader.data.source.local.exception.NotSynchronizedException;
import com.commitstrip.commitstripreader.dto.StripDto;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

@Singleton
public class StripLocalDataSource implements StripDataSource.LocalDataSource {

    private final ReactiveEntityStore<Persistable> mLocalDatabase;
    private final SharedPreferences mSharedPreferences;

    private static String TAG = "StripLocalDataSource";

    private Date dateLongTimeAgo;

    @Inject
    public StripLocalDataSource(ReactiveEntityStore<Persistable> localDatabase,
                                SharedPreferences sharedPreferences) {

        mSharedPreferences = sharedPreferences;
        mLocalDatabase = localDatabase;

        dateLongTimeAgo = new Date(1*1000);
    }

    /**
     * Fetch a strip with the id specified in parameter in local database.
     *
     * Throw a NotSynchronizedException, if the local database have not been synchronized with the org.commitstrip.commistripreader.data.source.remote yet.
     *
     * @param id null id will return the most recent strip
     */
    @Override
    public Single<StripDto> fetchStrip(Long id) {

        if (mSharedPreferences.getBoolean(Configuration.SHAREDPREFERENCES_KEY_DATABASE_SYNC_OK, false)) {
            if (id != null) {
                return fetchStripDao(id)
                        .map(new StripDaoToStripDto());
            }
            else {
                return mLocalDatabase.select(StripDaoEntity.class)
                        .from(StripDaoEntity.class)
                        .orderBy(StripDaoEntity.DATE.desc())
                        .limit(1)
                        .get()
                        .observable()
                        .map(new StripDaoToStripDto())
                        .firstOrError();
            }
        }
        else {
            return Single.error(new NotSynchronizedException());
        }
    }

    /**
     * Return true, if the strip exist in local database, false otherwise.
     *
     * Be warned, it doesn't mean that the strip will not exist in the backend.
     * If the local database is not synchronized with the backend, the method will NOT return a NotSynchronizedException
     *
     * @param id
     * @return
     */
    @Override
    public boolean existStrip(@NonNull Long id) {

        if (id == null)
            throw new IllegalArgumentException();

        return mLocalDatabase.count(StripDaoEntity.class)
            .from(StripDaoEntity.class)
            .where(StripDaoEntity.ID.equal(id))
            .get()
            .value() >= 1;
    }

    @Override
    public boolean isFavorite(@NonNull Long id) {

        if (id == null)
            throw new IllegalArgumentException();

        return mLocalDatabase.count(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.ID.equal(id)).and(StripDaoEntity.IS_FAVORITE.equal(true))
                .get()
                .value() >= 1;
    }

    @Override
    public Single<StripDto> addFavorite(@NonNull StripDto mCurrentStrip) {

        if (mCurrentStrip == null)
            throw new IllegalArgumentException();

        return Single.just(mCurrentStrip)
                .map(new StripDtoToStripDao())
                .map(strip -> {strip.setIsFavorite(true); return strip; })
                .flatMap(strip -> mLocalDatabase.upsert(strip).toFlowable().firstOrError())
                .map(new StripDaoToStripDto());
    }


    @Override
    public Single<StripDto> deleteFavorite(@NonNull StripDto mCurrentStrip) {

        if (mCurrentStrip == null)
            throw new IllegalArgumentException();

        return mLocalDatabase.update(StripDaoEntity.class)
                .set(StripDaoEntity.IS_FAVORITE, false)
                .where(StripDaoEntity.ID.equal(mCurrentStrip.getId())).and(StripDaoEntity.IS_FAVORITE.eq(true))
                .get()
                .single()
                .flatMap(row -> {
                    if (row >= 1) {
                        return Single.just(mCurrentStrip);
                    }
                    else {
                        return Single.error(new NoSuchElementException());
                    }
                });
    }

    @Override
    public boolean cacheForStripShouldBeClean(Long id) {
        return !isIn10MostRecentStrip(id) &&
               !isInNext10Strip (mSharedPreferences.getLong(Configuration.SHAREDPREFERENCES_KEY_LAST_STRIP_READ, 0), id) &&
               !isFavorite(id);
    }

    private boolean isInNext10Strip(long startId, long idToBeFound) {
        Integer i=0;
        StripDaoEntity strip;
        Long currentId = startId;
        boolean found = false;

        do {
            strip = mLocalDatabase.select(StripDaoEntity.class)
                    .from(StripDaoEntity.class)
                    .where(StripDaoEntity.ID.equal(currentId))
                    .get().first();

           if (strip.getId() == idToBeFound) {
               found = true;
           }

           currentId = strip.getNext();

        } while (i<=10 && !found);

        return found;
    }

    private boolean isIn10MostRecentStrip(Long id) {
          return mLocalDatabase.count(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.ID.equal(id))
                .orderBy(StripDaoEntity.DATE.desc())
                .limit(10)
                .get().value() > 0;
    }

    @Override
    public Single<StripDto> fetchNextFavoriteStrip(@NonNull Date date) {

        if (date == null)
            throw new IllegalArgumentException();

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.DATE.between(date, new Date())).and(StripDaoEntity.IS_FAVORITE.eq(true)).and(StripDaoEntity.DATE.ne(date))
                .orderBy(StripDaoEntity.DATE.asc())
                .limit(1)
                .get()
                .flowable()
                .singleOrError()
                .map(new StripDaoToStripDto());
    }

    @Override
    public Single<StripDto> fetchPreviousFavoriteStrip(@NonNull Date date) {

        if (date == null)
            throw new IllegalArgumentException();

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.DATE.between(dateLongTimeAgo, date)).and(StripDaoEntity.IS_FAVORITE.eq(true)).and(StripDaoEntity.DATE.ne(date))
                .orderBy(StripDaoEntity.DATE.desc())
                .limit(1)
                .get()
                .flowable()
                .singleOrError()
                .map(new StripDaoToStripDto());
    }

    @Override
    public Single<StripDaoEntity> saveStrip(@NonNull StripDto strip) {

        StripDaoEntity stripDao = new StripDtoToStripDao().apply(strip);
        return mLocalDatabase.insert(stripDao);
    }

    @Override
    public Flowable<Iterable<StripDaoEntity>> saveStrip(@NonNull List<StripDto> strips) {

        if (strips == null)
            throw new IllegalArgumentException();

        List<StripDaoEntity> stripDao = new ListStripDtoToListStripDao().apply(strips);
        return mLocalDatabase.insert(stripDao).toFlowable();
    }

    @Override
    public Single<StripDto> fetchMostRecentStrip() {
        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .orderBy(StripDaoEntity.DATE.desc())
                .limit(1)
                .get()
                .flowable()
                .firstOrError()
                .map(new StripDaoToStripDto());
    }

    @Override
    public Flowable<StripDto> fetchFavoriteStrip() {

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.IS_FAVORITE.equal(true))
                .get()
                .flowable()
                .map(new StripDaoToStripDto());
    }

    private Single<StripDaoEntity> fetchStripDao (Long id) {

        if (id == null)
            throw new IllegalArgumentException();

        return mLocalDatabase.select(StripDaoEntity.class)
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.ID.equal(id))
                .limit(1)
                .get()
                .observable().firstOrError();
    }
}
