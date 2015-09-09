package com.example.dan.ted.TED.common;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.dan.ted.TED.api.Api;
import com.example.dan.ted.TED.api.RestClient;
import com.example.dan.ted.TED.model.listModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Dan on 7/16/2015.
 */
public class HttpUpdateService extends IntentService {
    static String[] images = new String[0];
    private static final String imageURL = "http://10.0.3.2:5000/api/v1.0/photo_gallery/";

    public HttpUpdateService() {
        super(HttpUpdateService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("tag", "Reached service");
        final String [] oldImages;

        if (intent.hasExtra("img_list"))
            oldImages = intent.getStringArrayExtra("img_list");
        else
            oldImages = null;
        SessionManager session;

        session = new SessionManager(this);
        String token = (String)session.getUserDetails().get("token");

        Api restClient = RestClient.createService(Api.class, token);
        restClient.getList(new Callback<listModel>() {
            @Override
            public void success(listModel list, Response response) {
                int oldImgLength;
                int newImgLength;
                if (oldImages != null)
                    oldImgLength = oldImages.length;
                else oldImgLength = 0;
                Log.e("tag", "Reached onSuccess");

                images = new String[list.getImgList().size()];
                for (int c = 0; c < images.length; c++) {
                    String image = list.getImgList().get(c);
                    try {
                        images[c] = imageURL + URLEncoder.encode(image, "utf-8").replace("+", "%20");
                    } //replace pluses with %20 for space
                    catch (UnsupportedEncodingException e) {
                        Log.e("tag", "UnsupportedEncoding");
                    }
                }

                newImgLength = images.length;
                if (newImgLength == 0) {
                    Intent intent = new Intent("img_list_off");
                    sendBroadcast(intent);
                }

                if (oldImgLength < newImgLength) {//Give Photo_Sharing new image list and set imgURLReady to true
                    Intent intent = new Intent("img_list_new");
                    intent.putExtra("img_list", images);
                    sendBroadcast(intent);
                    //TODO: Give the ImgListFragment the img list
                    //TODO: Send new photo notification AND place notification in Notification fragment
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Intent intent = new Intent("img_list_off");
                if (oldImages.length == 0)
                    sendBroadcast(intent);
                Log.e("tag", "failure " + error.getMessage());
            }
        });
    }
/*
    private void scheduleNextUpdate() {
        Intent intent = new Intent(getApplicationContext(), HttpUpdateService.class);
        Log.e("tag", "Reached update scheduler");

        intent.putExtra("img_list", images);
        PendingIntent pendingIntent =
                PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // TODO: Allow user to turn off auto update

        long currentTimeMillis = System.currentTimeMillis();
        long nextUpdateTimeMillis = currentTimeMillis + (10 * DateUtils.SECOND_IN_MILLIS); //update once every 10 seconds
        Time nextUpdateTime = new Time();
        nextUpdateTime.set(nextUpdateTimeMillis);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
    }*/
}