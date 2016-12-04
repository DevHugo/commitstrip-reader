package com.commitstrip.commitstripreader.backend.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A util class for downloading content synchronously or asynchronously.
 */
@Service
public class DownloadFile {

    /**
     * Download content synchronously. If the request was not successfully received, an runtime exception will be thrown.
     *
     * @param url
     * @return Content of the specified url.
     * @throws IOException Network exception.
     */
    public String downloadFileFromNetwork(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(url).get().build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String content = response.body().string();
            response.body().close();

            return content;
        } else {
            throw constructUnknownResponseException(response);
        }
    }

    private RuntimeException constructUnknownResponseException(Response response) throws IOException {
        return new RuntimeException(String.format("Unknown response from Server {Code: %d Body: %s}", response.code(), response.body().string()));
    }
}
