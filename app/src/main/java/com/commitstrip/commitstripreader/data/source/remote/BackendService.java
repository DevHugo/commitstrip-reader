package com.commitstrip.commitstripreader.data.source.remote;

import com.commitstrip.commitstripreader.dto.StripDto;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BackendService {

    @GET("strip/")
    Flowable<BackendResponseListStrip> fetchStrip(@Query("page") Integer page,
            @Query("size") Integer size);

    @GET("strip/{id}")
    Maybe<StripDto> fetchStrip(@Path("id") Long id);

    @GET("strip/?sort=releaseDate,desc")
    Flowable<BackendResponseListStrip> fetchListStrip(
            @Query("page") Integer page, @Query("size") Integer size
    );

    @GET("strip/recent")
    Maybe<StripDto> fetchMostRecentStrip();
}
