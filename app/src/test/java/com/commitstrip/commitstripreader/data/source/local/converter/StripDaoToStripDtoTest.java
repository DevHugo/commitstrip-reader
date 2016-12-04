package com.commitstrip.commitstripreader.data.source.local.converter;

import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class StripDaoToStripDtoTest {

    @Test
    public void convertStripDaoWithEmptyStripShouldReturnEmpty () {
        StripDaoToStripDto converter = new StripDaoToStripDto();

        StripDaoEntity source = new StripDaoEntity();
            source.setId(1L);

        StripDto other = converter.apply(source);
        assertTrue (source.getId().equals(other.getId()));
    }

    @Test
    public void convertStripDaoShouldReturnStripDao () {

        StripDaoEntity source = SampleStrip.generateSampleDao();

        StripDaoToStripDto converter = new StripDaoToStripDto();
        StripDto other = converter.apply(source);

        SampleStrip.compareEveryPropertiesOfStripDtoVsStripDao(other, source);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertStripWithNullShouldReturnIllegalException () {
        StripDaoToStripDto converter = new StripDaoToStripDto();
        converter.apply(null);
    }

}
