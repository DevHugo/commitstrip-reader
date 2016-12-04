package com.commitstrip.commistripreader.data.source.remote;

import android.content.Context;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.commitstrip.commitstripreader.BuildConfig;
import com.commitstrip.commitstripreader.data.source.remote.BackendResponseListStrip;
import com.commitstrip.commitstripreader.data.source.remote.StripRemoteDataSource;
import com.commitstrip.commitstripreader.data.source.util.RestServiceTestHelper;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.picasso.Picasso;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StripRemoteDataSourceTest {

    private static StripRemoteDataSource mStripRemote;
    private static MockWebServer mServer;
    private static Picasso mPicasso;
    private static Retrofit mRetrofit;

    private static String responseForOneStrip;
    private static String responseForStrips;

    public StripRemoteDataSourceTest(){}

    @BeforeClass
    public static void setUp() throws Exception {
        mServer = new MockWebServer();

        responseForOneStrip = RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), "strip_200_ok_response.json");
        responseForStrips = RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), "strips_200_ok_response.json");

        mServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest recordedRequest) throws InterruptedException {

                if (recordedRequest.getPath().equals("/strip/recent")){
                    return new MockResponse().setResponseCode(200).setBody(responseForOneStrip);
                } else if (recordedRequest.getPath().equals("/strip/1")){
                    return new MockResponse().setResponseCode(200).setBody(responseForOneStrip);
                } else if (recordedRequest.getPath().equals("/strip/?page=0&size="+Integer.MAX_VALUE)) {
                    return new MockResponse().setResponseCode(200).setBody(responseForStrips);
                }
                return new MockResponse().setResponseCode(404);
            }
        });

        mServer.start();

        Context context = getInstrumentation().getContext();
        mPicasso = Picasso.with(context);

        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://"+mServer.getHostName() + ":" +mServer.getPort())
                .client(getClientHttp())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mStripRemote = new StripRemoteDataSource(mRetrofit, mPicasso);
    }

    @Test
    public void fetchStripWithCorrectResponseShouldReturnStrip() throws Exception {

        Gson gson = new Gson();

        StripDto strip = mStripRemote.fetchStrip(1L).blockingGet();
        StripDto other = gson.fromJson(responseForOneStrip, StripDto.class);

        SampleStrip.compareEveryPropertiesOfStripDto (strip, other);
    }

    @Test(expected = RuntimeException.class)
    public void mostRecentWithIncorrectIdShouldReturnError() throws Exception {
        mStripRemote.fetchStrip(10000L).blockingGet();
    }

    @Test
    public void mostRecentWithCorrectResponseShouldReturnStrip() throws Exception {

        Gson gson = new Gson();

        StripDto strip = mStripRemote.fetchStrip(null).blockingGet();
        StripDto other = gson.fromJson(responseForOneStrip, StripDto.class);

        SampleStrip.compareEveryPropertiesOfStripDto (strip, other);
    }

    @Test
    public void fetchAllStripWithCorrectResponseShouldReturnStrip() throws Exception {

        Gson gson = new Gson();

        List<StripDto> strips = mStripRemote.fetchAllStrip().blockingFirst();

        BackendResponseListStrip others = gson.fromJson(responseForStrips, BackendResponseListStrip.class);

        for (StripDto strip : strips)
            for (StripDto other : others.getContent())
                if (strip.getId().equals(other.getId()))
                    SampleStrip.compareEveryPropertiesOfStripDto (strip, other);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        mServer.shutdown();
    }

    public static OkHttpClient getClientHttp() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(1, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            client.interceptors().add(logging);
        }

        client.connectTimeout(2, TimeUnit.SECONDS);
        client.writeTimeout(2, TimeUnit.SECONDS);
        client.readTimeout(2, TimeUnit.SECONDS);

        return client.build();
    }
}
