package com.commitstrip.commitstripreader.displayfavorite;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;

import com.commitstrip.commitstripreader.BaseActivity;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripFragment;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripPresenterModule;
import com.commitstrip.commitstripreader.common.displaystrip.FetchStripType;
import com.commitstrip.commitstripreader.util.ActivityUtils;

import javax.inject.Inject;

/**
 * Display the favorite list strip of the current user.
 */
public class DisplayFavoriteStripActivity extends BaseActivity {

    private static String ARGUMENT_STRIP_ID = "STRIP_ID";
    private static String ARGUMENT_FETCH_TYPE = "FETCH_TYPE";

    @Inject DisplayFavoriteStripPresenter mStripPresenter;

    private DisplayStripFragment mStripFragment;

    /**
     * Construct a bundle to help to create the intent.
     *
     * @param id strip id
     * @return android bundle
     */
    public static Bundle newInstance(Long id) {

        Bundle bundle = new Bundle();
            bundle.putSerializable(ARGUMENT_FETCH_TYPE, FetchStripType.ID);
            bundle.putLong(ARGUMENT_STRIP_ID, id);

        return bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the fragment
        mStripFragment = (DisplayStripFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        Long id = -1L;
        if (mStripFragment == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null
                    && extras.containsKey(ARGUMENT_STRIP_ID)
                    && extras.containsKey(ARGUMENT_FETCH_TYPE)
                    && extras.getSerializable(ARGUMENT_FETCH_TYPE).equals(FetchStripType.ID)) {

                id = extras.getLong(ARGUMENT_STRIP_ID);

            } else {
                throw new IllegalArgumentException("Illegal argument exception, you must initialize "
                        + "activity with strip id and fetch type");
            }

            mStripFragment = DisplayStripFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mStripFragment,
                    R.id.contentFrame);
        }

        // Create the presenter
        DaggerDisplayFavoriteStripComponent.builder()
                .dataSourceComponent(((MyApp) getApplication()).getDataSourceComponent())
                .displayStripPresenterModule(
                        new DisplayStripPresenterModule(FetchStripType.ID, id, mStripFragment))
                .build()
                .inject(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            if (mStripFragment != null) {
                return mStripFragment.onKeyVolumeDown();
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

            if (mStripFragment != null) {
                return mStripFragment.onKeyVolumeUp();
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
