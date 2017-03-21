package com.commitstrip.commitstripreader.data.source.local;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import android.content.SharedPreferences;

import com.commitstrip.commitstripreader.BuildConfig;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.data.source.local.converter.StripDaoToStripDto;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.util.RobolectricDatabaseRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.sql.StatementExecutionException;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = MyApp.class)
public class StripLocalDataSourceTest {

    private static StripLocalDataSource mStripLocalDataSource;
    private static StripDaoEntity stripDao;

    @Rule
    public RobolectricDatabaseRule mDataSourceRule =
            new RobolectricDatabaseRule();

    private ReactiveEntityStore<Persistable> mSingleEntityStore;

    @Before
    public void setUp() {

        // Populate class under test
        mStripLocalDataSource = new StripLocalDataSource(mDataSourceRule.getLocalDatabase());

        // Insert some strip in local database
        stripDao = SampleStrip.generateSampleDao();

        mSingleEntityStore =  mDataSourceRule.getLocalDatabase();

        if (mStripLocalDataSource.existStrip(stripDao.getId()).blockingGet() == null) {
           mSingleEntityStore.insert(stripDao).blockingGet();
        }
    }

    @Test
    public void fetchStripDaoWithCorrectIdShouldReturnOneResult () {
        TestObserver testObserver = new TestObserver();

        mStripLocalDataSource.fetchStrip(stripDao.getId()).subscribe(testObserver);

        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();
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

        testSubscriber.assertNoValues();
        testSubscriber.assertTerminated();
    }

    @Test
    public void existStripWithExistStripShouldReturnTrue () {
        assertEquals(stripDao, mStripLocalDataSource.existStrip(1L).blockingGet());
    }

    @Test
    public void existStripWithNonExistStripShouldReturnFalse () {
        assertEquals(null, mStripLocalDataSource.existStrip(1000000L).blockingGet());
    }

    @Test
    public void isFavoriteWithExistStripShouldReturnTrue () {
        assertEquals(true, mStripLocalDataSource.isFavorite(1L));
    }

    @Test
    public void isFavoriteWithNonExistStripShouldReturnFalse () {
        assertEquals(null, mStripLocalDataSource.existStrip(10000000L).blockingGet());
    }

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

        mStripLocalDataSource.deleteFavorite(stripDto.getId()).subscribe(testSubscriber);

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

        TestObserver testSubscriber = new TestObserver<>();

        mStripLocalDataSource.deleteFavorite(29071991L).subscribe(testSubscriber);

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

        Date beforeStrip = new Date(stripDao.getReleaseDate().getTime()-1000);

        mStripLocalDataSource.fetchPreviousFavoriteStrip(beforeStrip).subscribe(testSubscriber);

        testSubscriber.assertError(NoSuchElementException.class);
        testSubscriber.assertNoValues();
    }

    @Test
    public void saveStripWithAnNonExistingStripShouldReturnStrip() {

        TestSubscriber testSubscriber = new TestSubscriber<>();

        StripDto stripDto = new StripDto();
            stripDto.setId(2L);

        List<StripDto> strips = new ArrayList<>();
        strips.add(stripDto);

        mStripLocalDataSource.upsertStrip(strips).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();

        assertTrue(mSingleEntityStore.count(StripDaoEntity.class)
                .where(StripDaoEntity.ID.eq(stripDto.getId()))
                .get().value() >= 1);

        mSingleEntityStore.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(stripDto.getId())).get().value();
    }

    @Test
    public void saveListStripWithAnNonExistingStripShouldReturnStrip() {
        TestSubscriber testSubscriber = new TestSubscriber();

        StripDto stripDto = new StripDto();
        stripDto.setId(2L);

        List<StripDto> strips = new ArrayList<>();
        strips.add(stripDto);

        mStripLocalDataSource.upsertStrip(strips).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertComplete();

        assertTrue(mSingleEntityStore.count(StripDaoEntity.class)
                .where(StripDaoEntity.ID.eq(stripDto.getId()))
                .get().value() >= 1);

        mSingleEntityStore.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(stripDto.getId())).get().value();
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

    @After
    public void tearDownAfterClass() {
        mSingleEntityStore.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(stripDao.getId())).get().value();
    }
}
