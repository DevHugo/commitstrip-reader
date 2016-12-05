package com.commitstrip.commitstripreader.strip;

import android.app.AlarmManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.commitstrip.commitstripreader.BaseActivity;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.data.source.StripRepositorySingleton;
import com.commitstrip.commitstripreader.util.ActivityUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class StripActivity extends BaseActivity {

    @Inject
    StripPresenter mStripPresenter;

    @NonNull public static String ARGUMENT_STRIP_ID = "ARGUMENT_STRIP_ID";
    private StripFragment mStripFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the fragment
        mStripFragment = (StripFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mStripFragment == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null && extras.containsKey(ARGUMENT_STRIP_ID))
                mStripFragment = StripFragment.newInstance(extras.getLong(ARGUMENT_STRIP_ID));
            else
                mStripFragment = StripFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mStripFragment, R.id.contentFrame);
        }

        // Create the presenter
        DaggerStripComponent.builder()
                .stripRepositoryComponent(StripRepositorySingleton.getInstance(getApplicationContext()).getStripRepositoryComponent())
                .stripPresenterModule(new StripPresenterModule(mStripFragment)).build()
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
