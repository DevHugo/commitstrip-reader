package com.commitstrip.commistripreader.data.source;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.data.source.remote.StripRemoteDataSource;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.strip.StripActivity;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NoConnectionTest {

    @Rule
    public ActivityTestRule<StripActivity> mainActivityTestRule
            = new ActivityTestRule<>(StripActivity.class);

    private StripDaoEntity mStrip;
    private ReactiveEntityStore<Persistable> mDatabase;

    @Before
    public void setUp() {
        StripRemoteDataSource.AIRPLANE_MODE = true;

        mStrip = SampleStrip.generateSampleDao();

        Context context = InstrumentationRegistry.getTargetContext();

        mDatabase = ((MyApp) context.getApplicationContext())
                .getLocalDatabaseComponent()
                .provideLocalDatabase();

        mDatabase.upsert(mStrip).blockingGet();
    }

    @Test
    public void noInternetConnectionShouldDisplayTitle() {

        Espresso.onView(ViewMatchers.withId(R.id.title))
                .check(matches(ViewMatchers.withText(mStrip.getTitle())));
    }

    @After
    public void turnDown() {
        mDatabase.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(mStrip.getId())).get();
    }

}
