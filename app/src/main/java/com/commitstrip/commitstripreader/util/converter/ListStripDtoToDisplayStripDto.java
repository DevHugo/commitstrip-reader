package com.commitstrip.commitstripreader.util.converter;

import com.commitstrip.commitstripreader.common.dto.DisplayStripDto;
import com.commitstrip.commitstripreader.dto.StripDto;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.List;

public class ListStripDtoToDisplayStripDto implements
    Function<Iterable<StripDto>, Iterable<DisplayStripDto>> {

  @Override
  public Iterable<DisplayStripDto> apply(Iterable<StripDto> source) throws Exception {

    if (source == null)
      throw new IllegalArgumentException();

    List<DisplayStripDto> to = new ArrayList<>();

    StripDtoToDisplayStripDto converter = new StripDtoToDisplayStripDto();

    for (StripDto strip : source) {
      to.add(converter.apply(strip));
    }

    return to;
  }

}
