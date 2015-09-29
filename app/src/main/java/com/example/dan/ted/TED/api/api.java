package com.example.dan.ted.TED.api;

import com.example.dan.ted.TED.model.ListModel;
import com.example.dan.ted.TED.model.TimestampModel;

import java.security.Timestamp;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Dan on 8/29/2015.
 */
public interface Api {
    @GET("/photo_gallery")
    void getPhotoList(Callback<ListModel> cb);

    @GET("/event_details/speakers")
    void getSpeakerList(Callback<ListModel> cb);

    @GET("/countdown")
    void getTimestamp(Callback<TimestampModel> cb);
}
