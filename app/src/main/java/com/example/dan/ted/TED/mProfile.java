package com.example.dan.ted.TED;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.Constants;
import com.example.dan.ted.TED.common.FragmentChangeInterface;
import com.example.dan.ted.TED.common.SessionManager;
import com.example.dan.ted.TED.common.UserRequest;
import com.example.dan.ted.TED.common.Utility;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link mProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link mProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mProfile extends Fragment implements FragmentChangeInterface {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String IMAGE_EXTENSION = ".jpg";
    private static final String url = Constants.url;
    private static final int CAMERA_REQUEST = 1337;

    private Uri outputFileUri;
    private String mCapturedImagePath;
    private String mParam1;
    private String mParam2;
    private String photoURL;
    private String email;
    private boolean newPhoto = false;
    private View mainView;
    private TextView mNameView;
    private TextView mEmailView;
    private TextView mPhoneView;
    private TextView mAffiliationView;
    private TextView mPhotoView;
    private ImageView mPhotoImageView;
    private Button mEditButton;
    private Button mDialogPhotoButton;
    private ImageView mDialogPhotoView;
    private TextView mDialogPhotoTextView;

    AlertDialog alertDialog;
    Context context;
    SessionManager session;
    ViewGroup parent;

    @Override
    public void fragmentBecameVisible() {
        System.out.println("TestFragment");
    }

    public static mProfile newInstance(String param1, String param2) {
        mProfile fragment = new mProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public mProfile() {
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_mprofile, container, false);
        mNameView = (TextView) mainView.findViewById(R.id.hello);
        mEmailView = (TextView) mainView.findViewById(R.id.email);
        mPhoneView = (TextView) mainView.findViewById(R.id.phone);
        mAffiliationView = (TextView) mainView.findViewById(R.id.affiliation);
        mPhotoView = (TextView) mainView.findViewById(R.id.photo);
        mPhotoImageView = (ImageView) mainView.findViewById(R.id.profile_imageview_photo);
        parent = (ViewGroup) mainView.findViewById(R.id.email_register_form);

        mEditButton = (Button) mainView.findViewById(R.id.edit_profile);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditButtonPressed();
            }
        });
        populateViews();
        return mainView;
    }

    public void populateViews() {
        HashMap<String, Object> user = session.getUserDetails();
        String name = (String) user.get(SessionManager.KEY_NAME);
        email = (String) user.get(SessionManager.KEY_EMAIL);
        String phone = (String) user.get(SessionManager.KEY_PHONE);
        String affiliation = (String) user.get(SessionManager.KEY_AFFILIATION);
        mNameView.setText(getString(R.string.profile_hello) + " " + name);
        mEmailView.setText(getString(R.string.profile_email) + " " + email);
        mPhoneView.setText(getString(R.string.profile_phone) + " " + phone);
        mAffiliationView.setText(getString(R.string.profile_affiliation) + " " + affiliation);

        if (!(TextUtils.isEmpty((String)user.get(SessionManager.KEY_PHOTO_URL)))) {
            photoURL = Constants.baseUrl + (String) user.get(SessionManager.KEY_PHOTO_URL);
            Log.e("tag", photoURL);
            Picasso.with(context)
                    .load(photoURL)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    //.fit()
                    //.centerCrop()
                    .resize(800,800)
                    .centerInside()
                    .placeholder(R.drawable.ic_ted_loading)
                    .into(mPhotoImageView);
            mPhotoImageView.setVisibility(View.VISIBLE);
            mPhotoView.setText(getString(R.string.profile_photo));
        } else
            mPhotoView.setText(R.string.profile_no_photo);
    }

    public void onEditButtonPressed() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_profile, null);
        final EditText mDialogPassword = (EditText) view.findViewById(R.id.dialog_old_password);
        final EditText mDialogNewPassword = (EditText) view.findViewById(R.id.dialog_new_password);
        final EditText mDialogNewPasswordConf = (EditText) view.findViewById(R.id.dialog_new_password_conf);
        final EditText mDialogEmailView = (EditText) view.findViewById(R.id.dialog_email);
        final EditText mDialogPhoneView = (EditText) view.findViewById(R.id.dialog_phone);
        final EditText mDialogAffiliationView = (EditText) view.findViewById(R.id.dialog_affiliation);
        final EditText[] editFields = new EditText[]{mDialogPassword, mDialogNewPassword, mDialogNewPasswordConf,
                mDialogEmailView, mDialogPhoneView, mDialogAffiliationView};

        mDialogPhotoView = (ImageView) view.findViewById(R.id.dialog_imageview_photo);
        mDialogPhotoTextView = (TextView) view.findViewById(R.id.dialog_photo);
        mDialogPhotoButton = (Button) view.findViewById(R.id.dialog_add_photo);
        mDialogPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/TEDxCSU");
                    if (!folder.exists())
                        folder.mkdirs();
                    File image_file = new File(folder.toString(), "thumbnail.jpg");
                    mCapturedImagePath = image_file.getAbsolutePath();
                    outputFileUri = Uri.fromFile(image_file);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        if (!TextUtils.isEmpty(photoURL)) {
            mDialogPhotoButton.setText(getString(R.string.register_change_photo));
            Picasso.with(context)
                    .load(photoURL)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    //.fit()
                    //.centerCrop()
                    .resize(800,800)
                    .centerInside()
                    .placeholder(R.drawable.ic_ted_loading)
                    .into(mDialogPhotoView);
            mDialogPhotoView.setVisibility(View.VISIBLE);
            mDialogPhotoTextView.setText(R.string.profile_photo);
        }


        alertDialog =
                new AlertDialog.Builder(context)
                        .setTitle("Edit Profile") //TODO: Create strings for these
                        .setView(view)
                        .setPositiveButton("Cancel", null)
                        .setNegativeButton("Save", null)
                        .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button cancel = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newPhoto = false;
                        alertDialog.dismiss();
                    }
                });
                Button save = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean cancel = false;
                        View focusView = null;
                        newPhoto = false;
                        mDialogEmailView.setError(null);
                        mDialogPhoneView.setError(null);
                        mDialogAffiliationView.setError(null);
                        mDialogPassword.setError(null);
                        mDialogNewPassword.setError(null);
                        String oldPassword = mDialogPassword.getText().toString();
                        String newPassword = mDialogNewPassword.getText().toString();
                        String newPasswordConfirmation = mDialogNewPasswordConf.getText().toString();
                        String newEmail = mDialogEmailView.getText().toString();
                        String newPhone = mDialogPhoneView.getText().toString();
                        String newAffiliation = mDialogAffiliationView.getText().toString();

                        if (TextUtils.isEmpty(oldPassword) || !Utility.isPasswordValid(oldPassword)) {
                            mDialogPassword.setError(getString(R.string.error_invalid_password));
                            focusView = mDialogPassword;
                            cancel = true;
                        } else if (!TextUtils.isEmpty(newPassword) && !Utility.isPasswordValid(newPassword)) {
                            mDialogNewPassword.setError(getString(R.string.error_invalid_password));
                            focusView = mDialogNewPassword;
                            cancel = true;
                        } else if (!TextUtils.isEmpty(newPassword) && !newPassword.equals(newPasswordConfirmation)) {
                            mDialogNewPassword.setError(getString(R.string.error_confirmation_password));
                            focusView = mDialogNewPassword;
                            cancel = true;
                        }

                        if (!TextUtils.isEmpty(newAffiliation) && !Utility.isAffiliationValid(newAffiliation)) {
                            mDialogAffiliationView.setError(getString(R.string.error_invalid_affiliation));
                            focusView = mDialogAffiliationView;
                            cancel = true;
                        }

                        if (!TextUtils.isEmpty(newPhone) && !Utility.isPhoneValid(newPhone)) {
                            mDialogPhoneView.setError(getString(R.string.error_invalid_phone));
                            focusView = mDialogPhoneView;
                            cancel = true;
                        }

                        if (!TextUtils.isEmpty(newEmail) && !Utility.isEmailValid(newEmail)) {
                            mDialogEmailView.setError(getString(R.string.error_invalid_email));
                            focusView = mDialogEmailView;
                            cancel = true;
                        }

                        if (((allEmpty(editFields)) && TextUtils.isEmpty(mCapturedImagePath))) {
                            alertDialog.dismiss();
                        } else {
                            if (cancel)
                                focusView.requestFocus();
                            else {
                                UserRequest.put(context, oldPassword, newPassword, email, newEmail, newPhone, newAffiliation, mCapturedImagePath);
                                /*Picasso.with(context)
                                        .load(photoURL)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .fit()
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_ted_loading)
                                        .into(mPhotoImageView);
                                Picasso.with(context)
                                        .load(photoURL)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .fit()
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_ted_loading)
                                        .into(mDialogPhotoView);*/
                            }
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    private boolean allEmpty(EditText[] fields) {
        for (int i = 0; i < fields.length; i++) {
            EditText currentField = fields[i];
            if (!TextUtils.isEmpty(currentField.getText().toString()))
                return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            File file = new File(mCapturedImagePath);
            mDialogPhotoView.setImageURI(Uri.fromFile(file));
            mDialogPhotoView.setVisibility(View.VISIBLE);
            mDialogPhotoButton.setText(getString(R.string.register_change_photo));
            newPhoto = true;
        } else {
            //Toast.makeText(context, "Camera error: " + resultCode, Toast.LENGTH_SHORT).show();
            mCapturedImagePath = null;
        }
    }

    public void onButtonPressed(Uri uri) {
      /*  if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
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