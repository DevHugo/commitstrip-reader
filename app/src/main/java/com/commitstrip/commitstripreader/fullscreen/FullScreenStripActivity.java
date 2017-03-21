package com.commitstrip.commitstripreader.fullscreen;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.squareup.picasso.RequestCreator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

public class FullScreenStripActivity extends AppCompatActivity implements
        FullScreenStripContrat.View {

    @Nullable @Inject FullScreenStripPresenter mStripPresenter;

    @BindView(R.id.strip)
    PhotoView mImageView;

    @BindView(R.id.error_view)
    LinearLayout mErrorView;

    @BindView(R.id.error_text)
    TextView mErrorText;

    public static String ARGUMENT_STRIP = "STRIP";
    public static String ARGUMENT_READ_FROM_0 = "READ_FROM_0";

    public static Bundle newInstance(StripDto strip, Boolean readFrom0Mode) {
        Bundle args = new Bundle();
            args.putSerializable(ARGUMENT_STRIP, strip);
            args.putBoolean(ARGUMENT_READ_FROM_0, readFrom0Mode);

        return args;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ask for FullScreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the view after the RequestWindowsMode
        setContentView(R.layout.activity_fullscreenstrip);

        // Get Strip id
        StripDto currentStripDto = null;
        boolean readFrom0Mode = false;
        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null &&
                    extras.containsKey(ARGUMENT_STRIP) &&
                    extras.containsKey(ARGUMENT_READ_FROM_0)) {
                currentStripDto = (StripDto) extras.getSerializable(ARGUMENT_STRIP);
                readFrom0Mode = extras.getBoolean(ARGUMENT_READ_FROM_0);
            }
        }

        // Ask ButterKnife to inject view field
        ButterKnife.bind(this);

        // Check id is correct
        if (currentStripDto != null) {

            // Inject the presenter with Dagger2
            DaggerFullScreenStripComponent.builder()
                    .dataSourceComponent(((MyApp) getApplication()).getDataSourceComponent())
                    .fullScreenStripPresenterModule(
                            new FullScreenStripPresenterModule(currentStripDto, readFrom0Mode, this)).build()
                    .inject(this);

            // Subscribe to the presenter
            mStripPresenter.subscribe();

            // Fetch image strip
            displayImage(currentStripDto.getId(), currentStripDto.getContent());
        } else {
            mErrorView.setVisibility(View.VISIBLE);
            mErrorText.setText(getString(R.string.error_fetch_strips));
        }

        mImageView.setOnSingleFlingListener(this::reactToFling);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            if (mStripPresenter != null && mStripPresenter.fetchPriorityForUseVolumeKey()) {
                onSwipeRight();

                return true;
            } else {
                return false;
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

            if (mStripPresenter != null && mStripPresenter.fetchPriorityForUseVolumeKey()) {
                onSwipeLeft();

                return true;
            } else {
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void displayImage(Long id, String url) {

        if (mStripPresenter != null) {
            RequestCreator requestCreator = mStripPresenter.getImageStrip(id, url);
            requestCreator.fit().centerInside().into(mImageView);
        }
    }

    public void onSwipeRight() {
        if (mStripPresenter != null) {
            mStripPresenter.onSwipeRight();
        }
    }

    public void onSwipeLeft() {
        if (mStripPresenter != null) {
            mStripPresenter.onSwipeLeft();
        }
    }

    @Override
    public void askForDisplayImage(StripDto mStrip) {
        displayImage(mStrip.getId(), mStrip.getContent());
    }

    private boolean reactToFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (e1 != null && e2 != null) {

            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) &&
                    Math.abs(distanceX) > Configuration.SWIPE_DISTANCE_THRESHOLD &&
                    Math.abs(velocityX) > Configuration.SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    onSwipeLeft();
                } else {
                    onSwipeRight();
                }

                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mStripPresenter != null) {
            mStripPresenter.unsubscribe();
        }
    }

}
