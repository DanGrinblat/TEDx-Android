package com.example.dan.ted.TED;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.Constants;
import com.example.dan.ted.TED.common.HttpUpdateService;
import com.example.dan.ted.TED.common.SessionManager;
import com.example.dan.ted.TED.common.UserRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Photo_Sharing.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Photo_Sharing#newInstance} factory method to
 * create an instance of this fragment.
 */
public abstract class Photo_Sharing extends Fragment {
    boolean imageURLReady = false;
    Context context;
    SessionManager session;
    static String[] images;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        session = new SessionManager(context);
        Intent intent = new Intent(context, HttpUpdateService.class);
        context.startService(intent);
    }
}
