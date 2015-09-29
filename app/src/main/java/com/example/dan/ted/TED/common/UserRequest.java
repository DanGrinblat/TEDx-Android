package com.example.dan.ted.TED.common;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.dan.ted.TED.MainActivity;
import com.example.dan.ted.TED.RegisterActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Dan on 7/1/2015.
 */
public class UserRequest {
    public static void getToken(final Context context, final String email, final String password) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(email, password);
        String URL = Constants.url + "token";
        client.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                SessionManager session = new SessionManager(context);
                try {
                    //JSON Object
                    String str = new String(response);
                    JSONObject obj = new JSONObject(str);
                    String name = obj.getString("first_name");
                    int id = obj.getInt("id");
                    String phone = obj.getString("phone");
                    String affiliation = obj.getString("affiliation");
                    String token = obj.getString("token");
                    String photoUrl = obj.getString("photo_url");

                    session.createLoginSession(email, name, phone, affiliation, token, id, photoUrl);

                    Intent intent = new Intent(context, MainActivity.class);
                    // Closing all the Activities
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add new Flag to start new Activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (JSONException e) {
                    Toast.makeText(context, "JSON Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                if (response != null) {
                    toastResponse(response, context);
                } else {
                    Toast.makeText(context, "Timed out.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Put calls postImage if an image is given. If not, then it goes ahead to call getToken after put.
    public static void put(final Context context, final String oldPassword, final String newPassword, final String oldEmail,
                             final String newEmail, final String newPhone, final String newAffiliation, final String capturedImagePath) {
        final boolean newPasswordGiven = !TextUtils.isEmpty(newPassword);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(oldEmail, oldPassword);
        String URL = Constants.url + "user";
        JSONObject jsonParams = new JSONObject();
        //Log.e("tag", oldPassword + " " + newPasswordGiven + " " + newPassword + " " + oldEmail + " " + newEmail + " " + newPhone + " " + newAffiliation + " " + capturedImagePath);
        try {
            jsonParams.put("old_password", oldPassword);
            if (!TextUtils.isEmpty(newEmail))
                jsonParams.put("email", newEmail);
            if (!TextUtils.isEmpty(newPassword))
                jsonParams.put("new_password", newPassword);
            if (!TextUtils.isEmpty(newPhone))
                jsonParams.put("phone", newPhone);
            if (!TextUtils.isEmpty(newAffiliation))
                jsonParams.put("affiliation", newAffiliation);
        } catch (JSONException e) {
            Toast.makeText(context, "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(context, "StringEntity Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        client.put(context, URL, entity, "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] response) {
                        SessionManager session = new SessionManager(context);
                        try {
                            //JSON Object
                            String str = new String(response);
                            JSONObject obj = new JSONObject(str);

                            int id = obj.getInt("id"); //TODO: Serverside, make it only return what we need here
                            String email = obj.getString("email");
                            String photoPostURL = obj.getString("photo_url");
                            if (!TextUtils.isEmpty(capturedImagePath)) {
                                if (newPasswordGiven)
                                    postImage(context, capturedImagePath, photoPostURL, email, newPassword);
                                else
                                    postImage(context, capturedImagePath, photoPostURL, email, oldPassword);
                            }
                            if (newPasswordGiven)
                                getToken(context, email, newPassword);
                            else getToken(context, email, oldPassword);
                            //session.createLoginSession(email, name, phone, affiliation, token, id, photoUrl);

                        } catch (JSONException e) {
                            Toast.makeText(context, "JSON Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                        if (response != null) {
                            toastResponse(response, context);
                        } else {
                            Toast.makeText(context, "Timed out.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void checkLogin(final Context context, final String token) {
        final SessionManager session = new SessionManager(context);
        AsyncHttpClient client = new AsyncHttpClient();
        if (TextUtils.isEmpty(token)) {//No token in memory - first app launch
            session.logoutUser();
            return;
        }
        client.setBasicAuth(token, "");
        String URL = Constants.url + "user";
        client.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                if (response != null) {
                    if (checkUnauthorizedResponse(throwable, context, token)) {
                        Toast.makeText(context, "Token expired. Please log in.", Toast.LENGTH_SHORT).show();
                        session.logoutUser();
                    } else {
                        toastResponse(response, context);
                        session.logoutUser();
                    }
                } else {
                    Toast.makeText(context, "Connection lost.", Toast.LENGTH_SHORT).show();
                    //TODO: If this happens, either poll every few minutes for login OR have each HTTP request call this method upon Unauthorized Access
                }
            }
        });
    }

    public static void postImage(final Context context, final String capturedImagePath, final String photoPostURL, final String email, final String password) {
        String URL = Constants.url + "user/photo";
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(email, password);

        RequestParams params = new RequestParams();
        File file = new File(capturedImagePath);

        try {
            params.put("file", file);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Photo not found on device. Please try again later.", Toast.LENGTH_SHORT).show();
            UserRequest.getToken(context, email, password);
        }

        client.post(context, URL, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] response) {
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                        if (response != null) {
                            toastResponse(response, context);
                        } else {
                            Toast.makeText(context, "Photo upload timed out. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static boolean checkUnauthorizedResponse(Throwable throwable, Context context, String token) {
        HttpResponseException hre = (HttpResponseException) throwable;
        int statusCode = hre.getStatusCode();
        if (statusCode == 401) {
            checkLogin(context, token);
            return true;
        }
        else return false;
    }

    public static void toastResponse(byte[] response, Context context) {
        try {
            String str = new String(response);
            JSONObject obj = new JSONObject(str);
            String message = obj.getString("message");
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "JSON Error 2: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}

/*    public static void getImage(final Context context, final String token) {
        final SessionManager session = new SessionManager(context);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(token, "");
        String URL = Constants.url + "user/photo";
        client.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                SessionManager session = new SessionManager(context);
                try {
                    String str = new String(response);
                    JSONObject obj = new JSONObject(str);
                } catch (JSONException e) {
                    Toast.makeText(context, "JSON Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                if (response != null) {
                    String str = new String(response);
                    Toast.makeText(context, "(Wrong credentials). Message:" + str, Toast.LENGTH_SHORT).show();
                    session.checkLogin();
                } else {
                    Toast.makeText(context, "Timed out.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void getImgURL(String token, AsyncHttpResponseHandler handler) {
        if (!TextUtils.isEmpty(token)) { // This makes sure no program crash if user logs out in middle of request
            AsyncHttpClient client = new AsyncHttpClient();
            client.setBasicAuth(token, "");
            String URL = Constants.url + "photo_gallery";
            client.get(URL, handler);
        }
    }
*/