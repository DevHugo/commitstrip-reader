package com.commitstrip.commitstripreader.data.source;

import android.content.SharedPreferences;

import com.commitstrip.commitstripreader.data.source.StripDataSource
        .StripSharedPreferencesDataSource;
import com.commitstrip.commitstripreader.data.source.remote.StripRemoteDataSource;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.common.dto.DisplayStripDto;
import com.squareup.picasso.RequestCreator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
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
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        StripDto strip = SampleStrip.generateSampleDto();
        when(stripLocalDataSource.fetchStrip(1L)).thenReturn(Maybe.just(strip));
        when(stripRemoteDataSource.fetchStrip(1L)).thenReturn(Maybe.error(new RuntimeException()));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        TestObserver testObserver = underTest.fetchStrip(1L, false).test();

        testObserver.assertValueCount(1);
        testObserver.assertValue(strip);
        testObserver.assertComplete();
    }

    @Test
    public void fetchStripWithNoLocalStripAndNetworkShouldReturnLocalStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        StripDto strip = SampleStrip.generateSampleDto();
        when(stripRemoteDataSource.fetchStrip(1L)).thenReturn(Maybe.just(strip));
        when(stripLocalDataSource.fetchStrip(1L)).thenReturn(Maybe.error(new RuntimeException()));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        TestObserver testObserver = underTest.fetchStrip(1L, false).test();

        testObserver.assertValueCount(1);
        testObserver.assertValue(strip);
        testObserver.assertComplete();
    }

    @Test
    public void fetchStripWithNoLocalStripAndNoNetworkShouldReturnError() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        StripDto strip = SampleStrip.generateSampleDto();
        when(stripRemoteDataSource.fetchStrip(1L)).thenReturn(Maybe.error(new RuntimeException()));
        when(stripLocalDataSource.fetchStrip(1L)).thenReturn(Maybe.error(new RuntimeException()));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        TestObserver testObserver = underTest.fetchStrip(1L, false).test();

        testObserver.assertValueCount(0);
        testObserver.assertError(RuntimeException.class);
        testObserver.assertTerminated();
    }

    @Test
    public void fetchStripWithLocalStripAndNetworkShouldReturnLocalStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        StripDto strip = SampleStrip.generateSampleDto();
        when(stripRemoteDataSource.fetchStrip(1L)).thenReturn(Maybe.just(strip));
        when(stripLocalDataSource.fetchStrip(1L)).thenReturn(Maybe.just(strip));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        TestObserver testObserver = underTest.fetchStrip(1L, false).test();

        testObserver.assertValueCount(1);
        testObserver.assertValue(strip);
        testObserver.assertComplete();
    }

    @Test
    public void fetchImageStripWithLocalStripShouldReturnStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(true);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        underTest.fetchImageStrip(1L, "");

        verify(stripImageCacheDataSource, times(1)).fetchImageStrip(1L);
    }

    @Test
    public void fetchImageStripWithNoLocalStripShouldReturnStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(false);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        underTest.fetchImageStrip(1L, "");

        verify(stripRemoteDataSource, times(1)).fetchImageStrip("");
    }

    @Test
    public void isFavorite() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(true);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        underTest.isFavorite(1L);

        verify(stripLocalDataSource, times(1)).isFavorite(1L);
    }

    @Test
    public void addFavoriteWithExistingImageShouldAddFavorite() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(true);
        when(stripLocalDataSource.addFavorite(stripDto)).thenReturn(Single.just(stripDto));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TestObserver testObserver = underTest.addFavorite(stripDto, byteArrayOutputStream).test();

        verify(stripLocalDataSource, times(1)).addFavorite(stripDto);

        testObserver.assertValueCount(1);
        testObserver.assertValue(stripDto);
        testObserver.assertComplete();
    }

    @Test
    public void addFavoriteWithNoExistingImageShouldAddFavoriteAndSaveImage() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        RequestCreator requestCreator = mock(RequestCreator.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(false);
        when(stripLocalDataSource.addFavorite(stripDto)).thenReturn(Single.just(stripDto));
        when(stripRemoteDataSource.fetchImageStrip(stripDto.getContent())).thenReturn(requestCreator);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TestObserver testObserver = underTest.addFavorite(stripDto, byteArrayOutputStream).test();

        verify(stripLocalDataSource, times(1)).addFavorite(stripDto);
        verify(stripRemoteDataSource, times(1)).fetchImageStrip(stripDto.getContent());
        verify(stripImageCacheDataSource, times(1)).saveImageStripInCache(stripDto.getId(),
                byteArrayOutputStream);

        testObserver.assertValueCount(1);
        testObserver.assertValue(stripDto);
        testObserver.assertComplete();
    }

    @Test
    public void deleteFavoriteWithExistingImageShouldDeleteFavoriteAndDeleteImage() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(true);
        when(stripLocalDataSource.deleteFavorite(stripDto.getId())).thenReturn(Maybe.just(1));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        TestObserver testObserver = underTest.deleteFavorite(stripDto.getId()).test();

        verify(stripLocalDataSource, times(1)).deleteFavorite(stripDto.getId());
        verify(stripImageCacheDataSource, times(1)).isImageCacheForStripExist(stripDto.getId());
        verify(stripImageCacheDataSource, times(1)).deleteImageStripInCache(stripDto.getId());

        testObserver.assertValueCount(1);
        testObserver.assertValue(1);
        testObserver.assertComplete();
    }

    @Test
    public void deleteFavoriteWithNoExistingImageShouldDeleteFavorite() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        when(stripImageCacheDataSource.isImageCacheForStripExist(1L)).thenReturn(false);
        when(stripLocalDataSource.deleteFavorite(stripDto.getId())).thenReturn(Maybe.just(1));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        TestObserver testObserver = underTest.deleteFavorite(stripDto.getId()).test();

        verify(stripLocalDataSource, times(1)).deleteFavorite(stripDto.getId());

        testObserver.assertValueCount(1);
        testObserver.assertValue(1);
        testObserver.assertComplete();
    }

    @Test
    public void fetchFavoriteWithNoStripShouldReturnEmpty() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        when(stripLocalDataSource.fetchFavoriteStrip()).thenReturn(Flowable.empty());

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        TestSubscriber<DisplayStripDto> testObserver = underTest.fetchFavoriteStrip().test();

        testObserver.assertValueCount(0);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
    }

    @Test
    public void fetchFavoriteWithStripShouldReturnList() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        when(stripLocalDataSource.fetchFavoriteStrip()).thenReturn(Flowable.just(stripDto));

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        TestSubscriber<DisplayStripDto> testObserver = underTest.fetchFavoriteStrip().test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertComplete();
    }

    @Test
    public void fetchNextFavoriteStrip() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        underTest.fetchNextFavoriteStrip(stripDto.getReleaseDate());

        verify(stripLocalDataSource, times(1)).fetchNextFavoriteStrip(stripDto.getReleaseDate());
    }

    @Test
    public void fetchPreviousFavorite() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        underTest.fetchPreviousFavoriteStrip(stripDto.getReleaseDate());

        verify(stripLocalDataSource, times(1)).fetchPreviousFavoriteStrip(stripDto.getReleaseDate());
    }

    @Test
    public void saveImageStripInCache() {

        StripDto stripDto = SampleStrip.generateSampleDto();

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        underTest.saveImageStripInCache(stripDto.getId(), stripDto.getContent());

        verify(stripImageCacheDataSource, times(1)).saveImageStripInCache(stripDto.getId(),
                new ByteArrayOutputStream());
    }

    @Test
    public void fetchAllStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        underTest.fetchAllStrip();

        verify(stripRemoteDataSource, times(1)).fetchAllStrip();
    }

    @Test
    public void upsertStrip() {

        StripDataSource.RemoteDataSource stripRemoteDataSource = mock(StripRemoteDataSource.class);
        StripDataSource.LocalDataSource stripLocalDataSource = mock(
                StripDataSource.LocalDataSource.class);
        StripDataSource.StripImageCacheDataSource stripImageCacheDataSource = mock(
                StripDataSource.StripImageCacheDataSource.class);
        StripSharedPreferencesDataSource sharedPreferences = mock(StripSharedPreferencesDataSource.class);

        StripRepository underTest = new StripRepository(stripRemoteDataSource, stripLocalDataSource,
                stripImageCacheDataSource, sharedPreferences);

        List<StripDto> strips = new ArrayList<>();

        underTest.upsertStrip(strips);

        verify(stripLocalDataSource, times(1)).upsertStrip(strips);
    }

}
