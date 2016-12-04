package com.commitstrip.commitstripreader.service;

import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ResumeSyncLocalDatabaseTest {

    private static String TAG = "SyncLocalDatabaseTest";

    public ResumeSyncLocalDatabaseTest(){}

    @Test
    public void resumeFromLastSyncWithoutAnyFile() throws InterruptedException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File fileToResume = new File (tempDir);

        List<StripDto> strips = SampleStrip.generateSampleDto(1000);

        StripRepository stripRepository = Mockito.mock(StripRepository.class);
        Mockito.when (stripRepository.fetchAllStrip()).thenReturn(Flowable.just(strips));

        ResumeSyncLocalDatabase resumeSyncLocalDatabase = new ResumeSyncLocalDatabase (stripRepository, fileToResume);
        Flowable<StripDto> underTest = resumeSyncLocalDatabase.resumeFromLastSync();

        TestSubscriber<StripDto> test = underTest.test();

        test.assertValueCount(1000);
        test.assertTerminated();
    }

    @Test
    public void resumeFromLastSyncWithFileToResume() throws InterruptedException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File fileToResume = new File (tempDir);

        if (fileToResume.exists())
            fileToResume.delete();

        List<StripDto> strips = SampleStrip.generateSampleDto(1000);

        StripRepository stripRepository = Mockito.mock(StripRepository.class);
        Mockito.when (stripRepository.fetchAllStrip()).thenReturn(Flowable.just(strips));

        ResumeSyncLocalDatabase resumeSyncLocalDatabase = new ResumeSyncLocalDatabase (stripRepository, fileToResume);
        resumeSyncLocalDatabase.saveCurrentProgression(strips);

        Flowable<StripDto> underTest = resumeSyncLocalDatabase.resumeFromLastSync();

        TestSubscriber<StripDto> test = underTest.test();

        test.assertValueCount(1000);
        test.assertTerminated();
    }

    @Test
    public void saveCurrentProgressionWithCurrentProgress() throws InterruptedException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File fileToResume = new File (tempDir);

        if (fileToResume.exists())
            fileToResume.delete();

        List<StripDto> strips = SampleStrip.generateSampleDto(1000);

        StripRepository stripRepository = Mockito.mock(StripRepository.class);
        Mockito.when (stripRepository.fetchAllStrip()).thenReturn(Flowable.just(strips));

        ResumeSyncLocalDatabase resumeSyncLocalDatabase = new ResumeSyncLocalDatabase (stripRepository, fileToResume);
        resumeSyncLocalDatabase.saveCurrentProgression(strips);

        assertTrue(fileToResume.exists());

        String content = "";
        try {
            content = Files.toString(new File(fileToResume, Configuration.FILENAME_CACHE_SYNC_LOCAL_DATABASE), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type listType = new TypeToken<ArrayList<StripDto>>(){}.getType();

        Gson gson = new Gson();
        List<StripDto> list = gson.fromJson(content, listType);

        for (int i=0; i<list.size(); i++) {
            SampleStrip.compareEveryPropertiesOfStripDto(list.get(0), strips.get(0));
        }

    }
}
