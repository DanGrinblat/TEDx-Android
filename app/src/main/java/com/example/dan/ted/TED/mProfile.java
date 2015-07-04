package com.example.dan.ted.TED;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.SessionManager;

import org.w3c.dom.Text;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link mProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link mProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mProfile extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView mNameView;
    private TextView mEmailView;
    private TextView mPhoneView;
    private TextView mAffiliationView;
    private TextView mPhotoView;
    private Button mEditButton;
    private TextView mDialogEmailView;
    private TextView mDialogPhoneView;
    private TextView mDialogAffiliationView;
    private ImageView mDialogPhotoView;
    private Button mDialogPhotoButton;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    Context context;
    SessionManager session;
    SharedPreferences pref;
    ViewGroup parent;

    //private OnFragmentInteractionListener mListener;

    public static mProfile newInstance(String param1, String param2) {
        mProfile fragment = new mProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public mProfile() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity();
        session = new SessionManager(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (session.isLoggedIn()) {
            populateViews();
        }
    }

    public void populateViews() {
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManager.KEY_NAME);
        String email = user.get(SessionManager.KEY_EMAIL);
        mNameView.setText(getString(R.string.profile_hello) + " " + name);
        mEmailView.setText(getString(R.string.profile_email) + " " + email);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mprofile, container, false);
        mNameView = (TextView) view.findViewById(R.id.hello);
        mEmailView = (TextView) view.findViewById(R.id.email);
        mPhoneView = (TextView) view.findViewById(R.id.phone);
        mAffiliationView = (TextView) view.findViewById(R.id.affiliation);
        mPhotoView = (TextView) view.findViewById(R.id.photo);
        parent = (ViewGroup) view.findViewById(R.id.email_register_form);


        mEditButton = (Button) view.findViewById(R.id.edit_profile);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditButtonPressed();
            }
        });
        return view;
    }

    public void onEditButtonPressed() {
        alertDialogBuilder =
                new AlertDialog.Builder(context)
                        .setTitle("Edit Profile")
                        .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_profile, parent, false ))
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing
                            }
                        });
        mDialogEmailView = (TextView) alertDialog.findViewById(R.id.dialog_email);
        mDialogPhoneView = (TextView) alertDialog.findViewById(R.id.dialog_phone);
        mDialogAffiliationView = (TextView) alertDialog.findViewById(R.id.dialog_affiliation);
        mDialogPhotoView = (ImageView) alertDialog.findViewById(R.id.dialog_imageview_photo);
        mDialogPhotoButton = (Button) alertDialog.findViewById(R.id.dialog_add_photo);

        alertDialog = alertDialogBuilder.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
      /*  if (mListener != null) {
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
       // mListener = null;
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
   /* public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }*/

}
