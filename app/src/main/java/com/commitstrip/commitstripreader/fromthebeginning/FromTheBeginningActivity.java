package com.commitstrip.commitstripreader.fromthebeginning;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;

import com.commitstrip.commitstripreader.BaseActivity;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripFragment;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripPresenterModule;
import com.commitstrip.commitstripreader.common.displaystrip.FetchStripType;
import com.commitstrip.commitstripreader.data.component.DataSourceComponent;
import com.commitstrip.commitstripreader.util.ActivityUtils;

import java.util.Date;

import javax.inject.Inject;

public class FromTheBeginningActivity extends BaseActivity {

    @Inject FromTheBeginningPresenter mStripPresenter;

    private DisplayStripFragment mStripFragment;

    /**
     * Construct a bundle to help to create the intent.
     *
     * @return android bundle
     */
    public static Bundle newInstance() {
        return new Bundle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FetchStripType fetchStripType = FetchStripType.ID;

        DataSourceComponent dataSourceComponent = ((MyApp) getApplication()).getDataSourceComponent();

        Long id = dataSourceComponent.getStripRepository().fetchLastReadIdFromTheBeginningMode();

        if (id == -1) {
            fetchStripType = FetchStripType.OLDER;
        }

        // Create the fragment
        mStripFragment = (DisplayStripFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mStripFragment == null) {

            mStripFragment = DisplayStripFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mStripFragment,
                    R.id.contentFrame);
        }

        DaggerFromTheBeginningComponent.builder()
                .dataSourceComponent(dataSourceComponent)
                .displayStripPresenterModule(
                        new DisplayStripPresenterModule(fetchStripType, id, mStripFragment))
                .build()
                .inject(this);
    }

    public void onResume() {
        super.onResume();

        Long id = mStripPresenter.fetchLastReadIdFromTheBeginningMode();

        if (id != -1 && !mStripPresenter.getStripId().equals(id)) {
            mStripPresenter.passToStrip(id);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){

            if (mStripFragment != null) {
                return mStripFragment.onKeyVolumeDown();
            }
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){

            if (mStripFragment != null) {
                return mStripFragment.onKeyVolumeUp();
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
