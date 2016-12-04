package com.commitstrip.commitstripreader.data.source.local;

import android.content.SharedPreferences;

import com.commitstrip.commitstripreader.data.source.local.converter.StripDaoToStripDto;
import com.commitstrip.commitstripreader.data.source.local.exception.NotSynchronizedException;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.util.H2LocalDatabase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Date;
import java.util.NoSuchElementException;

import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.sql.StatementExecutionException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class StripLocalDataSourceTest {

    private String TAG = "StripLocalDataSource";

    private static StripLocalDataSource mStripLocalDataSource;
    private static ReactiveEntityStore<Persistable> mSingleEntityStore;
    private static SharedPreferences mSharedPreferences;
    private static StripDaoEntity stripDao;

    @BeforeClass
    public static void setUpBeforeClass() {

        // Connect to local database
        mSingleEntityStore = H2LocalDatabase.getConnection();
        
        // Mock SharedPreferences android class
        mSharedPreferences = mock(SharedPreferences.class);
        when(mSharedPreferences.getBoolean(com.commitstrip.commitstripreader.configuration.Configuration.SHAREDPREFERENCES_KEY_DATABASE_SYNC_OK, false)).thenReturn(true);

        // Populate class under test
        mStripLocalDataSource = new StripLocalDataSource(mSingleEntityStore, mSharedPreferences);

        // Insert some strip in local database
        stripDao = SampleStrip.generateSampleDao();

        if (!mStripLocalDataSource.existStrip(stripDao.getId()))
            mSingleEntityStore.insert(stripDao).blockingGet();
    }

    @Test
    public void fetchStripDaoWithCorrectIdShouldReturnOneResult () {
        TestObserver testSubscriber = new TestObserver();

        mStripLocalDataSource.fetchStrip(stripDao.getId()).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();
    }

    @Test
    public void fetchStripDaoWithNullShouldReturnMostRecentStrip () {
        TestObserver testSubscriber = new TestObserver<>();

        mStripLocalDataSource.fetchStrip(null).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();
    }

    @Test
    public void fetchStripDaoWithNonCorrectIdShouldReturnNoValue() {
        TestObserver testSubscriber = new TestObserver<>();

        mStripLocalDataSource.fetchStrip(100000L).subscribe(testSubscriber);

        testSubscriber.assertError(NoSuchElementException.class);
        testSubscriber.assertNoValues();
        testSubscriber.assertTerminated();
    }

    @Test
    public void fetchStripDaoWithNonSynchronizedDatabaseShouldReturnOnError () {
        TestObserver testSubscriber = new TestObserver<>();

        when(mSharedPreferences.getBoolean(com.commitstrip.commitstripreader.configuration.Configuration.SHAREDPREFERENCES_KEY_DATABASE_SYNC_OK, false)).thenReturn(false);

        mStripLocalDataSource.fetchStrip(100000L).subscribe(testSubscriber);

        when(mSharedPreferences.getBoolean(com.commitstrip.commitstripreader.configuration.Configuration.SHAREDPREFERENCES_KEY_DATABASE_SYNC_OK, false)).thenReturn(true);

        testSubscriber.assertError(NotSynchronizedException.class);
        testSubscriber.assertTerminated();
    }

    @Test
    public void existStripWithExistStripShouldReturnTrue () {
        assertEquals(true, mStripLocalDataSource.existStrip(1L));
    }

    @Test
    public void existStripWithNonExistStripShouldReturnFalse () {
        assertEquals(false, mStripLocalDataSource.existStrip(1000000L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void existStripWithNullShouldThrowIllegalException () {
        mStripLocalDataSource.existStrip(null);
    }

    @Test
    public void isFavoriteWithExistStripShouldReturnTrue () {
        assertEquals(true, mStripLocalDataSource.isFavorite(1L));
    }

    @Test
    public void isFavoriteWithNonExistStripShouldReturnFalse () {
        assertEquals(false, mStripLocalDataSource.existStrip(10000000L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isFavoriteWithNullShouldThrowIllegalException () {
        mStripLocalDataSource.existStrip(null);
    }

    @Test
    public void addFavoriteWithAnExistingStripShouldUpdateFieldInLocalDatabase() {
        // Update the strip for being a non favorite strip
        addOrDeleteFavorite (stripDao, false);

        // Call addFavorite method
        TestObserver testSubscriber = new TestObserver<>();

        StripDto stripDto = new StripDaoToStripDto().apply(stripDao);

        mStripLocalDataSource.addFavorite(stripDto).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();

        // Test if we correctly added the strip
        assertEquals(true, mSingleEntityStore.select(StripDaoEntity.class)
                .where(StripDaoEntity.ID.eq(stripDao.getId()))
                .get().first().isIsFavorite());

        addOrDeleteFavorite (stripDao, true);
    }

    @Test
    public void addFavoriteWithAnNonExistingStripShouldCreateInLocalDatabase() {
        StripDto sampleNonExistingStrip = new StripDto();
            sampleNonExistingStrip.setId(29071991L);

        // Call addFavorite method
        TestObserver testSubscriber = new TestObserver<>();

        mStripLocalDataSource.addFavorite(sampleNonExistingStrip).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();

        // Test if we correctly added the strip
        assertEquals(true, mSingleEntityStore.select(StripDaoEntity.class)
                .where(StripDaoEntity.ID.eq(sampleNonExistingStrip.getId()))
                .get().first().isIsFavorite());

        // Delete the strip, we don't need it anymore
        mSingleEntityStore.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(sampleNonExistingStrip.getId())).get().value();
    }

    @Test
    public void deleteFavoriteWithAnExistingStripShouldDeleteInLocalDatabase() {
        TestObserver testSubscriber = new TestObserver<>();

        StripDto stripDto = new StripDaoToStripDto().apply(stripDao);

        mStripLocalDataSource.deleteFavorite(stripDto).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();

        // Test if we correctly deleted the strip
        assertEquals(false, mSingleEntityStore.select(StripDaoEntity.class)
                .where(StripDaoEntity.ID.eq(stripDto.getId()))
                .get().first().isIsFavorite());

        addOrDeleteFavorite (stripDao, true);
    }

    @Test
    public void deleteFavoriteWithAnNonExistingStripShouldReturnErrorDatabase() {
        StripDto sampleNonExistingStrip = new StripDto();
            sampleNonExistingStrip.setId(29071991L);

        TestObserver testSubscriber = new TestObserver<>();

        mStripLocalDataSource.deleteFavorite(sampleNonExistingStrip).subscribe(testSubscriber);

        testSubscriber.assertError(NoSuchElementException.class);
        testSubscriber.assertNoValues();
    }

    @Test
    public void fetchNextFavoriteStripWithAnExistingStripShouldReturnStrip() {
        TestObserver testSubscriber = new TestObserver<>();

        Date beforeStrip = new Date(1);

        mStripLocalDataSource.fetchNextFavoriteStrip(beforeStrip).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();
    }

    @Test
    public void fetchNextFavoriteStripWithAnNonExistingStripShouldReturnError() {
        TestObserver testSubscriber = new TestObserver<>();

        mStripLocalDataSource.fetchNextFavoriteStrip(new Date()).subscribe(testSubscriber);

        testSubscriber.assertError(NoSuchElementException.class);
        testSubscriber.assertNoValues();
    }

    @Test
    public void fetchPreviousFavoriteStripWithAnExistingStripShouldReturnStrip() {
        TestObserver testSubscriber = new TestObserver<>();

        mStripLocalDataSource.fetchPreviousFavoriteStrip(new Date()).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();
    }

    @Test
    public void fetchPreviousFavoriteStripWithAnNonExistingStripShouldReturnError() {
        TestObserver testSubscriber = new TestObserver<>();

        Date beforeStrip = new Date(stripDao.getDate().getTime()-1000);

        mStripLocalDataSource.fetchPreviousFavoriteStrip(beforeStrip).subscribe(testSubscriber);

        testSubscriber.assertError(NoSuchElementException.class);
        testSubscriber.assertNoValues();
    }

    @Test
    public void saveStripWithAnNonExistingStripShouldReturnStrip() {

        TestObserver testSubscriber = new TestObserver<>();

        StripDto stripDto = new StripDto();
            stripDto.setId(2L);

        mStripLocalDataSource.saveStrip(stripDto).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();

        assertTrue(mSingleEntityStore.count(StripDaoEntity.class)
                .where(StripDaoEntity.ID.eq(stripDto.getId()))
                .get().value() >= 1);

        mSingleEntityStore.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(stripDto.getId())).get().value();
    }

    @Test
    public void saveStripWithAnExistingStripShouldReturnError() {
        TestObserver testSubscriber = new TestObserver<>();

        StripDto stripDto = new StripDaoToStripDto().apply(stripDao);

        mStripLocalDataSource.saveStrip(stripDto).subscribe(testSubscriber);

        testSubscriber.assertError(StatementExecutionException.class);
        testSubscriber.assertValueCount(0);
    }

    @Test
    public void saveListStripWithAnNonExistingStripShouldReturnStrip() {
        TestSubscriber testSubscriber = new TestSubscriber();

        StripDto stripDto = new StripDto();
        stripDto.setId(2L);

        mStripLocalDataSource.saveStrip(Arrays.asList(stripDto)).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();

        assertTrue(mSingleEntityStore.count(StripDaoEntity.class)
                .where(StripDaoEntity.ID.eq(stripDto.getId()))
                .get().value() >= 1);

        mSingleEntityStore.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(stripDto.getId())).get().value();
    }

    @Test
    public void saveListStripWithAnExistingStripShouldReturnError() {
        TestObserver testSubscriber = new TestObserver<>();

        StripDto stripDto = new StripDaoToStripDto().apply(stripDao);

        mStripLocalDataSource.saveStrip(stripDto).subscribe(testSubscriber);

        testSubscriber.assertError(StatementExecutionException.class);
        testSubscriber.assertValueCount(0);
    }

    @Test
    public void fetchMostRecentStripWithAnExistingStripShouldReturnStrip() {

        TestObserver testSubscriber = new TestObserver<>();

        mStripLocalDataSource.fetchMostRecentStrip().subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();
    }

    @Test
    public void fetchFavoriteStripWithNoExistingStripShouldReturnOnComplete() {

        mSingleEntityStore.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(stripDao.getId())).get().value();

        TestSubscriber testSubscriber = new TestSubscriber();

        mStripLocalDataSource.fetchFavoriteStrip().subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(0);
        testSubscriber.assertComplete();

        mSingleEntityStore.insert(stripDao).blockingGet();
    }

    @Test
    public void fetchFavoriteStripWithAnExistingStripShouldReturnStrip() {

        TestObserver testSubscriber = new TestObserver();

        mStripLocalDataSource.fetchMostRecentStrip().subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();
    }

    @Test
    public void fetchMostRecentStripWithNoExistingStripShouldReturnOnComplete() {

        mSingleEntityStore.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(stripDao.getId())).get().value();

        TestObserver testSubscriber = new TestObserver<>();

        mStripLocalDataSource.fetchMostRecentStrip().subscribe(testSubscriber);

        testSubscriber.assertError(NoSuchElementException.class);
        testSubscriber.assertNoValues();

        mSingleEntityStore.insert(stripDao).blockingGet();
    }

    private void addOrDeleteFavorite(StripDaoEntity stripDto, boolean shouldBeFavorite) {
        mSingleEntityStore.update(StripDaoEntity.class)
                .set(StripDaoEntity.IS_FAVORITE, shouldBeFavorite)
                .where(StripDaoEntity.ID.eq(stripDto.getId()))
                .get().value();
    }

    @AfterClass
    public static void tearDownAfterClass() {
        mSingleEntityStore.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(stripDao.getId())).get().value();
    }
}
