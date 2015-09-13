package com.example.dan.ted.TED;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.dan.ted.TED.common.SessionManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public abstract class BaseFragment extends Fragment {
    static boolean imageURLReady = false;
    static String[] images = new String[0];
    Context context;
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        session = new SessionManager(context);
    }
}
