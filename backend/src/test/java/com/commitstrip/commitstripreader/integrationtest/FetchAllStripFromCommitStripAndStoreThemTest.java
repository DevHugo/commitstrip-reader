package com.commitstrip.commitstripreader.integrationtest;

import com.commitstrip.commitstripreader.backend.converter.StripDaoToStrip;
import com.commitstrip.commitstripreader.backend.dao.StripDao;
import com.commitstrip.commitstripreader.backend.repository.CommitStripRepository;
import com.commitstrip.commitstripreader.backend.repository.CommitStripRepositoryImpl;
import com.commitstrip.commitstripreader.backend.repository.DatabaseRepository;
import com.commitstrip.commitstripreader.backend.service.DownloadFile;
import com.commitstrip.commitstripreader.backend.service.StripService;
import com.commitstrip.commitstripreader.backend.service.StripServiceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.atLeast;

@RunWith(SpringJUnit4ClassRunner.class)
public class FetchAllStripFromCommitStripAndStoreThemTest {

    @Mock
    private DatabaseRepository databaseRepositoryMock;

    @Spy
    private CommitStripRepositoryImpl commitStripRepository;

    @Mock
    private StripDaoToStrip converter;

    @Mock
    private StripService classUnderTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fetchAllTest() throws IOException, ParseException {

        // Limit the number of page fetched don't want to crawl all the CommitStrip website :)
        Mockito.when(commitStripRepository.fetchPageNumber()).thenReturn(4);
        Mockito.when(commitStripRepository.fetchStripFromCommitStripOnPage(anyInt())).thenCallRealMethod();

        classUnderTest = new StripServiceImpl(converter, commitStripRepository, databaseRepositoryMock, null);
        classUnderTest.fetchAllStripFromCommitStripAndStoreThem();

        Mockito.verify(databaseRepositoryMock, atLeast(3)).save((List<StripDao>) anyObject());
    }
}
