package com.commitstrip.commitstripreader.dto;

import java.util.ArrayList;
import java.util.List;

public class NotificationDataPayload {

    private List<SimpleStripDto> strips;

    public NotificationDataPayload() {
        strips = new ArrayList<>();
    }

    public List<SimpleStripDto> getStrips() {
        return strips;
    }

    public void setStrips(List<SimpleStripDto> strips) {
        this.strips = strips;
    }
}
