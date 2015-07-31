package com.example.dan.ted.TED.common;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

/**
 * Created by Dan on 7/16/2015.
 */
public class HttpUpdateService extends IntentService {
    String[] images;
    private static final String imageURL = "http://10.0.3.2:5000/api/v1.0/img/";

    public HttpUpdateService() {
        super(HttpUpdateService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String [] oldImages;

        if (intent.hasExtra("img_list"))
            oldImages = intent.getStringArrayExtra("img_list");
        else
            oldImages = null;
        SessionManager session;

        session = new SessionManager(this);
        String token = (String)session.getUserDetails().get("token");
        //TODO: For all toasts, to actually make them show you must implement Handler
        UserRequest.getImgURL(token, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                try {
                    int oldImgLength;
                    int newImgLength;
                    if (oldImages != null)
                        oldImgLength = oldImages.length;
                    else oldImgLength = 0;

                    String str = new String(response);
                    JSONObject obj = new JSONObject(str);
                    JSONArray jsonImageList = new JSONArray(obj.getString("img_list"));
                    images = new String[jsonImageList.length()];
                    for (int c = 0; c < jsonImageList.length(); c++) {
                        String image = jsonImageList.getString(c);
                        try { images[c] = imageURL + URLEncoder.encode(image, "utf-8"); }
                        catch(UnsupportedEncodingException e) {
                            //This will never happen
                        }
                    }
                    newImgLength = images.length;

                    if (oldImgLength != newImgLength) {//Give Photo_Sharing new images and set imgURLReady to true
                        Intent intent = new Intent("img_list_new");
                        intent.putExtra("img_list", images);
                        sendBroadcast(intent);
                        //TODO: Give the ImgListFragment the img list
                        //TODO: Send new photo notification AND place notification in Notification fragment
                    }
                    else {//Img list stays on - no updates.
                        Intent intent = new Intent("img_list_on");
                        sendBroadcast(intent);
                    }
                } catch (JSONException e) {
                    Toast.makeText(HttpUpdateService.this, "JSON Error 1: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
                //imageURLReady = true;
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                try {
                    String str = new String(response);
                    JSONObject obj = new JSONObject(str);
                    //if (oldImages == null) {  //We don't need to change anything
                    //    Intent intent = new Intent("img_list_off");
                    //    sendBroadcast(intent);
                    //}
                    Toast.makeText(HttpUpdateService.this, "Error: " + str, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(HttpUpdateService.this, "JSON Error 2: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        scheduleNextUpdate();
    }

    private void scheduleNextUpdate() {
        Intent intent = new Intent(this, this.getClass());
        intent.putExtra("img_list", images);
        PendingIntent pendingIntent =
                PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // TODO: Allow user to turn off auto update

        long currentTimeMillis = System.currentTimeMillis();
        long nextUpdateTimeMillis = currentTimeMillis + 10 * DateUtils.SECOND_IN_MILLIS; //update once every 10 seconds
        Time nextUpdateTime = new Time();
        nextUpdateTime.set(nextUpdateTimeMillis);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
    }
}

/*
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                try {
                    String str = new String(response);
                    JSONObject obj = new JSONObject(str);
                    JSONArray jsonImageList = new JSONArray(obj.getString("img_list"));
                    final String[] images = new String[jsonImageList.length()];
                    for (int c = 0; c < jsonImageList.length(); c++) {
                        String string = jsonImageList.getString(c);
                        images[c] = string;
                    }
                    if (oldImages != null && oldImages.length != images.length) {
                        //TODO: Tell the ImageGridFragment to refresh all images OR find a way to make them add/subtract only alternative images (maybe check if image in cache before calling its URL)
                        //TODO: Send new photo notification AND place notification in Notification fragment
                    }

                } catch (JSONException e) {
                    Toast.makeText(HttpUpdateService.this, "JSON Error 1: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
                //imageURLReady = true;
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                try {
                    String str = new String(response);
                    JSONObject obj = new JSONObject(str);
                    //TODO: Show TextView (change visibilities) in Grid fragment
                    Toast.makeText(HttpUpdateService.this, "Error: " + str, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(HttpUpdateService.this, "JSON Error 2: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        scheduleNextUpdate();
    }
    */