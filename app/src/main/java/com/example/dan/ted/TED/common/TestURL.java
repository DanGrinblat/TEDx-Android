package com.example.dan.ted.TED.common;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.dan.ted.TED.MainActivity;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dan on 7/4/2015.
 */
public class TestURL extends AsyncTask<String, Void, Boolean> {
    private Context context;
//CURRENTLY NOT IN USE
    public TestURL(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(params[0]).openConnection();
            con.setRequestMethod("HEAD");
            System.out.println(con.getResponseCode());
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        boolean bResponse = result;
        if (bResponse)
        {

            Toast.makeText(context, "File exists!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "File does not exist!", Toast.LENGTH_SHORT).show();
        }
    }
}