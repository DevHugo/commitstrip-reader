package com.commitstrip.commitstripreader.data.source.remote;

import com.commitstrip.commitstripreader.dto.StripDto;

import java.util.List;

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
