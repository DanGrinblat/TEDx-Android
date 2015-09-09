package com.example.dan.ted.TED.api;

import android.util.Base64;

import com.example.dan.ted.TED.common.Constants;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Dan on 8/29/2015.
 */
public class RestClient {
    private Api api;

    private RestClient() {}

    public static <S> S createService(Class<S> serviceClass, final String token) {
// Add interceptor when building adapter
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Constants.url)
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        if (token != null) {
                builder.setRequestInterceptor(new RequestInterceptor() {
                    final String credentials = token + ":";
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        String string = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                        request.addHeader("Accept", "application/json");
                        request.addHeader("Authorization", string);
                    }
                });
        }
        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }


    public static <S> S createService(Class<S> serviceClass, final String email, final String password) {
// Add interceptor when building adapter
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Constants.url)
                .setClient(new OkClient(new OkHttpClient()));

        if (email != null && password != null) {
            // concatenate username and password with colon for authentication
            final String credentials = email + ":" + password;

            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    // create Base64 encodet string
                    String string = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    request.addHeader("Accept", "application/json");
                    request.addHeader("Authorization", string);
                }
            });
        }
        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }
}
