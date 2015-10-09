package com.example.dan.ted.TED;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.FragmentChangeInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link More_Info.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link More_Info#newInstance} factory method to
 * create an instance of this fragment.
 */
public class More_Info extends Fragment implements FragmentChangeInterface{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View mainView;
    Button emailButton;
    private EditText editText;

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
     * @return A new instance of fragment More_Info.
     */
    // TODO: Rename and change types and number of parameters
    public static More_Info newInstance(String param1, String param2) {
        More_Info fragment = new More_Info();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public More_Info() {
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
        mainView = inflater.inflate(R.layout.fragment_more_info, container, false);
        editText = (EditText) mainView.findViewById(R.id.more_info_edittext);
        emailButton = (Button) mainView.findViewById(R.id.email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"j.spiker49@csuohio.edu"});
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject);
                intent.putExtra(Intent.EXTRA_TEXT, editText.getText());
                try {
                    startActivity(Intent.createChooser(intent, "Send email"));
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mainView.getContext(), "There are no email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return mainView;
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
       /* try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
     //   mListener = null;
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
