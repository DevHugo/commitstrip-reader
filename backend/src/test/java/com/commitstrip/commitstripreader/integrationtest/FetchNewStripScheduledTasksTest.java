package com.commitstrip.commitstripreader.integrationtest;

import com.commitstrip.commitstripreader.backend.config.Configuration;
import com.commitstrip.commitstripreader.backend.config.SampleConfig;
import com.commitstrip.commitstripreader.backend.converter.StripDaoToSimpleStripDto;
import com.commitstrip.commitstripreader.backend.dao.StripDao;
import com.commitstrip.commitstripreader.backend.repository.CommitStripRepositoryImpl;
import com.commitstrip.commitstripreader.backend.repository.DatabaseRepository;
import com.commitstrip.commitstripreader.backend.schedule.FetchNewStripScheduledTasks;
import com.commitstrip.commitstripreader.integrationtest.util.SampleData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.atLeast;

@RunWith(SpringJUnit4ClassRunner.class)
public class FetchNewStripScheduledTasksTest {

    @Mock
    private DatabaseRepository databaseRepositoryMock;

    @Spy
    private CommitStripRepositoryImpl commitStripRepository;

    @Mock
    private FetchNewStripScheduledTasks classUnderTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Configuration.shouldStartToFetchNewStrip = true;
        Configuration.shouldSentNotification = false;
    }

    @Test
    public void fetchOneNewStripTest() throws IOException, ParseException {

        List<StripDao> strips = new ArrayList<>();
            StripDao stripDao = new StripDao();
            stripDao.setId(11L);
            stripDao.setTitle("Hello there !");
            stripDao.setContent("http://www.commitstrip.com/fr/2016/10/24/meanwhile-on-mars-11/");
            stripDao.setDate(new Date());
        strips.add(stripDao);

        Mockito.when(commitStripRepository.fetchStripFromCommitStripOnPage(1)).thenReturn(strips);

        StripDao stubStripDao = new StripDao();
        stubStripDao.setId(Long.valueOf(10));
        Mockito.when(databaseRepositoryMock.findFirst1ByOrderByIdDesc()).thenReturn(stubStripDao);

        StripDao stubStripDao2 = new StripDao();
        stubStripDao2.setId(Long.valueOf(1));
        Mockito.when(databaseRepositoryMock.findFirst1ByOrderByDateDesc()).thenReturn(stubStripDao2);

        StripDaoToSimpleStripDto converter = new StripDaoToSimpleStripDto();
        SampleConfig config = new SampleConfig();

        classUnderTest = new FetchNewStripScheduledTasks(commitStripRepository, databaseRepositoryMock, config, converter);
        List<StripDao> toCheck = classUnderTest.fetchNewStrip();

        assert (toCheck.get(0).getId() == 11);
        assert (toCheck.get(0).getPrevious() == 1);
        assert (toCheck.get(0).getNext() == 0);

    }

    @Test
    public void fetchTwoNewStripTest() throws IOException, ParseException {

        List<StripDao> strips = SampleData.addSampleStrips(2);

        Mockito.when(commitStripRepository.fetchStripFromCommitStripOnPage(1)).thenReturn(strips);

        StripDao stubStripDao = new StripDao();
            stubStripDao.setId(Long.valueOf(10));
            stubStripDao.setTitle("Hello there !");
            stubStripDao.setContent("http://www.commitstrip.com/fr/2016/10/24/meanwhile-on-mars-11/");
        Mockito.when(databaseRepositoryMock.findFirst1ByOrderByIdDesc()).thenReturn(stubStripDao);

        StripDao stubStripDao2 = new StripDao();
            stubStripDao2.setTitle("Hello there 2 !");
            stubStripDao2.setContent("http://www.commitstrip.com/fr/2016/10/24/meanwhile-on-mars-11/");
            stubStripDao2.setId(Long.valueOf(1));
        Mockito.when(databaseRepositoryMock.findFirst1ByOrderByDateDesc()).thenReturn(stubStripDao2);

        Mockito.when(databaseRepositoryMock.save((List<StripDao>) anyObject())).thenReturn(strips);

        StripDaoToSimpleStripDto converter = new StripDaoToSimpleStripDto();
        SampleConfig config = new SampleConfig();

        classUnderTest = new FetchNewStripScheduledTasks(commitStripRepository, databaseRepositoryMock, config, converter);
        List<StripDao> toCheck = classUnderTest.fetchNewStrip();

        assert (toCheck.get(0).getId() == 11);
        assert (toCheck.get(0).getPrevious() == 12);
        assert (toCheck.get(0).getNext() == 0);

        assert (toCheck.get(1).getId() == 12);
        assert (toCheck.get(1).getPrevious() == 1);
        assert (toCheck.get(1).getNext() == 11);
    }
}
