package com.commitstrip.commitstripreader.cache;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.cache.CacheContract.Presenter;
import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.util.CheckInternetConnection;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.sdsmdg.tastytoast.TastyToast;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CacheFragment extends Fragment implements CacheContract.View {
    Presenter mCachePresenter;

    @BindView(R.id.rangebar)
    RangeBar mRangeBar;

    @BindView(R.id.numberOfStrip)
    TextView mNumberOfStrip;

    @BindView(R.id.size) TextView mSize;

    @BindView(R.id.clearCache)
    Button mClearCache;

    @BindView(R.id.ok) Button mOk;

    public static int PIN_RADIUS = 100;

    private Calendar from;
    private Calendar to;

    private WeakReference<ProgressDialog> mWeakProgressBar;

    public static CacheFragment newInstance() {
        return new CacheFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cache, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Subscribe to the presenter
        mCachePresenter.subscribe();

        // Ask ButterKnife to inject view field
        ButterKnife.bind(this, view);

        // Setting max value selectable by user
        int numberOfMonthBetweenFirstStripAndNow =
                mCachePresenter.getNumberOfMonthBetweenFirstStripAndNow();
        mRangeBar.setTickEnd(numberOfMonthBetweenFirstStripAndNow);

        // By default, select every strip
        from = mCachePresenter.getDateFromNumberOfMonth(String.valueOf(0));
        to = mCachePresenter.getDateFromNumberOfMonth(String.valueOf(100));
        mCachePresenter.getNumberOfStrip(from.getTime(), to.getTime());

        mRangeBar.setOnRangeBarChangeListener(
                (rangeBar, leftPinIndex, rightPinIndex, leftPinValue, rightPinValue) -> {

                    if (leftPinIndex - rightPinIndex > 0) {
                        from = mCachePresenter.getDateFromNumberOfMonth(rightPinValue);
                        to = mCachePresenter.getDateFromNumberOfMonth(leftPinValue);
                    } else {
                        from = mCachePresenter.getDateFromNumberOfMonth(leftPinValue);
                        to = mCachePresenter.getDateFromNumberOfMonth(rightPinValue);
                    }

                    mCachePresenter.getNumberOfStrip(from.getTime(), to.getTime());
                });

        // Pin text displayed
        mRangeBar.setPinRadius(PIN_RADIUS);
        mRangeBar.setFormatter(oldPinText -> {

            Calendar dateToDisplay = mCachePresenter.getDateFromNumberOfMonth (oldPinText);

            return dateToDisplay.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE)
                    + " " +
                    dateToDisplay.get(Calendar.YEAR);
        });

        mClearCache.setOnClickListener(event -> {
            mCachePresenter.clearCacheStripForDownload();

            // Display a small message
            TastyToast.makeText(getContext(),
                    getString(R.string.activity_cache_delete_cache),
                    TastyToast.LENGTH_LONG,
                    TastyToast.SUCCESS);
        });

        mOk.setOnClickListener(event -> {
            mCachePresenter.scheduleStripForDownload (from.getTime(), to.getTime());

            if (CheckInternetConnection.isOnline() && !Configuration.OFFLINE_MODE) {

                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.activity_cache_title_alert_download_now))
                        .setMessage(getString(R.string.activity_cache_message_alert_download))
                        .setPositiveButton("Oui", (dialog, which) -> {

                            mCachePresenter.downloadStrip();
                        })
                        .setNegativeButton("Non", (dialog, which) -> {
                            scheduleService();
                        })
                        .show();
            }
            else {
                scheduleService();
            }

        });
    }

    @Override
    public void initProgressBar() {
        mWeakProgressBar = new WeakReference<>(new ProgressDialog(getContext()));

        ProgressDialog progressDialog = mWeakProgressBar.get();

        if (progressDialog != null) {
            progressDialog.setCancelable(true);
            progressDialog.setMessage(getString(R.string.activity_cache_progress_message));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.show();
        }
    }

    @Override
    public void setMaxProgressBar(Integer max) {
        ProgressDialog progressDialog = mWeakProgressBar.get();

        if (progressDialog != null) {
            progressDialog.setMax(max);
        }
    }



    @Override
    public void setDownloadProgress (int progress) {
        ProgressDialog progressDialog = mWeakProgressBar.get();

        if (progressDialog != null) {
            progressDialog.setProgress(progress);
        }
    }

    @Override
    public void hideProgressBar() {

        ProgressDialog progressDialog = mWeakProgressBar.get();

        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }

    private void scheduleService () {
        // Schedule service to download image in advance
        FirebaseJobDispatcher dispatcher =
                new FirebaseJobDispatcher(
                        new GooglePlayDriver(getContext().getApplicationContext())
                );
        mCachePresenter.scheduleImageService (dispatcher);

        // Display a small message
        TastyToast.makeText(getContext().getApplicationContext(),
                getString(R.string.activity_cache_schedule_download_ok),
                TastyToast.LENGTH_LONG,
                TastyToast.SUCCESS);
    }

    @Override
    public void setNumberOfStrip(Long numberOfStrip) {
        mNumberOfStrip.setText(numberOfStrip+" "+getString(R.string.activity_cache_strips));

        Long size = mCachePresenter.getSize(numberOfStrip);
        if (size >= 2000) {
            mSize.setText("~ " + Math.round(size/1024) + " Mo");
        } else {
            mSize.setText("~ " + size + " kB");
        }
    }
    @Override
    public void onPause() {
        super.onPause();

        if (mWeakProgressBar != null) {
            ProgressDialog progressDialog = mWeakProgressBar.get();

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        if (mCachePresenter != null) {
            mCachePresenter.unsubscribe();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCachePresenter != null) {
            mCachePresenter.unsubscribe();
        }

        mCachePresenter = null;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mCachePresenter = presenter;
    }
}
