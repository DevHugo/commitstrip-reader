package com.commitstrip.commitstripreader.cache;

import com.commitstrip.commitstripreader.BasePresenter;
import com.commitstrip.commitstripreader.BaseView;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;

import java.util.Calendar;
import java.util.Date;

public class CacheContract {

    interface View extends BaseView<CacheContract.Presenter> {

        void initProgressBar();

        void setMaxProgressBar(Integer max);

        void setDownloadProgress(int progress);

        void setNumberOfStrip(Long numberOfMonth);

        void hideProgressBar();
    }

    interface Presenter extends BasePresenter {

        int getNumberOfMonthBetweenFirstStripAndNow();

        Long getSize(Long numberOfStrip);

        Calendar getDateFromNumberOfMonth(String numberOfMonth);

        void getNumberOfStrip(Date from, Date to);

        void scheduleStripForDownload(Date time, Date time1);

        void scheduleImageService(FirebaseJobDispatcher dispatcher);

        void downloadStrip();

        void clearCacheStripForDownload();
    }
}
