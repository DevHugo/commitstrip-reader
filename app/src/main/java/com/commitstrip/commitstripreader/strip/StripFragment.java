package com.commitstrip.commitstripreader.strip;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.AbstractDisplayStripFragment;
import com.commitstrip.commitstripreader.common.AbstractDisplayStripPresenter;
import com.commitstrip.commitstripreader.displayfavorite.DisplayFavoriteStripFragment;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.squareup.picasso.Callback;
import com.squareup.picasso.RequestCreator;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Display a Strip
 */
public class StripFragment extends AbstractDisplayStripFragment implements StripContract.View
{

    public static StripFragment newInstance(Long id) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_STRIP_ID, id);

        StripFragment fragment = new StripFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    public static StripFragment newInstance() {
        Bundle arguments = new Bundle();

        StripFragment fragment = new StripFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    public void onSwipeLeft() {
        Long id = mPresenter.askForNextIdStrip();

        if (id != null) {
            mPresenter.fetchStrip(id);
        }
    }

    public void onSwipeRight() {

        Long id = mPresenter.askForPreviousIdStrip();

        if (id != null) {
            mPresenter.fetchStrip(id);
        }
    }

}
