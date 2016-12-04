package com.commitstrip.commitstripreader.backend.dao;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class StripDao {

    @Id
    private Long id;

    private String title;
    private Date date;
    private String thumbnail;
    private String content;
    private String url;
    private Long next;
    private Long previous;

    public StripDao() {
        id = Long.valueOf(0);
        title = "";
        date = new Date();
        thumbnail = "";
        content = "";
        url = "";
    }

    public StripDao(Long id, String title, Date date, String thumbnail, String content, String url) {
        this.id = id;
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

    public Long getNext() {
        return next;
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

    @Override
    public String toString() {
        return "StripDao{" +
                "title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StripDao stripDao = (StripDao) o;

        return id.equals(stripDao.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
