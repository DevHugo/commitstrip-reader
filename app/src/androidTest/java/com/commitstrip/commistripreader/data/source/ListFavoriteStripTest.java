package com.commitstrip.commistripreader.data.source;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.commitstrip.commistripreader.util.RecyclerViewUtil;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.listfavorite.ListFavoriteActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListFavoriteStripTest {

    private ReactiveEntityStore<Persistable> mDatabase;
    private StripDaoEntity mStrip;

    @Rule
    public ActivityTestRule oneStripActivityRule =
            new ActivityTestRule<>(ListFavoriteActivity.class, false, false);

    @Before
    public void setUp() {

        Context context = InstrumentationRegistry.getTargetContext();

        mDatabase = ((MyApp) context.getApplicationContext())
                .getLocalDatabaseComponent()
                .provideLocalDatabase();

        // Add a strip as a favorite
        mStrip = SampleStrip.generateSampleDao();
        mStrip.setIsFavorite(true);

        mDatabase.upsert(mStrip).blockingGet();
    }

    @Test
    public void listFavoriteStrip_ListFavoriteActivity() throws Exception {

        Context context = InstrumentationRegistry.getTargetContext();

        Intent intent = new Intent(context, ListFavoriteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);

        // Check that the title is there.
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .check(ViewAssertions.matches(
                        RecyclerViewUtil.atPosition(0,
                                ViewMatchers.hasDescendant(
                                        ViewMatchers.withText(mStrip.getTitle())
                                )
                        )
                ));
    }

    @After
    public void turnDown() {

        // Delete new favorite in the database
        mDatabase.delete(StripDaoEntity.class).where(StripDaoEntity.ID.eq(mStrip.getId())).get();
    }

}
