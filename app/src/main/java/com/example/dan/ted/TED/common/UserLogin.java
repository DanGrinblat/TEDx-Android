package com.example.dan.ted.TED.common;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.dan.ted.TED.MainActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dan on 7/1/2015.
 */
public class UserLogin {
    public static void Login(final Context context, final String email, final String password) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(email, password);
        String URL = "http://10.0.3.2:5000/api/v1.0/token";
        client.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {
                SessionManager session = new SessionManager(context);
                try {
                    //JSON Object
                    String str = new String(response);
                    JSONObject obj = new JSONObject(str);
                    String name = obj.getString("name");
                    String token = obj.getString("token");
                    session.createLoginSession(name, email, token);

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
                    String str = new String(response);
                    Toast.makeText(context, "(Wrong credentials). Message:" + str, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Timed out.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

