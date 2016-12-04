package com.commitstrip.commitstripreader.backend.service;

import com.commitstrip.commitstripreader.backend.converter.StripDaoToStrip;
import com.commitstrip.commitstripreader.backend.dao.StripDao;
import com.commitstrip.commitstripreader.backend.repository.CommitStripRepository;
import com.commitstrip.commitstripreader.backend.repository.DatabaseRepository;
import com.commitstrip.commitstripreader.dto.StripDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StripServiceImpl implements StripService {

    @Autowired
    private StripDaoToStrip converterStrip;

    @Autowired
    private CommitStripRepository repositoryCommitStrip;

    @Autowired
    private DatabaseRepository repositoryDatabase;

    @Autowired
    private Environment environment;

    private static final Logger log = LoggerFactory.getLogger(StripServiceImpl.class);

    @Autowired
    public StripServiceImpl(StripDaoToStrip converterStrip, CommitStripRepository repositoryCommitStrip, DatabaseRepository repositoryDatabase) {
        this.converterStrip = converterStrip;
        this.repositoryCommitStrip = repositoryCommitStrip;
        this.repositoryDatabase = repositoryDatabase;
    }

    public Iterable<StripDto> fetchAllStripFromCommitStripAndStoreThem() throws IOException, ParseException {

        List<StripDto> toReturn = new ArrayList<>();

        int numberPageToFetch = 5;
        if (isInProduction())
            numberPageToFetch = repositoryCommitStrip.fetchPageNumber();

        for (int i=1; i <= numberPageToFetch; i++) {
            Iterable<StripDao> strips = repositoryCommitStrip.fetchStripFromCommitStripOnPage(i);

            repositoryDatabase.save(strips);

            for (StripDao strip : strips)
                toReturn.add(converterStrip.convert(strip));

            log.info("Save data from page "+i);
        }

        return toReturn;
    }

    @Override
    public Page<StripDto> findAll(Pageable pageable) {
        Page<StripDao> strips = repositoryDatabase.findAll(pageable);

        return strips.map(new StripDaoToStrip());
    }

    @Override
    public StripDto findOne(Long id) {
        StripDao strip = repositoryDatabase.findOne(id);

        if (strip != null) {
            return new StripDaoToStrip().convert(strip);
        }

        return null;
    }

    @Override
    public StripDto findMoreRecent() {
        StripDao strip = repositoryDatabase.findFirst1ByOrderByDateDesc();

        if (strip != null) {
            return new StripDaoToStrip().convert(strip);
        }

        return null;
    }

    private boolean isInProduction () {
        boolean isInProduction = false;

        for (String profile : environment.getActiveProfiles()) {

            if (profile.compareTo("Prod") == 0 ||
                    profile.compareTo("PROD") == 0 ||
                    profile.compareTo("Production") == 0 ||
                    profile.compareTo("PRODUCTION") == 0 ||
                    profile.compareTo("prod") == 0)
                isInProduction = true;
        }

        return isInProduction;
    }

}
