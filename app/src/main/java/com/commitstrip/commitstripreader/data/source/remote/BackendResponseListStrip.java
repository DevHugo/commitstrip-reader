package com.commitstrip.commitstripreader.data.source.remote;

import com.commitstrip.commitstripreader.dto.StripDto;

import org.reactivestreams.Publisher;

import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * A simple object to map the response from the backend for the url /strip/
 */
public class BackendResponseListStrip {

    private List<StripDto> content;
    private int totalElements;

    public List<StripDto> getContent() {
        return content;
    }

    public void setContent(List<StripDto> content) {
        this.content = content;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
}
