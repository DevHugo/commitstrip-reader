package com.commitstrip.commitstripreader.dto;

import com.commitstrip.commitstripreader.dto.serializer.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

public class StripDto {

    private Long id;
    private String title;
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date date;
    private String thumbnail;
    private String content;
    private String url;
    private Long next;
    private Long previous;

    public StripDto() {
    }

    public StripDto(String title, Date date, String thumbnail, String content, String url) {
        this.title = title;
        this.date = date;
        this.thumbnail = thumbnail;
        this.content = content;
        this.url = url;
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNext(Long next) {
        this.next = next;
    }

    public Long getPrevious() {
        return previous;
    }

    public void setPrevious(Long previous) {
        this.previous = previous;
    }

    public Long getNext() {
        return next;
    }

    @Override
    public String toString() {
        return "com.commitstrip.commitstripreader.model.StripDto{" +
                "title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StripDto stripDto = (StripDto) o;

        if (id == null || stripDto.getId() == null) return false;

        return id.equals(stripDto.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
