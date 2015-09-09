package com.example.dan.ted.TED.api;

import com.example.dan.ted.TED.model.listModel;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Dan on 8/29/2015.
 */
public interface Api {
    @GET("/photo_gallery")
    void getList(Callback<listModel> cb);
}
