package com.example.dan.ted.TED.common;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.dan.ted.TED.api.Api;
import com.example.dan.ted.TED.api.RestClient;
import com.example.dan.ted.TED.model.ListModel;

import org.apache.commons.io.FilenameUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Dan on 7/16/2015.
 */
public class HttpUpdateService extends IntentService {
    private static String[] images = new String[0];
    private static String[] speakerNames = new String[0];
    private static String[] speakerImages = new String[0];

    private static final String imageURL = Constants.imageURL;
    private static final String speakerURL = Constants.speakerURL;

    String token;
    //TODO: This should retrieve speaker bios, tent, itinerary, etc.

    public HttpUpdateService() {
        super(HttpUpdateService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SessionManager session;
        session = new SessionManager(this);
        token = (String) session.getUserDetails().get("token");

        if (intent.hasExtra("intent"))
        if (intent.getStringExtra("intent").equals("Photos"))
            updatePhotoList(intent);
        else if (intent.getStringExtra("intent").equals("Speakers"))
            updateSpeakerList(intent);
    }

    private void updatePhotoList(Intent intent) {
        final String [] oldImages;
        if (intent.hasExtra("img_list"))
            oldImages = intent.getStringArrayExtra("img_list");
        else
            oldImages = null;

        Api restClient = RestClient.createService(Api.class, token);
        restClient.getPhotoList(new Callback<ListModel>() {
            @Override
            public void success(ListModel list, Response response) {
                int oldImgLength;
                int newImgLength;
                if (oldImages != null)
                    oldImgLength = oldImages.length;
                else oldImgLength = 0;
                //Log.e("tag", "Reached onSuccess");

                images = new String[list.getFileList().size()];
                for (int c = 0; c < images.length; c++) {
                    String name = list.getFileList().get(c);
                    try {
                        images[c] = imageURL + URLEncoder.encode(name, "utf-8").replace("+", "%20");
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
                if (oldImgLength < newImgLength) {//Give BaseFragment new image list and set imgURLReady to true
                    Intent intent = new Intent("img_list_new");
                    intent.putExtra("img_list", images);
                    sendBroadcast(intent);
                    //TODO: Send new photo notification AND place notification in Notification fragment
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Intent intent = new Intent("img_list_off");
                if (oldImages.length == 0)
                    sendBroadcast(intent);
                Log.e("tag", "Img List failure: " + error.getMessage());
            }
        });
    }

    private void updateSpeakerList(Intent intent) {  //TODO: Get speaker bios and arrange them with matching #s
        final String [] oldSpeakerList;
        if (intent.hasExtra("speaker_list"))
            oldSpeakerList = intent.getStringArrayExtra("speaker_list");
        else
            oldSpeakerList = null;

        Api restClient = RestClient.createService(Api.class, token);
        restClient.getSpeakerList(new Callback<ListModel>() {
            @Override
            public void success(ListModel list, Response response) {
                int oldSpeakerLength;
                int newSpeakerLength;
                if (oldSpeakerList != null)
                    oldSpeakerLength = oldSpeakerList.length;
                else oldSpeakerLength = 0;
                int listSize = list.getFileList().size();
                speakerNames = new String[listSize];
                speakerImages = new String[listSize];
                for (int c = 0; c < listSize; c++) {
                    String name = list.getFileList().get(c);
                    try {
                        speakerNames[c] =  FilenameUtils.removeExtension(name.replace("+", " ")); //replace pluses with space. NOTE: PNG FILES ONLY
                        speakerImages[c] = speakerURL + URLEncoder.encode(name, "utf-8").replace("+", "%20"); //replace pluses with %20 for space
                    } catch (UnsupportedEncodingException e) {
                        Log.e("tag", "UnsupportedEncoding");
                    }
                }
                newSpeakerLength = speakerNames.length;

                if (newSpeakerLength == 0) {
                    Intent intent = new Intent("speaker_list_off");
                    sendBroadcast(intent);
                }
                if (oldSpeakerLength != newSpeakerLength) {
                    Intent intent = new Intent("speaker_list_new");
                    intent.putExtra("speaker_list", speakerNames);
                    intent.putExtra("speaker_image_list", speakerImages);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Intent intent = new Intent("speaker_list_off");
                sendBroadcast(intent);
                Log.e("tag", "Speaker List failure: " + error.getMessage());
            }
        });
    }

    private void updateSpeakerBios(Intent intent) {
        final String [] oldSpeakerBios;
        if (intent.hasExtra("bios_list"))
            oldSpeakerBios = intent.getStringArrayExtra("speaker_bios");
        else
            oldSpeakerBios = null;
        Api restClient = RestClient.createService(Api.class, token);
        restClient.getSpeakerList(new Callback<ListModel>() {
            @Override
            public void success(ListModel list, Response response) {

            }
            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}