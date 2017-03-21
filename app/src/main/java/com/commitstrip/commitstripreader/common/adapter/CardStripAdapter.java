package com.commitstrip.commitstripreader.common.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.commitstrip.commitstripreader.common.dto.StripWithImageDto;
import com.commitstrip.commitstripreader.util.Preconditions;

import java.util.List;

public class CardStripAdapter extends RecyclerView.Adapter<CardStripViewHolder> {

    @NonNull private final Integer mLayout; // Target card layout.

    /**
     * Callback for the on touch event on the card, the like button and the fullscreen mode.
     */
    public interface OnItemClickListener {
        void onItemClick(StripWithImageDto item);
    }

    /**
     * Callback for the on touch event on the card, the like button and the fullscreen mode.
     */
    public interface OnLikeOrUnlikeItemClickListener {
        void onItemClick(StripWithImageDto item, Drawable drawable);
    }

    /**
     * Callback on the share button click listener.
     */
    public interface OnShareButtonClickListener {
        void onItemClick(StripWithImageDto strip, ImageView img_strip);
    }

    @NonNull private List<StripWithImageDto> mStrips;
    @Nullable private final OnItemClickListener mDisplayDetailStripListener;
    @Nullable private final OnLikeOrUnlikeItemClickListener mLikeOrUnlikeListener;
    @Nullable private final OnItemClickListener mFullscreenListener;
    @Nullable private final OnShareButtonClickListener mShareListener;

    /**
     * Construct an instance of StripCardAdapter.
     *
     * @param strips list strip to display.
     * @param displayDetailStripListener callback on card click.
     * @param likeOrUnlikeListener callback when the user like or dislike the content.
     * @param fullscreenListener callback when the user ask for displaying the strip on fullscreen.
     * @param shareListener callback when the user want to share content.
     * @param layout target layout for the strip.
     */
    public CardStripAdapter(@NonNull List<StripWithImageDto> strips,
            @Nullable OnItemClickListener displayDetailStripListener,
            @Nullable OnLikeOrUnlikeItemClickListener likeOrUnlikeListener,
            @Nullable OnItemClickListener fullscreenListener,
            @Nullable OnShareButtonClickListener shareListener,
            @NonNull Integer layout) {

        Preconditions.checkNotNull(strips);
        Preconditions.checkNotNull(layout);

        mStrips = strips;
        mDisplayDetailStripListener = displayDetailStripListener;
        mLikeOrUnlikeListener = likeOrUnlikeListener;
        mFullscreenListener = fullscreenListener;
        mShareListener = shareListener;

        mLayout = layout;
    }

    /* (no-Javadoc) */
    @Override
    public CardStripViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(mLayout, viewGroup, false);
        return new CardStripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardStripViewHolder holder, int position) {
        holder.bind(
                mStrips.get(position),
                mDisplayDetailStripListener,
                mLikeOrUnlikeListener,
                mFullscreenListener,
                mShareListener);
    }

    /* (no-Javadoc) */
    @Override
    public int getItemCount() {
        return mStrips.size();
    }

    /**
     * Return current strip list displayed
     *
     * @return strips
     */
    public List<StripWithImageDto> getStrips () {
        return mStrips;
    }
}
