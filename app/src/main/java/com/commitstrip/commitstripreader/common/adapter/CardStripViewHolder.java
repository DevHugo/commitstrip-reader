package com.commitstrip.commitstripreader.common.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.adapter.CardStripAdapter.OnItemClickListener;
import com.commitstrip.commitstripreader.common.adapter.CardStripAdapter.OnLikeOrUnlikeItemClickListener;
import com.commitstrip.commitstripreader.common.adapter.CardStripAdapter.OnShareButtonClickListener;
import com.commitstrip.commitstripreader.common.dto.StripWithImageDto;
import com.commitstrip.commitstripreader.util.Preconditions;
import com.like.LikeButton;
import com.like.OnLikeListener;

import butterknife.ButterKnife;


class CardStripViewHolder extends RecyclerView.ViewHolder {

    private TextView mTitle;
    private ImageView mImgStrip;
    private LikeButton mLikeButton;
    private Button mFullscreen;
    private Button mShare;

    /* (no-Javadoc) */
    CardStripViewHolder(@NonNull View view) {
        super(view);

        mTitle = (TextView) view.findViewById(R.id.title);
        mImgStrip = (ImageView) view.findViewById(R.id.img_strip);
        mLikeButton = (LikeButton) view.findViewById(R.id.fav);
        mFullscreen = (Button) view.findViewById(R.id.fullscreen);
        mShare = (Button) view.findViewById(R.id.share);

        ButterKnife.bind(view);
    }

    /* (no-Javadoc) */
    void bind(@NonNull StripWithImageDto strip,
            @Nullable OnItemClickListener displayDetailStripListener,
            @Nullable OnLikeOrUnlikeItemClickListener likeOrUnlikeListener,
            @Nullable OnItemClickListener fullscreenListener,
            @Nullable OnShareButtonClickListener shareListener) {

        Preconditions.checkNotNull(strip);

        mTitle.setText(strip.getTitle());
        strip.getImageRequestCreator().into(mImgStrip);

        if (displayDetailStripListener != null) {
            mImgStrip.setOnClickListener(view -> displayDetailStripListener.onItemClick(strip));
        } else {
            mImgStrip.setVisibility(View.GONE);
        }

        if (likeOrUnlikeListener != null) {
            mLikeButton.setLiked(strip.isFavorite());
            mLikeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    likeOrUnlikeListener.onItemClick(strip, mImgStrip.getDrawable());
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    likeOrUnlikeListener.onItemClick(strip, mImgStrip.getDrawable());
                }
            });
        } else {
            mLikeButton.setVisibility(View.GONE);
        }

        if (fullscreenListener != null) {
            mFullscreen.setOnClickListener(v -> fullscreenListener.onItemClick(strip));
        } else {
            mFullscreen.setVisibility(View.GONE);
        }

        if (shareListener != null) {
            mShare.setOnClickListener(v -> shareListener.onItemClick(strip, mImgStrip));
        }

        if (displayDetailStripListener != null) {
            itemView.setOnClickListener(v -> displayDetailStripListener.onItemClick(strip));
        }
    }

}
