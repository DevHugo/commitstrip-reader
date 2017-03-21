package com.commitstrip.commitstripreader.cache;

import android.util.Log;

import com.commitstrip.commitstripreader.configuration.Configuration;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.service.DownloadImageService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import org.reactivestreams.Publisher;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Listens to user actions from the UI ({@link CacheActivity}), retrieves the data and
 * updates the UI as required.
 * <p />
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the StripPresenter (if it fails, it emits a compiler error).  It uses
 * {@link CachePresenterModule} to do so.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 **/
public class CachePresenter implements CacheContract.Presenter {

    private String TAG = "CachePresenter";
    private static final int AVERAGE_SIZE_STRIP = 250;

    private Date mDateFirstStripPublished;
    private CompositeDisposable compositeDisposable;

    private StripRepository mStripRepository;
    private CacheContract.View mView;

    private Integer mNumberStrip = 0;

    @Inject
    public CachePresenter (StripRepository stripRepository, CacheContract.View view) {
        mStripRepository = stripRepository;
        mView = view;

        // First strip published on 2 february 2012 on the commitstrip website.
        Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 2);
            cal.set(Calendar.MONTH, 2);
            cal.set(Calendar.YEAR, 2012);

        mDateFirstStripPublished = cal.getTime();

        compositeDisposable = new CompositeDisposable ();

        view.setPresenter(this);
    }

    @Override
    public void subscribe() {}

    @Override
    public int getNumberOfMonthBetweenFirstStripAndNow() {

        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(mDateFirstStripPublished);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(new Date());

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);

        return diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    }

    @Override
    public Long getSize (Long numberOfStrip) {
        return 250 * numberOfStrip;
    }

    @Override
    public Calendar getDateFromNumberOfMonth(String numberOfMonth) {

        Calendar endCalendar = new GregorianCalendar();
            endCalendar.setTime(mDateFirstStripPublished);

        endCalendar.add(Calendar.MONTH, Integer.parseInt(numberOfMonth));

        return endCalendar;
    }

    @Override
    public void getNumberOfStrip(Date from, Date to) {
        compositeDisposable.add(
                mStripRepository.fetchNumberOfStrip(from, to)
                        .count()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Long>() {

                            @Override
                            public void onSuccess(Long numberOfStrip) {
                                mView.setNumberOfStrip(numberOfStrip);
                            }

                            @Override
                            public void onError(Throwable t) {
                                Log.e(TAG, "", t);
                            }

                        }));
    }

    @Override
    public void scheduleStripForDownload(Date from, Date to) {
        compositeDisposable.add(
            mStripRepository.fetchNumberOfStrip(from, to)
                .subscribeOn(Schedulers.newThread())
                .toList()
                .flatMap(strips -> mStripRepository.scheduleStripForDownload (strips))
                .subscribeWith(new DisposableSingleObserver<Integer>() {

                    @Override
                    public void onSuccess(Integer numberStrip) {
                        mNumberStrip = numberStrip;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "", e);
                    }
                }));

    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void scheduleImageService(FirebaseJobDispatcher dispatcher) {

        dispatcher
                .newJobBuilder()
                .setService(DownloadImageService.class)
                .setTag(Configuration.JOB_ID_DOWNLOAD_IMAGE_SERVICE)
                .setRecurring(false)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(0, 3600))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setConstraints(
                        Constraint.ON_UNMETERED_NETWORK,
                        Constraint.DEVICE_CHARGING
                )
                .build();

    }

    @Override
    public void downloadStrip() {

        mView.initProgressBar ();

        if (mNumberStrip != 0) {
            mView.setMaxProgressBar(mNumberStrip);
        }

        mStripRepository
                .fetchToDownloadImageStrip ()
                .buffer(5)
                .flatMap(Flowable::fromIterable)
                .flatMap(strip -> mStripRepository
                        .saveImageStripInCache(strip.getId(), strip.getContent())
                        .map(id -> strip))
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DisposableSubscriber<StripDto>() {

                    private int stripDone = 0;

                    @Override
                    public void onNext(StripDto strip) {
                        stripDone++;

                        mView.setDownloadProgress(stripDone);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "", t);
                    }

                    @Override
                    public void onComplete() {
                        mView.hideProgressBar();
                    }
                });
    }

    public void clearCacheStripForDownload() {
        mStripRepository.clearCacheStripForDownload();
    }
}
