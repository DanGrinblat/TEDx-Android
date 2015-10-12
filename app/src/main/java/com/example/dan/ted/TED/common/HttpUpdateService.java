package com.example.dan.ted.TED.common;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.dan.ted.TED.api.Api;
import com.example.dan.ted.TED.api.RestClient;
import com.example.dan.ted.TED.model.Bio;
import com.example.dan.ted.TED.model.ListModel;

import org.apache.commons.io.FilenameUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
    private static String[] speakerBios = new String[0];
    private static String[] speakerBioNames = new String[0];
    private static Api restClient;
    static String token;
    private static final String imageURL = Constants.imageURL;
    private static final String speakerURL = Constants.speakerURL;


    //TODO: This should retrieve tent, itinerary, etc.

    public HttpUpdateService() {
        super(HttpUpdateService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) { //TODO: Handle token expiration
        //TODO: Check if bios/names have been changed in terms of equivalency
        SessionManager session;
        session = new SessionManager(this);
        token = (String) session.getUserDetails().get("token");
        restClient = RestClient.createService(Api.class, token);


        String stringIntent;
        if (intent.hasExtra("intent")) {
            stringIntent = intent.getStringExtra("intent");
            if (session.isLoggedIn()) {
                switch (stringIntent) {
                    case "Photos":
                        updatePhotoList(intent);
                        break;
                    case "Speakers":
                        updateSpeakerList(intent);
                        if (!intent.hasExtra("bio_list"))
                            break;  //Intentional fallthrough if bio_list is available
                    case "Bios":
                        updateSpeakerBios(intent);
                        break;
                }
            }
            else {
                Intent broadcastIntent;
                switch (stringIntent) {
                    case "Photos":
                        broadcastIntent = new Intent("img_list_off");
                        sendBroadcast(broadcastIntent);
                        break;
                    case "Speakers":
                        broadcastIntent = new Intent("speaker_list_off");
                        sendBroadcast(broadcastIntent);
                        break;
                    case "Bios":
                        broadcastIntent = new Intent("bios_list_off");
                        sendBroadcast(broadcastIntent);
                        break;
                }
            }
        }
    }

    private void updatePhotoList(Intent intent) {
        final String [] oldImages;
        if (intent.hasExtra("img_list"))
            oldImages = intent.getStringArrayExtra("img_list");
        else
            oldImages = null;

        //Api restClient = RestClient.createService(Api.class, token);
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

    private void updateSpeakerList(Intent intent) {
        final String [] oldSpeakerList;
        if (intent.hasExtra("speaker_list"))
            oldSpeakerList = intent.getStringArrayExtra("speaker_list");
        else
            oldSpeakerList = null;

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

                Arrays.sort(speakerNames, new StringComparator());
                Arrays.sort(speakerImages, new URLComparator());


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
        final String [] oldBiosList;
        if (intent.hasExtra("bio_list"))
            oldBiosList = intent.getStringArrayExtra("bio_list");
        else
            oldBiosList = null;

        //Api restClient = RestClient.createService(Api.class, token);
        restClient.getBioList(new Callback<Bio>() {
            @Override
            public void success(Bio list, Response response) {
                int oldBiosLength;
                int newBiosLength;
                if (oldBiosList != null)
                    oldBiosLength = oldBiosList.length;
                else oldBiosLength = 0;
                int listSize = list.getFileList().size();

                HashMap<String, String> speakerBios1 = new HashMap<String, String>(listSize);

                speakerBioNames = new String[listSize];
                speakerBios = new String[listSize];

                for (int c = 0; c < listSize; c++) {
                    String name = list.getFileList().get(c).getName();
                    String bio = list.getFileList().get(c).getBio();
                    speakerBioNames[c] = FilenameUtils.removeExtension(name.replace("+", " "));
                    speakerBios[c] = bio.replaceAll("\u2019","'").replaceAll("\u201c","\"").replaceAll("\u201d", "\"");
                    speakerBios1.put(speakerBioNames[c], speakerBios[c]);
                }

                Arrays.sort(speakerBioNames, new StringComparator());

                newBiosLength = speakerBios1.size();

                if (newBiosLength == 0) {
                    Intent intent = new Intent("bio_list_off");
                    sendBroadcast(intent);
                }
                if (oldBiosLength != newBiosLength) {
                    Intent intent = new Intent("bio_list_new");
                    intent.putExtra("bio_name_list", speakerBioNames);
                    //intent.putExtra("bio_list", speakerBios);
                    intent.putExtra("bio_list", speakerBios1);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Intent intent = new Intent("bio_list_off");
                sendBroadcast(intent);
                Log.e("tag", "Bio List failure: " + error.getMessage());
            }
        });
    }

    public class StringComparator implements Comparator<String> {
        public int compare(String b1, String b2) {
            return b1.substring(b1.lastIndexOf(" ")+1).compareTo(b2.substring(b2.lastIndexOf(" ")+1));
        }
    }
    public class URLComparator implements Comparator<String> {
        public int compare(String b1, String b2) {
            return b1.substring(b1.lastIndexOf("%20")+3).compareTo(b2.substring(b2.lastIndexOf("%20")+3));
        }
    }
}
