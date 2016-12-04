package com.commitstrip.commitstripreader.displayfavorite;

import android.os.Bundle;

import com.commitstrip.commitstripreader.BaseActivity;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.AbstractDisplayStripFragment;
import com.commitstrip.commitstripreader.data.source.StripRepositorySingleton;
import com.commitstrip.commitstripreader.strip.StripPresenter;
import com.commitstrip.commitstripreader.util.ActivityUtils;

import javax.inject.Inject;

/**
 * Display the favorite list strip of the current user.
 */
public class DisplayFavoriteStripActivity extends BaseActivity {

    public static String ARGUMENT_STRIP_ID = AbstractDisplayStripFragment.ARGUMENT_STRIP_ID;

    @Inject
    DisplayFavoriteStripPresenter mStripPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the fragment
        DisplayFavoriteStripFragment stripFragment = (DisplayFavoriteStripFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (stripFragment == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null && extras.containsKey(AbstractDisplayStripFragment.ARGUMENT_STRIP_ID))
                stripFragment = DisplayFavoriteStripFragment.newInstance(extras.getLong(AbstractDisplayStripFragment.ARGUMENT_STRIP_ID));
            else
                stripFragment = DisplayFavoriteStripFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), stripFragment, R.id.contentFrame);
        }

        // Create the presenter
        DaggerDisplayFavoriteStripComponent.builder()
                .stripRepositoryComponent(StripRepositorySingleton.getInstance(getApplicationContext()).getStripRepositoryComponent())
                .displayFavoriteStripPresenterModule(new DisplayFavoriteStripPresenterModule(stripFragment)).build()
                .inject(this);
    }
}
