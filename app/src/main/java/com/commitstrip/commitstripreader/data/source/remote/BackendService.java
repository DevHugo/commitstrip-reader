package com.commitstrip.commitstripreader.data.source.remote;

import com.commitstrip.commitstripreader.dto.StripDto;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BackendService {

    @GET("strip/")
    Flowable<BackendResponseListStrip> fetchStrip(@Query("page") int page, @Query("size") int size);

    @GET("strip/{id}")
    Single<StripDto> fetchStrip(@Path("id") Long id);

    @GET("strip/recent")
    Single<StripDto> fetchMostRecentStrip();
}
