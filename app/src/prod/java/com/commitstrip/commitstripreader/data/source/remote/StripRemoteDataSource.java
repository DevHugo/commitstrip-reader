package com.commitstrip.commitstripreader.data.source.remote;

import android.support.annotation.VisibleForTesting;

import com.commitstrip.commitstripreader.data.source.StripDataSource;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import io.reactivex.Maybe;
import io.reactivex.functions.Function;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

import org.reactivestreams.Publisher;

import retrofit2.Retrofit;

public class StripRemoteDataSource implements StripDataSource.RemoteDataSource {

    public static boolean AIRPLANE_MODE = false;

    private final String TAG = "StripRemoteDataSource";

    private final Retrofit mRetrofit;
    private final Picasso mPicasso;

    public static Integer HUGE_NUMBER = 999999;

    public StripRemoteDataSource(Retrofit retrofit, Picasso picasso) {
        mRetrofit = retrofit;
        mPicasso = picasso;
    }

    /**
     * Fetch Strip from the backend.
     *
     * If network can not be found, retry at least two times and return null.
     *
     * @return A strip or null if we can not reach the server.
     */
    @Override
    public Maybe<StripDto> fetchStrip(Long id) {
        BackendService service = mRetrofit.create(BackendService.class);

        if (id == null) {
            return service.fetchMostRecentStrip().retry(2);
        } else {
            return service.fetchStrip(id).retry(2);
        }
    }

    /**
     * Fetch strip image from CommitStrip website.
     *
     * @return Picasso request object.
     */
    @Override
    public RequestCreator fetchImageStrip(String url) {
        return mPicasso.load(url).networkPolicy(NetworkPolicy.NO_STORE);
    }

    /**
     * Fetch all strip from org.commitstrip.commistripreader.data.source.remote
     */
    @Override
    public Flowable<List<StripDto>> fetchAllStrip() {
        BackendService service = mRetrofit.create(BackendService.class);

        return service.fetchStrip(0, HUGE_NUMBER)
                .retry(2)
                .map(BackendResponseListStrip::getContent);
    }

    /**
     * Fetch a list of strip according to the two parameter.
     *
     * @param numberOfStripPerPage How many strip you want
     * @param page from which page
     * @return list of strip from the page specified in parameter
     */
    @Override
    public Flowable<StripDto> fetchListStrip(Integer numberOfStripPerPage, int page) {
        BackendService service = mRetrofit.create(BackendService.class);

        return service.fetchListStrip(page, numberOfStripPerPage)
                .retry(2)
                .map(BackendResponseListStrip::getContent)
                .flatMap(new Function<List<StripDto>, Publisher<StripDto>>() {
                    @Override
                    public Publisher<StripDto> apply(List<StripDto> strips) throws Exception {
                        return Flowable.fromIterable(strips);
                    }
                });
    }
}
