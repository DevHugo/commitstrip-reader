package com.commitstrip.commistripreader.data.source.local;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.commitstrip.commitstripreader.data.source.local.StripImageCacheDataSource;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.bouncycastle.asn1.dvcs.TargetEtcChain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static java.lang.Thread.sleep;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StripImageCacheDataSourceTest {

    private static StripImageCacheDataSource underTest;

    private static File mInternalDirectory;
    private static File mExternalDirectory;
    private static Picasso mPicasso;

    private Long ID_STRIP = 1L;
    private String URL = "https://placeholdit.imgix.net/~text?txtsize=33&txt=350%C3%97150&w=350&h=150";

    public StripImageCacheDataSourceTest(){}

    @BeforeClass
    public static void setUp ()  throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();

        mPicasso = mock(Picasso.class);
        mExternalDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mInternalDirectory = context.getFilesDir();

        underTest = new StripImageCacheDataSource(mPicasso, mInternalDirectory, mExternalDirectory);
    }

    @Test
    public void getImageCacheForStripWithCorrectIdShouldReturnFile()  throws Exception  {
        File file = underTest.getImageCacheForStrip(ID_STRIP);

        assertTrue (file.getAbsolutePath().endsWith(ID_STRIP + underTest.EXTENSION_FILE_IMAGE));
    }


    @Test(expected = IllegalArgumentException.class)
    public void getImageCacheForStripWithNullShouldReturnIllegalArgumentException() {
        underTest.getImageCacheForStrip(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isImageCacheForStripExistWithNullShouldReturnIllegalArgumentException() {
        underTest.isImageCacheForStripExist(null);
    }

    @Test
    public void isImageCacheForStripExistWithImageExistShouldReturnTrue() throws IOException {
        File cache = new File(mInternalDirectory, ID_STRIP+underTest.EXTENSION_FILE_IMAGE);

        if (cache.exists())
            cache.delete();

        if (!cache.createNewFile())
            fail("Could not create image cache !");

        assertTrue(underTest.isImageCacheForStripExist(ID_STRIP));

    }

    @Test
    public void isImageCacheForStripExistWithNoImageExistShouldReturnFalse() throws IOException {
        File cache = new File(mInternalDirectory, ID_STRIP+underTest.EXTENSION_FILE_IMAGE);

        if (cache.exists())
            cache.delete();

        assertFalse(underTest.isImageCacheForStripExist(ID_STRIP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteImageStripInCacheWithNullShouldReturnIllegalArgumentException() {
        underTest.deleteImageStripInCache(null);
    }

    @Test
    public void deleteImageStripInCacheWithImageExistShouldDeleteImage() throws IOException {
        File cache = new File(mInternalDirectory, ID_STRIP+underTest.EXTENSION_FILE_IMAGE);

        if (!cache.exists())
            if (!cache.createNewFile())
                fail("Could not create image cache !");

        assertTrue(underTest.deleteImageStripInCache(ID_STRIP));
    }

    @Test
    public void deleteImageStripInCacheWithNoImageExistShouldReturnException() throws IOException {
        File cache = new File(mInternalDirectory, ID_STRIP+underTest.EXTENSION_FILE_IMAGE);

        if (!cache.exists())
            cache.createNewFile();

        assertTrue(underTest.deleteImageStripInCache(ID_STRIP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fetchImageStripWithNullShouldReturnIllegalArgumentException() {
        underTest.fetchImageStrip(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveImageStripInCacheWithNullShouldReturnIllegalArgumentException() {
        RequestCreator requestCreator = mock(RequestCreator.class);

        underTest.saveImageStripInCache(null, requestCreator);
    }

    @Test
    public void saveImageStripInCacheWithANonImageExistShouldSave() throws IOException {
        File cache = new File(mInternalDirectory, ID_STRIP+underTest.EXTENSION_FILE_IMAGE);

        if (cache.exists())
            cache.delete();

        Context context = InstrumentationRegistry.getTargetContext();

        RequestCreator requestCreator = Picasso.with(context).load(URL);
        Target target = underTest.getTarget(ID_STRIP);

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(() -> {
            requestCreator.into(target);
        });

        try {
            sleep(3000);
        } catch (InterruptedException e) {}

        assertTrue(cache.exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveImageStripInCacheWithUrlAndNullShouldReturnIllegalArgumentException() {
        underTest.saveImageStripInCache(null, "");
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }

}
