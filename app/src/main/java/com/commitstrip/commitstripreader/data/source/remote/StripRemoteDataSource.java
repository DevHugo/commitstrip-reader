package com.commitstrip.commitstripreader.data.source.remote;

import com.commitstrip.commitstripreader.data.source.StripDataSource;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

@Singleton
public class StripRemoteDataSource implements StripDataSource.RemoteDataSource {

    private final String TAG = "StripRemoteDataSource";

    private final Retrofit mRetrofit;
    private final Picasso mPicasso;

    private Integer hugeNumber = 999999;

    @Inject
    public StripRemoteDataSource (Retrofit retrofit, Picasso picasso) {
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
    public Single<StripDto> fetchStrip(Long id) {
        BackendService service = mRetrofit.create(BackendService.class);

        if (id == null) {
            return service.fetchMostRecentStrip().retry(2);
        }
        else {
            return service.fetchStrip(id).retry(2);
        }
    }

    /**
     * Fetch strip image from CommitStrip website.
     *
     * @param url
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

        return service.fetchStrip(0, hugeNumber)
                .retry(2)
                .map(BackendResponseListStrip::getContent);
    }

}

