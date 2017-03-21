package com.commitstrip.commitstripreader.util.converter;

import com.commitstrip.commitstripreader.common.dto.StripWithImageDto;
import com.commitstrip.commitstripreader.dto.StripDto;

import io.reactivex.functions.Function;

public class StripWithImageDtoToStripDto implements Function<StripWithImageDto, StripDto> {


    @Override
    public StripDto apply(StripWithImageDto from) {
        
        StripDto to = new StripDto();
            to.setId(from.getId());
            to.setContent(from.getContent());
            to.setPrevious(from.getPrevious());
            to.setNext(from.getNext());
            to.setReleaseDate(from.getReleaseDate());
            to.setThumbnail(from.getThumbnail());
            to.setUrl(from.getUrl());
        
        return to;
    }
}
