package com.commitstrip.commitstripreader.backend.converter;

import com.commitstrip.commitstripreader.backend.dao.StripDao;
import com.commitstrip.commitstripreader.dto.StripDto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class StripDaoToStrip implements Converter<StripDao, StripDto> {

    @Override
    public StripDto convert(StripDao source) {

        StripDto to = new StripDto();
            to.setId(source.getId());
            to.setTitle(source.getTitle());
            to.setContent(source.getContent());
            to.setReleaseDate(source.getReleaseDate());
            to.setThumbnail(source.getThumbnail());
            to.setUrl(source.getUrl());
            to.setNext(source.getNext());
            to.setPrevious(source.getPrevious());

        return to;
    }
}
