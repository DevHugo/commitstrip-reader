package com.commitstrip.commitstripreader.dto;

import java.io.Serializable;
import java.util.Date;

public class StripDto implements Serializable {

    private Long id;
    private String title;
    private Date releaseDate;
    private String thumbnail;
    private String content;
    private String url;
    private Long next;
    private Long previous;

    public StripDto() {
    }

    public StripDto(String title, Date releaseDate, String thumbnail, String content, String url) {
        this.title = title;
        this.releaseDate = releaseDate;
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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date date) {
        this.releaseDate = date;
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

        if (id != null ? !id.equals(stripDto.id) : stripDto.id != null) return false;
        if (title != null ? !title.equals(stripDto.title) : stripDto.title != null) return false;
        if (releaseDate != null ? !releaseDate.equals(stripDto.releaseDate) : stripDto.releaseDate != null) return false;
        if (thumbnail != null ? !thumbnail.equals(stripDto.thumbnail)
                : stripDto.thumbnail != null) {
            return false;
        }
        if (content != null ? !content.equals(stripDto.content) : stripDto.content != null) {
            return false;
        }
        if (url != null ? !url.equals(stripDto.url) : stripDto.url != null) return false;
        if (next != null ? !next.equals(stripDto.next) : stripDto.next != null) return false;
        return previous != null ? previous.equals(stripDto.previous) : stripDto.previous == null;

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
