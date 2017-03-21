package com.commitstrip.commistripreader.data.source;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.liststrip.ListStripActivity;
import com.commitstrip.commitstripreader.strip.StripActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * Try to add a strip in favorite from the strip list or strip detail.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddFavoriteStripTest {

    @Rule
    public ActivityTestRule<StripActivity> mStripActivityActivityTestRule =
            new ActivityTestRule<>(StripActivity.class, true, false);

    @Rule
    public ActivityTestRule<ListStripActivity> mListStripActivityActivityTestRule =
            new ActivityTestRule<>(ListStripActivity.class, true, false);

    private ReactiveEntityStore<Persistable> mDatabase;
    private StripDaoEntity mStrip;

    @Before
    public void setUp() {

        // Add a non favorite strip in datatabase
        Context context = InstrumentationRegistry.getTargetContext();

        MyApp myApp = ((MyApp) context.getApplicationContext());
        mDatabase = myApp.getLocalDatabaseComponent().provideLocalDatabase();

        mStrip = SampleStrip.generateSampleDao();
        mStrip.setIsFavorite(false);

        mDatabase.upsert(mStrip).blockingGet();
    }

    @Test
    public void addFavoriteStrip_StripActivity() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(context, StripActivity.class);

        mStripActivityActivityTestRule.launchActivity(intent);

        addAndCheckFavorite(R.id.fav_button);
    }

    @Test
    public void addFavoriteStrip_ListStripActivity() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(context, ListStripActivity.class);

        mListStripActivityActivityTestRule.launchActivity(intent);

        addAndCheckFavorite(R.id.fav);
    }

    @After
    public void turnDown() {

        // Delete new favorite in the database
        mDatabase.delete(StripDaoEntity.class).where(
                StripDaoEntity.ID.eq(mStrip.getId())).get();
    }

    private void addAndCheckFavorite(int idFavButton) throws Exception {

        // Check that the title is there.
        Espresso.onView(ViewMatchers.withId(R.id.title)).check(
                ViewAssertions.matches(ViewMatchers.withText(mStrip.getTitle()))
        );

        // Add in favorite
        Espresso.onView(ViewMatchers.withId(idFavButton)).perform(ViewActions.click());

        // Check if we correctly register the strip in the database
        Integer numberRow =
                mDatabase
                        .count()
                        .from(StripDaoEntity.class)
                        .where(StripDaoEntity.ID.eq(mStrip.getId()))
                        .and(StripDaoEntity.IS_FAVORITE.eq(true))
                        .get().call();

        if (numberRow != 1) {
            throw new AssertionError(
                    "[AddFavoriteStripTest] Strips have not been added in favorite");
        }
    }
}
