package com.commitstrip.commitstripreader.service;


import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class DownloadImageService extends JobService {

    private CompositeDisposable compositeDisposable;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        StripRepository stripRepository =
                ((MyApp) getApplication())
                        .getDataSourceComponent()
                        .getStripRepository();

        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(
                stripRepository
                    .fetchToDownloadImageStrip ()
                    .subscribeOn(Schedulers.newThread())
                    .flatMap(strip -> {
                        stripRepository.saveImageStripInCache(strip.getId(), strip.getContent());

                        return Flowable.just(strip);
                    })
                    .subscribeWith(new DisposableSubscriber<StripDto>() {

                        @Override
                        public void onNext(StripDto strip) {}

                        @Override
                        public void onError(Throwable t) {
                            jobFinished(jobParameters, true);
                        }

                        @Override
                        public void onComplete() {
                            jobFinished(jobParameters, false);
                        }
                    })
        );

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        compositeDisposable.clear();

        return true;
    }
}
