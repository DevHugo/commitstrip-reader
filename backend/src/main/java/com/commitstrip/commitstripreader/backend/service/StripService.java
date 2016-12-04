package com.commitstrip.commitstripreader.backend.service;

import com.commitstrip.commitstripreader.dto.StripDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public interface StripService {

    Iterable<StripDto> fetchAllStripFromCommitStripAndStoreThem() throws IOException, ParseException;

    Page<StripDto> findAll(Pageable pageable);

    StripDto findOne(Long id);

    StripDto findMoreRecent();
}
