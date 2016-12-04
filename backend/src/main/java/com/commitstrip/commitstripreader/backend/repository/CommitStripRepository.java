package com.commitstrip.commitstripreader.backend.repository;

import com.commitstrip.commitstripreader.backend.dao.StripDao;

import java.io.IOException;
import java.text.ParseException;

public interface CommitStripRepository {

    Integer fetchPageNumber () throws IOException;

    Iterable<StripDao> fetchStripFromCommitStripOnPage(Integer page) throws IOException, ParseException;
}
