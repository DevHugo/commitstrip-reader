package com.commitstrip.commitstripreader.data.source;

import com.commitstrip.commitstripreader.data.source.remote.StripRemoteDataSource;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.listfavorite.ListFavoriteDto;
import com.squareup.picasso.RequestCreator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class StripRepositoryTest {

    @Test
    public void fetchStripWithLocalStripAndNoNetworkShouldReturnLocalStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        StripDto strip = SampleStrip.generateSampleDto();
        when(stripLocalDataSource.fetchStrip(1L)).thenReturn(Single.just(strip));
        when(stripRemoteDataSource.fetchStrip(1L)).thenReturn(Single.error(new RuntimeException()));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        TestObserver testObserver = underTest.fetchStrip(1L).test();

        testObserver.assertValueCount(1);
        testObserver.assertValue(strip);
        testObserver.assertComplete();
    }

    @Test
    public void fetchStripWithNoLocalStripAndNetworkShouldReturnLocalStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        StripDto strip = SampleStrip.generateSampleDto();
        when(stripRemoteDataSource.fetchStrip(1L)).thenReturn(Single.just(strip));
        when(stripLocalDataSource.fetchStrip(1L)).thenReturn(Single.error(new RuntimeException()));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        TestObserver testObserver = underTest.fetchStrip(1L).test();

        testObserver.assertValueCount(1);
        testObserver.assertValue(strip);
        testObserver.assertComplete();
    }

    @Test
    public void fetchStripWithNoLocalStripAndNoNetworkShouldReturnError() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        StripDto strip = SampleStrip.generateSampleDto();
        when(stripRemoteDataSource.fetchStrip(1L)).thenReturn(Single.error(new RuntimeException()));
        when(stripLocalDataSource.fetchStrip(1L)).thenReturn(Single.error(new RuntimeException()));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        TestObserver testObserver = underTest.fetchStrip(1L).test();

        testObserver.assertValueCount(0);
        testObserver.assertError(RuntimeException.class);
        testObserver.assertTerminated();
    }

    @Test
    public void fetchStripWithLocalStripAndNetworkShouldReturnLocalStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        StripDto strip = SampleStrip.generateSampleDto();
        when(stripRemoteDataSource.fetchStrip(1L)).thenReturn(Single.just(strip));
        when(stripLocalDataSource.fetchStrip(1L)).thenReturn(Single.just(strip));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        TestObserver testObserver = underTest.fetchStrip(1L).test();

        testObserver.assertValueCount(1);
        testObserver.assertValue(strip);
        testObserver.assertComplete();
    }

    @Test
    public void fetchImageStripWithLocalStripShouldReturnStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(true);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        underTest.fetchImageStrip(1L, "");

        verify(stripImageCacheDataSource, times(1)).fetchImageStrip(1L);
    }

    @Test
    public void fetchImageStripWithNoLocalStripShouldReturnStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(false);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        underTest.fetchImageStrip(1L, "");

        verify(stripRemoteDataSource, times(1)).fetchImageStrip("");
    }

    @Test
    public void isFavorite() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(true);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        underTest.isFavorite(1L);

        verify(stripLocalDataSource, times(1)).isFavorite(1L);
    }

    @Test
    public void addFavoriteWithExistingImageShouldAddFavorite() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(true);
        when(stripLocalDataSource.addFavorite(stripDto)).thenReturn(Single.just(stripDto));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        TestObserver testObserver = underTest.addFavorite(stripDto).test();

        verify(stripLocalDataSource, times(1)).addFavorite(stripDto);

        testObserver.assertValueCount(1);
        testObserver.assertValue(stripDto);
        testObserver.assertComplete();
    }

    @Test
    public void addFavoriteWithNoExistingImageShouldAddFavoriteAndSaveImage() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);
        RequestCreator requestCreator = mock(RequestCreator.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(false);
        when(stripLocalDataSource.addFavorite(stripDto)).thenReturn(Single.just(stripDto));
        when(stripRemoteDataSource.fetchImageStrip(stripDto.getContent())).thenReturn(requestCreator);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        TestObserver testObserver = underTest.addFavorite(stripDto).test();

        verify(stripLocalDataSource, times(1)).addFavorite(stripDto);
        verify(stripRemoteDataSource, times(1)).fetchImageStrip(stripDto.getContent());
        verify(stripImageCacheDataSource, times(1)).saveImageStripInCache(stripDto.getId(), requestCreator);

        testObserver.assertValueCount(1);
        testObserver.assertValue(stripDto);
        testObserver.assertComplete();
    }

    @Test
    public void deleteFavoriteWithExistingImageShouldDeleteFavoriteAndDeleteImage() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(true);
        when(stripLocalDataSource.deleteFavorite(stripDto)).thenReturn(Single.just(stripDto));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        TestObserver testObserver = underTest.deleteFavorite(stripDto).test();

        verify(stripLocalDataSource, times(1)).deleteFavorite(stripDto);
        verify(stripImageCacheDataSource, times(1)).isImageCacheForStripExist(stripDto.getId());
        verify(stripImageCacheDataSource, times(1)).deleteImageStripInCache(stripDto.getId());

        testObserver.assertValueCount(1);
        testObserver.assertValue(stripDto);
        testObserver.assertComplete();
    }

    @Test
    public void deleteFavoriteWithNoExistingImageShouldDeleteFavorite() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(false);
        when(stripLocalDataSource.deleteFavorite(stripDto)).thenReturn(Single.just(stripDto));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        TestObserver testObserver = underTest.deleteFavorite(stripDto).test();

        verify(stripLocalDataSource, times(1)).deleteFavorite(stripDto);

        testObserver.assertValueCount(1);
        testObserver.assertValue(stripDto);
        testObserver.assertComplete();
    }

    @Test
    public void fetchFavoriteWithNoStripShouldReturnEmpty() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        when(stripLocalDataSource.fetchFavoriteStrip()).thenReturn(Flowable.empty());

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        TestSubscriber<ListFavoriteDto> testObserver = underTest.fetchFavoriteStrip().test();

        testObserver.assertValueCount(0);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
    }

    @Test
    public void fetchFavoriteWithStripShouldReturnList() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        when(stripLocalDataSource.fetchFavoriteStrip()).thenReturn(Flowable.just(stripDto));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        TestSubscriber<ListFavoriteDto> testObserver = underTest.fetchFavoriteStrip().test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
    }

    @Test
    public void fetchNextFavoriteStrip () {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        underTest.fetchNextFavoriteStrip(stripDto.getDate());

        verify(stripLocalDataSource, times(1)).fetchNextFavoriteStrip(stripDto.getDate());
    }

    @Test
    public void fetchPreviousFavorite () {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        underTest.fetchPreviousFavoriteStrip(stripDto.getDate());

        verify(stripLocalDataSource, times(1)).fetchPreviousFavoriteStrip(stripDto.getDate());
    }

    @Test
    public void saveImageStripInCache () {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        underTest.saveImageStripInCache(stripDto.getId(), stripDto.getContent());

        verify(stripImageCacheDataSource, times(1)).saveImageStripInCache(stripDto.getId(), stripDto.getContent());
    }

    @Test
    public void fetchAllStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        underTest.fetchAllStrip();

        verify(stripRemoteDataSource, times(1)).fetchAllStrip();
    }

    @Test
    public void saveStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        List<StripDto> strips = new ArrayList<>();

        underTest.saveStrips(strips);

        verify(stripLocalDataSource, times(1)).saveStrip(strips);
    }

    @Test
    public void existStripInCache() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(StripDataSource.StripImageCacheDataSource.class);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource, stripImageCacheDataSource);

        underTest.existStripInCache(stripDto.getId());

        verify(stripLocalDataSource, times(1)).existStrip(stripDto.getId());

    }

}
