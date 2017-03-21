package com.commitstrip.commitstripreader.data.source.local.converter;

import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.dto.StripDto;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Function;

public class ListStripDaoToListStripDto implements Function<Iterable<StripDaoEntity>, Iterable<StripDto>> {

  @Override
  public Iterable<StripDto> apply(Iterable<StripDaoEntity> source) {

    if (source == null)
        throw new IllegalArgumentException();

    List<StripDto> strips = new ArrayList<>();

    StripDaoToStripDto converter = new StripDaoToStripDto();
    for (StripDaoEntity strip : source) {
        strips.add(converter.apply(strip));
    }

    return strips;
  }
}
