package com.commitstrip.commitstripreader.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.data.component.SharedPreferencesComponent;
import com.commitstrip.commitstripreader.util.ImageUtils;
import com.commitstrip.commitstripreader.util.di.module.ImageUtilsModule;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

public class CompressPreference extends DialogPreference {

    private ImageView mImageView;
    private RangeBar mRangebar;
    private TextView mTextViewLengthSize;
    private TextView mFileSize;

    @Inject ImageUtils mImageUtils;

    public CompressPreference (Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.preference_dialog_compress);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        DaggerCompressPreferenceComponent
                .builder()
                .imageUtilsModule(new ImageUtilsModule())
                .build().inject(this);

        mImageView = (ImageView) view.findViewById(R.id.imageView);
        mRangebar = (RangeBar) view.findViewById(R.id.rangebar);
        mFileSize = (TextView) view.findViewById(R.id.fileSize);
        mTextViewLengthSize = (TextView) view.findViewById(R.id.lengthSize);

        mTextViewLengthSize.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getKey(), 99);

        int compression = sharedPreferences.getInt(getKey(), 99);

        if (compression > 0){
            mRangebar.setSeekPinByValue(compression);
        }

        Drawable drawable = mImageView.getDrawable();

        mRangebar.setOnRangeBarChangeListener(
                (rangeBar, leftPinIndex, rightPinIndex, leftPinValue, rightPinValue) -> {

                    mTextViewLengthSize.setVisibility(View.VISIBLE);

                    ByteArrayOutputStream byteArrayOutputStream = mImageUtils.transform(drawable, rightPinIndex);

                    byte[] bytes = byteArrayOutputStream.toByteArray();

                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    mImageView.setImageBitmap(
                            Bitmap.createScaledBitmap(
                                    bmp,
                                    drawable.getIntrinsicWidth(),
                                    drawable.getIntrinsicHeight(),
                                    false));

                    mFileSize.setText(" " + bytes.length);

                    sharedPreferences.edit().putInt(getKey(), rightPinIndex).apply();
                });
    }
}
