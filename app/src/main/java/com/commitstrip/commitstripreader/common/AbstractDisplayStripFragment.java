package com.commitstrip.commitstripreader.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.strip.StripContract;
import com.commitstrip.commitstripreader.strip.StripFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.RequestCreator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Abstract fragment use for displaying a strip.
 *
 * Extend this class and override the two methods onSwipeLeft and onSwipeRight for using it.
 */
public abstract class AbstractDisplayStripFragment extends Fragment implements AbstractDisplayStripContract.View {
    private String TAG = "StripFragment";

    @NonNull
    public static String ARGUMENT_STRIP_ID = "ARGUMENT_STRIP_ID";

    protected AbstractDisplayStripContract.Presenter mPresenter;
    private StripDto mStrip = null;

    @BindView(R.id.strip) protected ImageView mImage;
    @BindView(R.id.title) protected TextView mTitle;

    private Unbinder uibinder;
    private PhotoViewAttacher mAttacher;

    private MenuItem mItemMenuFavorite;
    private MenuItem mItemMenuUnFavorite;

    @NonNull private static final int SWIPE_DISTANCE_THRESHOLD = 100;
    @NonNull private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private boolean alreadyLoadMenuInformation = false;
    private boolean isFavorite;

    @BindView(R.id.error_view) protected LinearLayout mErrorView;
    @BindView(R.id.error_text) protected TextView mErrorText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_strip, container, false);

        // Use ButterKnife to inject field
        uibinder = ButterKnife.bind(this, view);

        // Listener for the swap left or right
        view.setOnTouchListener(new OnSwipeTouchListener(getContext()));

        // For the favorite button
        setHasOptionsMenu(true);

        mPresenter.subscribe();

        Bundle bundle = getArguments();

        // If we already have an id in the bundle, we need to fetch the specified strip
        if (bundle.containsKey(ARGUMENT_STRIP_ID)) {
            Long mStripId = bundle.getLong(ARGUMENT_STRIP_ID);
            mPresenter.fetchStrip(mStripId);
        }
        else {
            // Fetch the most recent strip
            mPresenter.fetchStrip(null);
        }

        // A small library for zooming capabilities on the strip image
        mAttacher = new PhotoViewAttacher(mImage);

        // Listener for the swap left or right on the strip image
        mAttacher.setOnSingleFlingListener((e1, e2, velocityX, velocityY) -> reactToFling(e1, e2, velocityX, velocityY));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_strip, menu);
        super.onCreateOptionsMenu(menu,inflater);

        mItemMenuFavorite = menu.findItem(R.id.favorite);
        mItemMenuUnFavorite = menu.findItem(R.id.unfavorite);

        // Warning we can pass by this method before or after loading the menu information. Menu informations are fetch async in the presenter.
        if (alreadyLoadMenuInformation) {
            displayIconIsFavorite(isFavorite);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite:
                mPresenter.deleteFavorite();
                mItemMenuFavorite.setVisible(false);
                mItemMenuUnFavorite.setVisible(true);
                break;
            case R.id.unfavorite:
                mPresenter.addFavorite();
                mItemMenuFavorite.setVisible(true);
                mItemMenuUnFavorite.setVisible(false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setAbstractPresenter(AbstractDisplayStripPresenter abstractDisplayStripPresenter) {
        this.mPresenter = abstractDisplayStripPresenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        uibinder.unbind();
        mPresenter.unsubscribe();
        mAttacher.cleanup();
    }

    @Override
    public void displayImage(RequestCreator requestCreator) {

        requestCreator
                .fit()
                .centerInside()
                .error(R.drawable.error)
                .into(mImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        mAttacher.update();
                    }

                    @Override
                    public void onError() {
                        Log.e(TAG, "Error when downloading image");
                    }
                });

    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void displayIconIsFavorite(boolean isFavorite) {

        if (mItemMenuFavorite != null && mItemMenuUnFavorite != null) {
            mItemMenuFavorite.setVisible(isFavorite);
            mItemMenuUnFavorite.setVisible(!isFavorite);
        }
        else {
            alreadyLoadMenuInformation = true;
            this.isFavorite = isFavorite;
        }
    }

    @Override
    public void displayError() {
        mTitle.setVisibility(View.GONE);
        mImage.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mErrorText.setText(getString(R.string.error_fetch_strips));
    }

    private boolean reactToFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float distanceX = e2.getX() - e1.getX();
        float distanceY = e2.getY() - e1.getY();
        if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (distanceX > 0)
                onSwipeLeft();
            else
                onSwipeRight();

            return true;
        }
        return false;
    }

    public abstract void onSwipeLeft();

    public abstract void onSwipeRight();

    @Override
    public boolean onKeyVolumeUp(int keyCode, KeyEvent event) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (sharedPreferences.getBoolean(Configuration.SHAREDPREFERENCES_KEY_SHOULD_USE_VOLUME_KEY, false)) {
            onSwipeLeft();

            return true;
        }
        else {
            return false;
        }

    }

    @Override
    public boolean onKeyVolumeDown(int keyCode, KeyEvent event) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (sharedPreferences.getBoolean(Configuration.SHAREDPREFERENCES_KEY_SHOULD_USE_VOLUME_KEY, false)) {
            onSwipeRight();

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Created by Edward Brey (http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures)
     */
    private class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new OnSwipeTouchListener.GestureListener());
        }

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return reactToFling(e1, e2, velocityX, velocityY);
            }
        }

    }
}
