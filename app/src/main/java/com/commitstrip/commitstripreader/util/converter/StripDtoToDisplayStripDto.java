package com.commitstrip.commitstripreader.util.converter;

import com.commitstrip.commitstripreader.common.dto.DisplayStripDto;
import com.commitstrip.commitstripreader.dto.StripDto;

import io.reactivex.functions.Function;

public class StripDtoToDisplayStripDto implements
        Function<StripDto, DisplayStripDto> {

    @Override
    public DisplayStripDto apply(StripDto stripDto) throws Exception {

        DisplayStripDto displayStripDto = new DisplayStripDto();

        displayStripDto.setId(stripDto.getId());
        displayStripDto.setContent(stripDto.getContent());
        displayStripDto.setTitle(stripDto.getTitle());
        displayStripDto.setReleaseDate(stripDto.getReleaseDate());

        return displayStripDto;
    }
}
