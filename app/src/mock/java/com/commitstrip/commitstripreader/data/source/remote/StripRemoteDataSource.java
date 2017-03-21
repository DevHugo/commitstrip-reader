package com.commitstrip.commitstripreader.data.source.remote;

import com.commitstrip.commitstripreader.data.source.StripDataSource;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import dagger.Provides;
import io.reactivex.Maybe;
import io.reactivex.functions.Function;

import java.net.SocketTimeoutException;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

import org.reactivestreams.Publisher;

import retrofit2.Retrofit;

public class StripRemoteDataSource implements StripDataSource.RemoteDataSource {

    public static boolean AIRPLANE_MODE = false;
    public static int HUGE_NUMBER = 99999;

    private final Picasso mPicasso;
    private final Retrofit mRetrofit;

    public StripRemoteDataSource(Retrofit retrofit, Picasso picasso) {
        mPicasso = picasso;
        mRetrofit = retrofit;
    }

    @Override
    public Maybe<StripDto> fetchStrip(Long id) {
        Gson gson = new Gson();

        BackendResponseListStrip strips =
                gson.fromJson(SampleStrip.JSON_FOR_STRIPS, BackendResponseListStrip.class);

        if (!AIRPLANE_MODE) {

            if (id == null) {
                return Maybe.just(fetchMostRecentStrip());
            }

            int i = 0;
            while (i < SampleStrip.NUMBER_STRIPS_JSON && !strips.getContent().get(i).getId().equals(
                    id)) {
                i++;
            }

            if (i < SampleStrip.NUMBER_STRIPS_JSON) {
                return Maybe.just(strips.getContent().get(i));
            } else {
                return Maybe.empty();
            }
        } else {
            return Maybe.error(new SocketTimeoutException());
        }
    }

    private StripDto fetchMostRecentStrip() {
        Gson gson = SampleStrip.provideGson();

        BackendResponseListStrip strips =
                gson.fromJson(SampleStrip.JSON_FOR_STRIPS, BackendResponseListStrip.class);

        StripDto mostRecentStrip = strips.getContent().get(0);

        for (StripDto strip : strips.getContent()) {
            if (strip.getReleaseDate().after(mostRecentStrip.getReleaseDate())) {
                mostRecentStrip = strip;
            }
        }

        return mostRecentStrip;
    }

    @Override
    public RequestCreator fetchImageStrip(String url) {
        if (!AIRPLANE_MODE) {
            return mPicasso.load(url);
        } else {
            return mPicasso.load(url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE);
        }
    }

    @Override
    public Flowable<List<StripDto>> fetchAllStrip() {

        if (!AIRPLANE_MODE) {
            Gson gson = new Gson();

            BackendResponseListStrip strips =
                    gson.fromJson(SampleStrip.JSON_FOR_STRIPS, BackendResponseListStrip.class);

            return Flowable.just(strips.getContent());
        } else {
            return Flowable.error(new SocketTimeoutException());
        }
    }

    @Override
    public Flowable<StripDto> fetchListStrip(Integer numberOfStripPerPage, int page) {

        if (!AIRPLANE_MODE) {
            Gson gson = new Gson();

            BackendResponseListStrip strips =
                    gson.fromJson(SampleStrip.JSON_FOR_STRIPS, BackendResponseListStrip.class);

            return Flowable
                    .just(strips.getContent())
                    .flatMap(new Function<List<StripDto>, Publisher<StripDto>>() {
                        @Override
                        public Publisher<StripDto> apply(List<StripDto> strips) throws Exception {
                            return Flowable.fromIterable(strips);
                        }
                    });
        } else {
            return Flowable.error(new SocketTimeoutException());
        }
    }


}



