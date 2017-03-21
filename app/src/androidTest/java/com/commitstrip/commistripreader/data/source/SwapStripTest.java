package com.commitstrip.commistripreader.data.source;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.commitstrip.commitstripreader.listfavorite.ListFavoriteActivity;
import com.commitstrip.commitstripreader.strip.StripActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SwapStripTest {

    @Rule
    public ActivityTestRule<StripActivity> mainActivityTestRule =
            new ActivityTestRule<StripActivity>(StripActivity.class, true, false){

                @Override
                protected void afterActivityLaunched() {
                    super.afterActivityLaunched();

                    Context context = InstrumentationRegistry.getTargetContext();

                    // Add a non favorite strip in datatabase
                    mDatabase = ((MyApp) context.getApplicationContext())
                            .getLocalDatabaseComponent()
                            .provideLocalDatabase();

                    mStrip = SampleStrip.generateSampleDao();
                    mStrip.setIsFavorite(false);

                    mDatabase.upsert(mStrip).blockingGet();
                }
            };
    private ReactiveEntityStore<Persistable> mDatabase;
    private StripDaoEntity mStrip;
    
    @Test
    public void swapStrip_StripActivity() {

        Context context = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(context, StripActivity.class);
            intent.putExtras (StripActivity.newInstance(Long.valueOf(SampleStrip.STRIP_ID_ONE)));
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);

        // Check that the title is there.
        Espresso.onView(ViewMatchers.withId(R.id.title)).check(
                ViewAssertions.matches(ViewMatchers.withText(SampleStrip.STRIP_ID_ONE_TITLE)));

        // Check that we can use swipe left, the goal is just to test that we have no error
        Espresso.onView(ViewMatchers.withId(R.id.strip)).perform(
                ViewActions.swipeRight());

        // Check that the title is always there.
        Espresso.onView(ViewMatchers.withId(R.id.title)).check(
                ViewAssertions.matches(ViewMatchers.withText(SampleStrip.STRIP_ID_ONE_TITLE)));

        // Check that we can use swipe left
        Espresso.onView(ViewMatchers.withId(R.id.strip)).perform(
                ViewActions.swipeLeft());

        // Expect a new strip to be displayed
        Espresso.onView(ViewMatchers.withId(R.id.title)).check(
                ViewAssertions.matches(ViewMatchers.withText(SampleStrip.STRIP_ID_TWO_TITLE)));
    }

    @After
    public void turnDown() {
        mDatabase.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(mStrip.getId())).get();
    }
}
