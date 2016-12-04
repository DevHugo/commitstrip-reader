package com.commitstrip.commitstripreader.data.source.local.converter;

import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.dto.StripDto;

import io.reactivex.functions.Function;

public class StripDaoToStripDto implements Function<StripDaoEntity, StripDto> {

    @Override
    public StripDto apply(StripDaoEntity source) {

        if (source == null)
            throw new IllegalArgumentException();

        StripDto stripDto = new StripDto();
            stripDto.setId(source.getId());
            stripDto.setTitle(source.getTitle());
            stripDto.setContent(source.getContent());
            stripDto.setDate(source.getDate());
            stripDto.setThumbnail(source.getThumbnail());
            stripDto.setUrl(source.getUrl());
            stripDto.setNext(source.getNext());
            stripDto.setPrevious(source.getPrevious());

        return stripDto;
    }
}