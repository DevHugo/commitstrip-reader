package com.commitstrip.commitstripreader.displayfavorite;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;

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
    private DisplayFavoriteStripFragment mStripFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the fragment
        mStripFragment = (DisplayFavoriteStripFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mStripFragment == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null && extras.containsKey(AbstractDisplayStripFragment.ARGUMENT_STRIP_ID))
                mStripFragment = DisplayFavoriteStripFragment.newInstance(extras.getLong(AbstractDisplayStripFragment.ARGUMENT_STRIP_ID));
            else
                mStripFragment = DisplayFavoriteStripFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mStripFragment, R.id.contentFrame);
        }

        // Create the presenter
        DaggerDisplayFavoriteStripComponent.builder()
                .stripRepositoryComponent(StripRepositorySingleton.getInstance(getApplicationContext()).getStripRepositoryComponent())
                .displayFavoriteStripPresenterModule(new DisplayFavoriteStripPresenterModule(mStripFragment)).build()
                .inject(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){

            if (mStripFragment == null) {
                return false;
            }
            else {
                return mStripFragment.onKeyVolumeDown(keyCode, event);
            }
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){

            if (mStripFragment == null) {
                return false;
            }
            else {
                return mStripFragment.onKeyVolumeUp(keyCode, event);
            }
        }
        else {
            return false;
        }
    }
}
