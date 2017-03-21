package com.commitstrip.commitstripreader.common.dto;

import com.squareup.picasso.RequestCreator;

import java.util.Date;

/**
 *
 */
public class DisplayStripDto {

    private Long mId;
    private String mTitle;
    private String mContent;
    private Date mDate;
    private RequestCreator mImageRequestCreator;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public RequestCreator getImageRequestCreator() {
        return mImageRequestCreator;
    }

    public void setImageRequestCreator(RequestCreator imageRequestCreator) {
        mImageRequestCreator = imageRequestCreator;
    }

    public Date getReleaseDate() {
        return mDate;
    }

    public void setReleaseDate(Date date) {
        mDate = date;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisplayStripDto that = (DisplayStripDto) o;

        if (mId != null ? !mId.equals(that.mId) : that.mId != null) return false;
        if (mTitle != null ? !mTitle.equals(that.mTitle) : that.mTitle != null) return false;
        if (mContent != null ? !mContent.equals(that.mContent) : that.mContent != null) return false;
        return mDate != null ? mDate.equals(that.getReleaseDate()) : that.getReleaseDate() == null;

    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0);
        result = 31 * result + (mDate != null ? mDate.hashCode() : 0);
        result = 31 * result + (mContent != null ? mContent.hashCode() : 0);
        return result;
    }


}
