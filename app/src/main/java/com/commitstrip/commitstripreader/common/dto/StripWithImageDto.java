package com.commitstrip.commitstripreader.common.dto;

import com.commitstrip.commitstripreader.dto.StripDto;
import com.squareup.picasso.RequestCreator;

public class StripWithImageDto extends StripDto {

    private RequestCreator mImageRequestCreator;
    private boolean mFavorite;

    public RequestCreator getImageRequestCreator() {
        return mImageRequestCreator;
    }

    public void setImageRequestCreator(RequestCreator imageRequestCreator) {
        this.mImageRequestCreator = imageRequestCreator;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }
}
