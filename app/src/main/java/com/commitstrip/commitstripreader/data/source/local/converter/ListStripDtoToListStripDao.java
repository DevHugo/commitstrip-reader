package com.commitstrip.commitstripreader.data.source.local.converter;

import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.dto.StripDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.functions.Function;

public class ListStripDtoToListStripDao implements Function<List<StripDto>, List<StripDaoEntity>> {

    private String TAG = "StripDtoToListStripDao";

    @Override
    public List<StripDaoEntity> apply(List<StripDto> source) {

        if (source == null)
            throw new IllegalArgumentException();

        List<StripDaoEntity> strips = new ArrayList();

        StripDtoToStripDao converter = new StripDtoToStripDao();
        for (StripDto strip : source) {
            strips.add(converter.apply(strip));
        }

        return strips;
    }
}
