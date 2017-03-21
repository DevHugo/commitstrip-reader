package com.commitstrip.commitstripreader.data.source.remote;

import android.content.Context;

import com.commitstrip.commitstripreader.BuildConfig;
import com.commitstrip.commitstripreader.data.source.util.RestServiceTestHelper;
import com.commitstrip.commitstripreader.data.source.util.SampleStrip;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.util.NetworkSecurityPolicyWorkaround;
import com.commitstrip.commitstripreader.util.RobolectricStripImageDataSourceRule;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.picasso.Picasso;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = NetworkSecurityPolicyWorkaround.class)
public class StripRemoteDataSourceTest {

    private StripRemoteDataSource mStripRemote;
    private MockWebServer mServer;
    private Picasso mPicasso;
    private Retrofit mRetrofit;

    private String responseForOneStrip;
    private String responseForStrips;

    public StripRemoteDataSourceTest(){}

    @Rule
    public RobolectricStripImageDataSourceRule mDataSourceRule =
            new RobolectricStripImageDataSourceRule();

    @Before
    public void setUp() throws Exception {
        mServer = new MockWebServer();

        Context context = RuntimeEnvironment.application;

        responseForOneStrip = RestServiceTestHelper.getStringFromFile(context, "strip_200_ok_response.json");
        responseForStrips = RestServiceTestHelper.getStringFromFile(context, "strips_200_ok_response.json");

        mServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest recordedRequest) throws InterruptedException {

                if (recordedRequest.getPath().equals("/strip/recent")){
                    return new MockResponse().setResponseCode(200).setBody(responseForOneStrip);
                } else if (recordedRequest.getPath().equals("/strip/1")){
                    return new MockResponse().setResponseCode(200).setBody(responseForOneStrip);
                } else if (recordedRequest.getPath().equals("/strip/?page=0&size="+StripRemoteDataSource.HUGE_NUMBER)) {
                    return new MockResponse().setResponseCode(200).setBody(responseForStrips);
                }
                return new MockResponse().setResponseCode(404);
            }
        });

        mServer.start();

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

        for (StripDto strip : strips) {
            others.getContent().stream()
                    .filter(other -> strip.getId().equals(other.getId()))
                    .forEach(other -> SampleStrip.compareEveryPropertiesOfStripDto(strip, other));
        }
    }

    @After
    public void tearDown() throws Exception {
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
