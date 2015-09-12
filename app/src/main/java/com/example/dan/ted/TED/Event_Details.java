package com.example.dan.ted.TED;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.Constants;
import com.example.dan.ted.TED.common.FragmentChangeInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Event_Details.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Event_Details#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Event_Details extends Fragment implements FragmentChangeInterface{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View mainView;
    private Button mMeetSpeakersButton;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void fragmentBecameVisible() {
        System.out.println("TestFragment");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Event_Details.
     */
    // TODO: Rename and change types and number of parameters
    public static Event_Details newInstance(String param1, String param2) {
        Event_Details fragment = new Event_Details();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Event_Details() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_event_details, container, false);
        mMeetSpeakersButton = (Button) mainView.findViewById(R.id.button_meet_speakers);
        mMeetSpeakersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Fragment newFragment = new ImageListFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, newFragment);

                transaction.addToBackStack(null);
                transaction.commit();
*/
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageListFragment.INDEX);
                startActivity(intent);
               // getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();

            }
        });
        return mainView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
     /*   if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    /*    try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    // public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    //     public void onFragmentInteraction(Uri uri);
    // }

}
