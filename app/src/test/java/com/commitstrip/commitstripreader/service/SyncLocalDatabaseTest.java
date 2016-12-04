package com.commitstrip.commitstripreader.service;

import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.data.source.local.converter.ListStripDtoToListStripDao;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

@RunWith(JUnit4.class)
public class SyncLocalDatabaseTest {

    @Test
    public void syncLocalDatabase() throws InterruptedException {

        List<StripDto> strips = SampleStrip.generateSampleDto(1001);
        Flowable<Iterable<StripDaoEntity>> shouldReturnAfterSave =
                Flowable
                    .just(strips)
                    .map(new ListStripDtoToListStripDao());

        Flowable<StripDto> shouldSave =
                Flowable
                        .just(strips)
                        .flatMap(new Function<List<StripDto>, Flowable<StripDto>>() {
                            @Override
                            public Flowable<StripDto> apply(List<StripDto> strips) throws Exception {
                                return Flowable.fromIterable(strips);
                            }
                        });

        StripRepository stripRepository = Mockito.mock(StripRepository.class);
        Mockito.when (stripRepository.existStripInCache(Mockito.anyLong()))
                .thenReturn(false);
        Mockito.when (stripRepository.saveStrips(Mockito.anyList()))
                .thenReturn(shouldReturnAfterSave);

        SyncLocalDatabaseTask syncLocalDatabase = new SyncLocalDatabaseTask(stripRepository);
        Flowable<StripDto> underTest = syncLocalDatabase.execute(shouldSave);

        underTest.blockingSubscribe(
                strip -> {},
                error -> { error.printStackTrace(); throw new AssertionError(); },
                () -> {
                    Mockito.verify(stripRepository, Mockito.times(1001)).existStripInCache(Mockito.anyLong());
                    Mockito.verify(stripRepository, Mockito.times(51)).saveStrips(Mockito.anyList());
                }
        );
    }

}
