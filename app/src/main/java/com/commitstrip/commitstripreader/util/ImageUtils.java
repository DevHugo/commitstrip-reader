package com.commitstrip.commitstripreader.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Simple class to help to transform a bitmap to an array of byte
 */
public class ImageUtils {

    private String TAG = "ImageUtils";

    /**
     * Try to transform a android {@link Drawable} to {@link ByteArrayOutputStream}
     *
     * @param from
     * @param compression compression level from 0 to 100 (best quality).
     * @return an empty {@link ByteArrayOutputStream}, in case of failing transformation.
     * A complete fully {@link ByteArrayOutputStream}.
     */
    public ByteArrayOutputStream transform (@NonNull Drawable from, Integer compression) {
        Preconditions.checkNotNull(from);

        Bitmap to = null;
        if (from instanceof BitmapDrawable) {
            to = ((BitmapDrawable) from).getBitmap();
        }

        ByteArrayOutputStream bos = compress(to, compression);

        return bos;
    }

    /**
     * Saved an image on disk.
     *
     * @param file where to save the image
     * @param compression compression level from 0 to 100 (best quality).
     * @param bitmap image representation
     */
    public void savedFileOnDisk (File file, Bitmap bitmap, Integer compression) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = compress (bitmap, compression);

        try {
            OutputStream outputStream = new FileOutputStream(file);
            byteArrayOutputStream.writeTo(outputStream);

        } catch (IOException exception) {
            Log.e(TAG, "", exception);
        }

        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
    }

    private ByteArrayOutputStream compress (Bitmap bitmap, Integer compression) {

        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.WEBP, compression, ostream);

        return ostream;
    }
}
