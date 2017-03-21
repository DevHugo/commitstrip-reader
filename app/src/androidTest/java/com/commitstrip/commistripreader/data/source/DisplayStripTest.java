package com.commitstrip.commistripreader.data.source;

import static android.support.test.espresso.assertion.ViewAssertions.matches;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.strip.StripActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DisplayStripTest {

    @Rule
    public ActivityTestRule<StripActivity> mMainActivityTestRule =
            new ActivityTestRule<>(StripActivity.class);

    @Test
    public void displayStrip_StripActivity() {

        // Check that the title is there.
        Espresso.onView(ViewMatchers.withId(R.id.title))
                .check(matches(ViewMatchers.withText(SampleStrip.STRIP_ID_ONE_TITLE)));

        // Check that the strip is not one of his favorite.
        Espresso.onView(ViewMatchers.withId(R.id.fav_button))
                .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

}
