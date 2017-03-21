package com.commitstrip.commitstripreader.data.source.local;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.StripDataSource;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.util.ImageUtils;
import com.commitstrip.commitstripreader.util.di.CacheStorage;
import com.commitstrip.commitstripreader.util.di.ExternalStorage;
import com.commitstrip.commitstripreader.util.di.InternalStorage;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.commitstrip.commitstripreader.util.Storage.isExternalStorageWritable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

/**
 * Helper to save file image on disk.
 */
@Singleton
public class StripImageCacheDataSource implements StripDataSource.StripImageCacheDataSource {

    private static String TAG = "StripImageCache";

    public String PREFIXE_IMAGE = "StripImageCache_";
    public String EXTENSION_FILE_IMAGE = ".jpg";

    private final Picasso mPicasso;
    private final File mStorage;
    private final File mCacheDir;
    private final ImageUtils mImageUtils;

    @Inject
    public StripImageCacheDataSource(Picasso picasso,
            @InternalStorage File internalStorage,
            @ExternalStorage File externalStorage,
            @CacheStorage File cacheDir,
            ImageUtils imageUtils) {
        mPicasso = picasso;

        File storage = internalStorage;
        if (externalStorage != null) {
            if (isExternalStorageWritable()) {
                storage = new File(externalStorage, Configuration.FOLDER_NAME_IMAGE);

                if (!storage.mkdirs()) {
                    storage = internalStorage;
                }
            }
        }

        mStorage = storage;

        mCacheDir = cacheDir;

        mImageUtils = imageUtils;
    }

    /* (no-Javadoc) */
    @Override
    public File getImageCacheForStrip(@NonNull Long id) {

        if (id == null) {
            throw new IllegalArgumentException();
        }

        return new File(mStorage, PREFIXE_IMAGE + id + EXTENSION_FILE_IMAGE);
    }

    /* (no-Javadoc) */
    @Override
    public boolean isImageCacheForStripExist(@NonNull Long id) {

        if (id == null) {
            throw new IllegalArgumentException();
        }

        return new File(mStorage, PREFIXE_IMAGE + id + EXTENSION_FILE_IMAGE).exists();
    }

    /* (no-Javadoc) */
    @Override
    public boolean deleteImageStripInCache(@NonNull Long id) {

        if (id == null) {
            throw new IllegalArgumentException();
        }

        File file = getImageCacheForStrip(id);

        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /* (no-Javadoc) */
    @Override
    public RequestCreator fetchImageStrip(@NonNull Long id) {

        if (id == null) {
            throw new IllegalArgumentException();
        }

        File file = getImageCacheForStrip(id);

        return mPicasso
                .load(file)
                .networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_STORE);
    }

    /* (no-Javadoc) */
    @Override
    public void saveImageStripInCache(
            @NonNull Long id,
            @NonNull RequestCreator requestCreator,
            int compression) {

        if (id == null && requestCreator == null) {
            throw new IllegalArgumentException();
        }
        final Target target = getTarget(id, compression);
        requestCreator.into(target);
    }

    /* (no-Javadoc) */
    @Override
    public Flowable<Long> saveImageStripInCache(@NonNull Long id, @NonNull String url, int compression) {

        if (id == null && url == null) {
            throw new IllegalArgumentException();
        }

        return Flowable
                .defer(() -> {

                    OkHttpClient httpClient = new OkHttpClient();

                    Request request = new Request.Builder().url(url).build();
                    Response response = httpClient.newCall(request).execute();

                    return Flowable.just(response);
                })
                .filter(Response::isSuccessful)
                .retry(3)
                .doOnNext(response -> {
                    File downloadedFile = getImageCacheForStrip(id);

                    BufferedSink sink = null;
                    try {
                        sink = Okio.buffer(Okio.sink(downloadedFile));
                        sink.writeAll(response.body().source());
                    }
                    finally {
                        if (sink != null) {
                            try {
                                sink.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                })
                .doOnError(throwable -> {
                    if (isImageCacheForStripExist(id)) {
                        deleteImageStripInCache(id);
                    }
                })
                .map(response -> id);
    }

    /* (no-Javadoc) */
    @Override
    public List<Long> getCachedImagesId() {

        List<Long> listId = new ArrayList<>();

        File[] listOfFiles = mStorage.listFiles();

        String fileName, id;
        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {
                fileName = listOfFiles[i].getName();

                if (fileName.startsWith(PREFIXE_IMAGE)) {
                    id = fileName.substring(PREFIXE_IMAGE.length(), fileName.lastIndexOf("."));

                    listId.add(Long.parseLong(id));
                }
            }
        }

        return listId;
    }

    /* (no-Javadoc) */
    @Override
    public Target getTarget(final @NonNull Long id, int compression) {
        return new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(() -> {

                    File file = getImageCacheForStrip(id);

                    if (!file.exists()) {
                        try {
                            mImageUtils.savedFileOnDisk(file, bitmap, compression);
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to save drawable on local repository : " + e);
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(TAG, "Failed to save drawable on local repository : " + errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };
    }

    /* (no-Javadoc) */
    @Override
    public File saveImageStripInCache(
            @NonNull Long id, @NonNull ByteArrayOutputStream outputStream) {

        if (id == null || outputStream == null) {
            throw new IllegalArgumentException();
        }

        File newFile = null;

        try {
            newFile = getImageCacheForStrip(id);

            FileOutputStream stream = new FileOutputStream(newFile);
                stream.write(outputStream.toByteArray());

            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "Impossible to save image", e);
        }

        return newFile;
    }

    @Override
    public File saveSharedImageInSharedFolder(Long id, ByteArrayOutputStream outputStream) {

        if (id == null || outputStream == null) {
            throw new IllegalArgumentException();
        }

        File newFile = null;

        try {
            File root = new File(mCacheDir, "images");
            newFile = new File(root, "sharedContent.png");

            FileOutputStream stream = new FileOutputStream(newFile);
            stream.write(outputStream.toByteArray());

            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "Impossible to save image", e);
        }

        return newFile;
    }

    @Override
    public Flowable<File> clearCache(List<StripDto> strips) {

        File[] listOfFiles = mStorage.listFiles();

        return Flowable
            .just(listOfFiles)
            .flatMap(Flowable::fromArray)
            .filter(File::isFile)
            .filter(file -> file.getName().startsWith (PREFIXE_IMAGE))
            .filter(file -> {

                String name = file.getName();
                String id = name.substring(PREFIXE_IMAGE.length(), name.lastIndexOf("."));

                for (StripDto strip : strips) {
                    if (strip.getId().equals(Long.parseLong(id))) {
                        return false;
                    }
                }

                return true;
            })
            .doOnEach(file -> {
                boolean isDelete = file.getValue().delete();

                if (!isDelete) {
                    Log.e(TAG, "Could not be deleted : "+file.getValue().getName());
                }
            });
    }
}
