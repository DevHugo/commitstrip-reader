package com.commitstrip.commitstripreader.data.source.local.converter;

import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class StripDtoToStripDaoTest {

    @Test
    public void convertStripDtoWithEmptyStripShouldReturnEmpty () {
        StripDtoToStripDao converter = new StripDtoToStripDao();

        StripDto source = new StripDto();
        source.setId(1L);

        StripDaoEntity other = converter.apply(source);
        assertTrue (source.getId().equals(other.getId()));
    }

    @Test
    public void convertStripDaoShouldReturnStripDao () {

        StripDto source = SampleStrip.generateSampleDto();

        StripDtoToStripDao converter = new StripDtoToStripDao();
        StripDaoEntity other = converter.apply(source);

        SampleStrip.compareEveryPropertiesOfStripDtoVsStripDao(source, other);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertStripWithNullShouldReturnIllegalException () {
        StripDaoToStripDto converter = new StripDaoToStripDto();
        converter.apply(null);
    }
}
