package com.commitstrip.commitstripreader.data.source.local;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.StripDataSource;
import com.commitstrip.commitstripreader.util.ExternalStorage;
import com.commitstrip.commitstripreader.util.InternalStorage;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.commitstrip.commitstripreader.util.Storage.isExternalStorageWritable;

@Singleton
public class StripImageCacheDataSource implements StripDataSource.StripImageCacheDataSource {

    private static String TAG = "StripImageCache";

    public String EXTENSION_FILE_IMAGE = ".jpg";

    private Picasso mPicasso;
    private File mStorage;

    @Inject
    public StripImageCacheDataSource(Picasso picasso, @InternalStorage File internalStorage, @ExternalStorage File externalStorage) {
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
    }

    @Override
    public File getImageCacheForStrip(Long id) {

        if (id == null)
            throw new IllegalArgumentException();

        return new File(mStorage, id+EXTENSION_FILE_IMAGE);
    }

    @Override
    public boolean isImageCacheForStripExist(Long id) {

        if (id == null)
            throw new IllegalArgumentException();

        return new File(mStorage, id+EXTENSION_FILE_IMAGE).exists();
    }

    @Override
    public boolean deleteImageStripInCache(Long id) {

        if (id == null)
            throw new IllegalArgumentException();

        File file = getImageCacheForStrip(id);

        if (file.exists())
            return file.delete();
        else
            return false;
    }

    @Override
    public RequestCreator fetchImageStrip(Long id) {

        if (id == null)
            throw new IllegalArgumentException();

        return mPicasso
                .load(getImageCacheForStrip(id))
                .networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_STORE);
    }

    @Override
    public void saveImageStripInCache(Long id, RequestCreator requestCreator) {

        if (id == null)
            throw new IllegalArgumentException();

        requestCreator.into(getTarget(id));
    }

    @Override
    public void saveImageStripInCache(Long id, String url) {

        if (id == null)
            throw new IllegalArgumentException();

        mPicasso.load(url).into(getTarget(id));
    }

    @Override
    public Target getTarget(final Long id){
        return new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(() -> {
                    File file = getImageCacheForStrip(id);

                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        ostream.flush();
                        ostream.close();
                    }
                    catch (Exception e) {
                        Log.e(TAG, "Failed to save drawable on local repository : "+ e);
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(TAG, "Failed to save drawable on local repository : "+ errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };
    }
}
