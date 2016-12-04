package com.commitstrip.commitstripreader.displayfavorite;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.commitstrip.commitstripreader.common.AbstractDisplayStripFragment;
import com.commitstrip.commitstripreader.strip.StripFragment;

/**
 * Display a Strip
 */
public class DisplayFavoriteStripFragment extends AbstractDisplayStripFragment implements DisplayFavoriteStripContract.View {

    @NonNull
    private static String ARGUMENT_STRIP_ID = "ARGUMENT_STRIP_ID";

    public static DisplayFavoriteStripFragment newInstance(Long id) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_STRIP_ID, id);

        DisplayFavoriteStripFragment fragment = new DisplayFavoriteStripFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    public static DisplayFavoriteStripFragment newInstance() {
        Bundle arguments = new Bundle();

        DisplayFavoriteStripFragment fragment = new DisplayFavoriteStripFragment();
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