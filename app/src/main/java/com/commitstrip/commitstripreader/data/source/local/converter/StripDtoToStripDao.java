package com.commitstrip.commitstripreader.data.source.local.converter;

import com.commitstrip.commitstripreader.data.source.local.StripDao;
import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.dto.StripDto;

import io.reactivex.functions.Function;


public class StripDtoToStripDao implements Function<StripDto, StripDaoEntity> {

    @Override
    public StripDaoEntity apply(StripDto source) {
        StripDaoEntity stripDao = new StripDaoEntity();
        stripDao.setId(source.getId());
        stripDao.setTitle(source.getTitle());
        stripDao.setContent(source.getContent());
        stripDao.setReleaseDate(source.getReleaseDate());
        stripDao.setThumbnail(source.getThumbnail());
        stripDao.setUrl(source.getUrl());
        stripDao.setPrevious(source.getPrevious());
        stripDao.setNext(source.getNext());

        return stripDao;
    }
}
