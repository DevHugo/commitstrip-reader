package com.commitstrip.commitstripreader.backend.converter;

import com.commitstrip.commitstripreader.backend.dao.StripDao;
import com.commitstrip.commitstripreader.dto.SimpleStripDto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StripDaoToSimpleStripDto implements Converter<StripDao, SimpleStripDto> {

    @Override
    public SimpleStripDto convert(StripDao source) {
        SimpleStripDto strip = new SimpleStripDto();
            strip.setId(source.getId());
            strip.setTitle(source.getTitle());
            strip.setContent(source.getContent());

        return strip;
    }
}
