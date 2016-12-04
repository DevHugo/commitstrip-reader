package com.commitstrip.commitstripreader.listfavorite;

import com.squareup.picasso.RequestCreator;

public class ListFavoriteDto {

    private Long id;
    private String title;
    private RequestCreator imageRequestCreator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RequestCreator getImageRequestCreator() {
        return imageRequestCreator;
    }

    public void setImageRequestCreator(RequestCreator imageRequestCreator) {
        this.imageRequestCreator = imageRequestCreator;
    }

}
