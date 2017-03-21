package com.commitstrip.commitstripreader.strip;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.commitstrip.commitstripreader.BaseActivity;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripFragment;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripPresenterModule;
import com.commitstrip.commitstripreader.common.displaystrip.FetchStripType;
import com.commitstrip.commitstripreader.util.ActivityUtils;

import javax.inject.Inject;

public class StripActivity extends BaseActivity {

    @Inject
    StripPresenter mStripPresenter;

    @NonNull public static String ARGUMENT_STRIP_ID = "ARGUMENT_STRIP_ID";
    @NonNull public static String ARGUMENT_FETCH_TYPE = "ARGUMENT_FETCH_TYPE";

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

    /**
     * Construct a bundle to help to create the intent.
     *
     * Fetch most recent strip
     *
     * @param id strip id
     * @return android bundle
     */
    public static Bundle newInstance() {

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_FETCH_TYPE, FetchStripType.MOST_RECENT);

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
            }

            mStripFragment = DisplayStripFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mStripFragment,
                    R.id.contentFrame);
        }

        // Create the presenter
        DaggerStripComponent.builder()
                .dataSourceComponent(((MyApp) getApplication()).getDataSourceComponent())
                .displayStripPresenterModule(
                        new DisplayStripPresenterModule(FetchStripType.ID, id, mStripFragment)).build()
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
