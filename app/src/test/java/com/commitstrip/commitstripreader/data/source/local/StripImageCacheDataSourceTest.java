package com.commitstrip.commitstripreader.data.source.local;

import static junit.framework.Assert.fail;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.commitstrip.commitstripreader.BuildConfig;
import com.commitstrip.commitstripreader.util.RobolectricStripImageDataSourceRule;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Unit tests for the implementation of {@link StripImageCacheDataSource}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StripImageCacheDataSourceTest {

    @Rule
    public RobolectricStripImageDataSourceRule mStripImageDataSourceRule =
            new RobolectricStripImageDataSourceRule();

    private Long ID_STRIP = 1L;
    private String URL = "https://placeholdit.imgix.net/~text?txtsize=33&txt=350%C3%97150&w=350&h=150";

    private Target target;

    public StripImageCacheDataSourceTest(){
        super();
    }

    @Test
    public void getImageCacheForStripWithCorrectIdShouldReturnFile()  throws Exception  {
        StripImageCacheDataSource underTest =
                mStripImageDataSourceRule.getStripImageCacheDataSource();

        File file = underTest.getImageCacheForStrip(ID_STRIP);

        Assert.assertTrue (file.getAbsolutePath().endsWith(ID_STRIP + underTest.EXTENSION_FILE_IMAGE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getImageCacheForStripWithNullShouldReturnIllegalArgumentException() {
        StripImageCacheDataSource underTest =
                mStripImageDataSourceRule.getStripImageCacheDataSource();

        underTest.getImageCacheForStrip(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isImageCacheForStripExistWithNullShouldReturnIllegalArgumentException() {
        StripImageCacheDataSource underTest =
                mStripImageDataSourceRule.getStripImageCacheDataSource();

        underTest.isImageCacheForStripExist(null);
    }

    @Test
    public void isImageCacheForStripExistWithImageExistShouldReturnTrue() throws IOException {
        StripImageCacheDataSource underTest =
                mStripImageDataSourceRule.getStripImageCacheDataSource();

        File internalDirectory = mStripImageDataSourceRule.getInternalDirectory();

        File cache = new File(internalDirectory,
                underTest.PREFIXE_IMAGE + ID_STRIP + underTest.EXTENSION_FILE_IMAGE);

        if (cache.exists())
            cache.delete();

        if (!cache.createNewFile())
            fail("Could not create image cache !");

        Assert.assertTrue(underTest.isImageCacheForStripExist(ID_STRIP));
    }

    @Test
    public void isImageCacheForStripExistWithNoImageExistShouldReturnFalse() throws IOException {
        StripImageCacheDataSource underTest =
                mStripImageDataSourceRule.getStripImageCacheDataSource();

        File internalDirectory = mStripImageDataSourceRule.getInternalDirectory();

        File cache = new File(internalDirectory, ID_STRIP+underTest.EXTENSION_FILE_IMAGE);

        if (cache.exists())
            cache.delete();

        Assert.assertFalse(underTest.isImageCacheForStripExist(ID_STRIP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteImageStripInCacheWithNullShouldReturnIllegalArgumentException() {
        StripImageCacheDataSource underTest =
                mStripImageDataSourceRule.getStripImageCacheDataSource();

        underTest.deleteImageStripInCache(null);
    }

    @Test
    public void deleteImageStripInCacheWithImageExistShouldDeleteImage() throws IOException {
        StripImageCacheDataSource underTest =
                mStripImageDataSourceRule.getStripImageCacheDataSource();

        File internalDirectory = mStripImageDataSourceRule.getInternalDirectory();

        File cache = new File(internalDirectory,
                underTest.PREFIXE_IMAGE + ID_STRIP+underTest.EXTENSION_FILE_IMAGE);

        if (!cache.exists())
            if (!cache.createNewFile())
                fail("Could not create image cache !");

        Assert.assertTrue(underTest.deleteImageStripInCache(ID_STRIP));
    }

    @Test
    public void deleteImageStripInCacheWithNoImageExistShouldReturnException() throws IOException {
        StripImageCacheDataSource underTest =
                mStripImageDataSourceRule.getStripImageCacheDataSource();

        File internalDirectory = mStripImageDataSourceRule.getInternalDirectory();

        File cache = new File(internalDirectory, underTest.PREFIXE_IMAGE + ID_STRIP+underTest.EXTENSION_FILE_IMAGE);

        if (!cache.exists())
            cache.createNewFile();

        Assert.assertTrue(underTest.deleteImageStripInCache(ID_STRIP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fetchImageStripWithNullShouldReturnIllegalArgumentException() {
        StripImageCacheDataSource underTest =
                mStripImageDataSourceRule.getStripImageCacheDataSource();

        underTest.fetchImageStrip(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveImageStripInCacheWithNullShouldReturnIllegalArgumentException() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StripImageCacheDataSource underTest =
                mStripImageDataSourceRule.getStripImageCacheDataSource();

        underTest.saveImageStripInCache(null, byteArrayOutputStream);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveImageStripInCacheWithUrlAndNullShouldReturnIllegalArgumentException() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StripImageCacheDataSource underTest =
                mStripImageDataSourceRule.getStripImageCacheDataSource();

        underTest.saveImageStripInCache(null, byteArrayOutputStream);
    }

}
