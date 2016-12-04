package com.commitstrip.commitstripreader.data.source.local.converter;

import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ListStripDtoToListStripDaoTest {

    @Test
    public void convertListStripWithEmptyListShouldReturnEmptyList () {
        ListStripDtoToListStripDao converter = new ListStripDtoToListStripDao();
        assertTrue (converter.apply(new ArrayList<>()).equals(new ArrayList<>()));
    }

    @Test
    public void convertListStripWithOneStripShouldReturnStrip () {

        List<StripDto> source = new ArrayList<>();
            source.add(SampleStrip.generateSampleDto());

        ListStripDtoToListStripDao converter = new ListStripDtoToListStripDao();
        List<StripDaoEntity> other = converter.apply(source);

        SampleStrip.compareEveryPropertiesOfStripDtoVsStripDao(source.get(0), other.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertListStripWithNullShouldReturnIllegalException () {
        ListStripDtoToListStripDao converter = new ListStripDtoToListStripDao();
        converter.apply(null);
    }

}
