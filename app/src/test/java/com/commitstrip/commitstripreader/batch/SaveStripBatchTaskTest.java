package com.commitstrip.commitstripreader.batch;

import android.content.Context;

import com.commitstrip.commitstripreader.BuildConfig;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.util.RobolectricClearDatabaseRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * Unit tests for the implementation of {@link SaveStripBatchTask}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = MyApp.class)
public class SaveStripBatchTaskTest {

    @Rule
    public RobolectricClearDatabaseRule mRobolectricRepositoryRule =
            new RobolectricClearDatabaseRule();

    @Test
    public void shouldSaveNoExistingStripInBatch() throws Exception {

        StripDto strip = SampleStrip.generateSampleDto();

        TestSubscriber testSubscriber = new TestSubscriber<>();

        Flowable<StripDto> strips = Flowable.just(strip);

        SaveStripBatchTask underTest = new SaveStripBatchTask(getStripRepository());
        underTest.execute(strips).blockingSubscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();

        testSubscriber.assertValueCount(1);
        testSubscriber.assertValue(strip);

        ReactiveEntityStore<Persistable> database = mRobolectricRepositoryRule.getLocalDatabase();
        Integer numberRow = database.count()
                .from(StripDaoEntity.class)
                .where(StripDaoEntity.ID.eq(1L))
                .get().call();

        Assert.assertTrue ("Should have one strip saved on database : ", numberRow == 1);
    }

    @Test
    public void saveInBatchExistingStripShouldReturnZeroStrip() throws Exception {

        StripDaoEntity strip = SampleStrip.generateSampleDao();
        mRobolectricRepositoryRule.getLocalDatabase().insert(strip).blockingGet();

        TestSubscriber testSubscriber = new TestSubscriber<>();

        Flowable<StripDto> strips = Flowable.just(SampleStrip.generateSampleDto());

        SaveStripBatchTask underTest = new SaveStripBatchTask(getStripRepository());
        underTest.execute(strips).blockingSubscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertComplete();

        ReactiveEntityStore<Persistable> database = mRobolectricRepositoryRule.getLocalDatabase();
        Integer numberRow = database
                .count()
                .from(StripDaoEntity.class)
                .get()
                .call();

        Assert.assertTrue ("Should have one strip saved on database : ", numberRow == 1);
    }

    private StripRepository getStripRepository () {
        Context context = RuntimeEnvironment.application;

        return ((MyApp) context.getApplicationContext())
                .getDataSourceComponent()
                .getStripRepository();
    }
}
