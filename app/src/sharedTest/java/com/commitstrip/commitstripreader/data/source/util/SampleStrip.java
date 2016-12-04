package com.commitstrip.commitstripreader.data.source.util;

import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.dto.StripDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertTrue;

public class SampleStrip {

    public static StripDaoEntity generateSampleDao() {

        StripDaoEntity stripDao = new StripDaoEntity();
            stripDao.setId(1L);
            stripDao.setTitle("Codeur Bohême");
            stripDao.setIsFavorite(true);
            stripDao.setDate(new Date(1478371005));
            stripDao.setContent("https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Souvenirs-650-final.jpg");
            stripDao.setUrl("http://www.commitstrip.com/fr/2016/10/31/bohemian-coder/?");
            stripDao.setNext(null);
            stripDao.setPrevious(2L);

        return stripDao;
    }

    public static StripDto generateSampleDto() {

        StripDto stripDto = new StripDto();
        stripDto.setId(1L);
        stripDto.setTitle("Codeur Bohême");
        stripDto.setDate(new Date(1478371005));
        stripDto.setContent("https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Souvenirs-650-final.jpg");
        stripDto.setUrl("http://www.commitstrip.com/fr/2016/10/31/bohemian-coder/?");
        stripDto.setNext(null);
        stripDto.setPrevious(2L);

        return stripDto;
    }

    public static List<StripDto> generateSampleDto(long number) {
        List<StripDto> strips = new ArrayList<>();

        for (long i=0; i<number; i++){

            StripDto stripDto = new StripDto();
                stripDto.setId(i);
                stripDto.setTitle("Codeur Bohême "+i);
                stripDto.setDate(new Date(1478371005));
                stripDto.setContent("https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Souvenirs-650-final.jpg");
                stripDto.setUrl("http://www.commitstrip.com/fr/2016/10/31/bohemian-coder/?");
                stripDto.setNext(null);
                stripDto.setPrevious(2L);

            strips.add(stripDto);
        }

        return strips;
    }

    public static void compareEveryPropertiesOfStripDtoVsStripDao (StripDto source, StripDaoEntity other) {

        assertTrue(source.getId().equals(other.getId()));
        assertTrue(source.getTitle().equals(other.getTitle()));
        assertTrue(source.getDate().equals(other.getDate()));
        assertTrue(source.getContent().equals(other.getContent()));
        assertTrue(source.getUrl().equals(other.getUrl()));

        if (source.getNext() != null)
            assertTrue(source.getNext().equals(other.getNext()));
        else
            assertTrue(other.getNext() == null);

        if (source.getPrevious() != null)
            assertTrue(source.getPrevious().equals(other.getPrevious()));
        else
            assertTrue(other.getPrevious() == null);

    }

    public static void compareEveryPropertiesOfStripDto (StripDto source, StripDto other) {

        assertTrue(source.getId().equals(other.getId()));
        assertTrue(source.getTitle().equals(other.getTitle()));
        assertTrue(source.getContent().equals(other.getContent()));
        assertTrue(source.getUrl().equals(other.getUrl()));

        if (source.getNext() != null)
            assertTrue(source.getNext().equals(other.getNext()));
        else
            assertTrue(other.getNext() == null);

        if (source.getPrevious() != null)
            assertTrue(source.getPrevious().equals(other.getPrevious()));
        else
            assertTrue(other.getPrevious() == null);

    }

}
