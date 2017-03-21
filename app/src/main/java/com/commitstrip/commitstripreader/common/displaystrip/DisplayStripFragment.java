package com.commitstrip.commitstripreader.common.displaystrip;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.GestureDetector;
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

import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.fullscreen.FullScreenStripActivity;
import com.commitstrip.commitstripreader.util.ImageUtils;
import com.commitstrip.commitstripreader.util.di.component.DaggerImageUtilsComponent;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.RequestCreator;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Abstract fragment use for displaying a strip.
 *
 * Extend this class and override the two methods onSwipeLeft and onSwipeRight for using it.
 */
public class DisplayStripFragment extends Fragment implements DisplayStripContract.View {

    private final String TAG = "StripFragment";

    private final Integer SWIPE_DISTANCE_THRESHOLD = Configuration.SWIPE_DISTANCE_THRESHOLD;
    private final Integer SWIPE_VELOCITY_THRESHOLD = Configuration.SWIPE_VELOCITY_THRESHOLD;

    private Unbinder mUiBinder;
    private PhotoViewAttacher mAttacher;

    private LikeButton mIconFavorite;

    @BindView(R.id.strip) protected ImageView mImage;
    @BindView(R.id.title) protected TextView mTitle;
    @BindView(R.id.error_view) protected LinearLayout mErrorView;
    @BindView(R.id.error_text) protected TextView mErrorText;

    private DisplayStripContract.Presenter mPresenter;

    @Inject ImageUtils mImageUtils;

    public static DisplayStripFragment newInstance() {
        Bundle arguments = new Bundle();

        DisplayStripFragment fragment = new DisplayStripFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    /* (non-Javadoc) */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_strip, container, false);

        // Inject dependencies
        DaggerImageUtilsComponent.builder()
                .build().inject(this);

        // First thing to do is to notify our presenter
        mPresenter.subscribe();

        // Use ButterKnife to inject field
        mUiBinder = ButterKnife.bind(this, view);

        // Listener for the swap left or right
        view.setOnTouchListener(new OnSwipeTouchListener(getContext()));

        // For the favorite button
        setHasOptionsMenu(true);

        // A small library for zooming capabilities on the strip image
        mAttacher = new PhotoViewAttacher(mImage);

        // Listener for the swap left or right on the strip image
        mAttacher.setOnSingleFlingListener(this::reactToFling);

        return view;
    }

    /* (non-Javadoc) */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_strip, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem mIconMenuFavorite = menu.findItem(R.id.fav_button_menu);

        mIconFavorite = (LikeButton) mIconMenuFavorite
                .getActionView()
                .findViewById(R.id.fav_button);

        mIconFavorite.setLiked(mPresenter.isFav());

        mIconFavorite.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                Drawable drawable = mImage.getDrawable();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                if (drawable != null) {

                    int compressionImage = mPresenter.getCompressionLevelImages();

                    byteArrayOutputStream = mImageUtils.transform(drawable, compressionImage);
                }

                mPresenter.addFavorite(byteArrayOutputStream);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                mPresenter.deleteFavorite();
            }
        });

    }

    /* (non-Javadoc) */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_button_menu:

                Drawable drawable = mImage.getDrawable();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                if (drawable != null) {
                    byteArrayOutputStream = mImageUtils.transform(drawable, 100);
                }

                File newFile = mPresenter
                        .saveSharedImageInSharedDirectory(1L, byteArrayOutputStream);

                Uri contentUri = FileProvider.getUriForFile(getContext(),
                        "com.commitstrip.commitstripreader",
                        newFile);

                if (contentUri != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.setDataAndType(contentUri,
                            getContext().getContentResolver().getType(contentUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(shareIntent,
                            getString(R.string.share_content_title)));
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* (non-Javadoc) */
    @Override
    public void setAbstractPresenter(DisplayStripAbstractPresenter abstractDisplayStripPresenter) {
        this.mPresenter = abstractDisplayStripPresenter;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    /* (non-Javadoc) */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUiBinder != null) {
            mUiBinder.unbind();
        }

        if (mPresenter != null) {
            mPresenter.unsubscribe();
        }

        if (mAttacher != null) {
            mAttacher.cleanup();
        }
    }

    private void askForFullScreen(StripDto strip) {
        if (strip != null && strip.getId() != null && strip.getContent() != null) {

            Intent intent = new Intent(getContext(), FullScreenStripActivity.class);
            intent.putExtras(FullScreenStripActivity.newInstance(
                    strip,
                    mPresenter.shouldUpdateNextStripOnFullScreen()
            ));

            startActivity(intent);
        }
    }

    /* (non-Javadoc) */
    @Override
    public void displayIconIsFavorite(boolean isFavorite) {
        mIconFavorite.setLiked(isFavorite);
    }

    /* (non-Javadoc) */
    public void onSwipeLeft() { mPresenter.onSwipeLeft(); }

    /* (non-Javadoc) */
    public void onSwipeRight() {
        mPresenter.onSwipeRight();
    }

    /* (non-Javadoc) */
    @Override
    public boolean onKeyVolumeUp() {

        if (mPresenter.fetchPriorityForUseVolumeKey()) {
            onSwipeLeft();

            return true;
        } else {
            return false;
        }

    }

    /* (non-Javadoc) */
    @Override
    public boolean onKeyVolumeDown() {

        if (mPresenter.fetchPriorityForUseVolumeKey()) {
            onSwipeRight();

            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc) */
    @Override
    public void displayErrorAddFavorite() {

        Context context;
        if (getActivity() != null) {
            context = getActivity().getApplicationContext();
        } else {
            context = getContext();
        }

        TastyToast.makeText(
                context,
                getString(R.string.error_add_favorite),
                TastyToast.LENGTH_LONG,
                TastyToast.ERROR);
    }

    /* (non-Javadoc) */
    @Override
    public void displayErrorFetchStrip() {
        Context context;
        if (getActivity() != null) {
            context = getActivity().getApplicationContext();
        } else {
            context = getContext();
        }

        TastyToast.makeText(
                context,
                getString(R.string.error_fetch_strips),
                TastyToast.LENGTH_LONG,
                TastyToast.ERROR);
    }

    @Override
    public void displayStrip(StripDto strip, RequestCreator requestCreator) {

        mTitle.setText(strip.getTitle());

        requestCreator

                .fit()
                .centerInside()
                .error(R.drawable.placeholder)
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

        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                askForFullScreen(strip);
            }

            @Override
            public void onOutsidePhotoTap() {
                askForFullScreen(strip);
            }
        });

    }

    /**
     * Created by Edward Brey (http://stackoverflow
     * .com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures)
     */
    private class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context,
                    new OnSwipeTouchListener.GestureListener());
        }

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, 
                    float velocityY) {
                return reactToFling(e1, e2, velocityX, velocityY);
            }
        }

    }

    private boolean reactToFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (e1 != null && e2 != null) {

            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX)
                    > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
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
}
