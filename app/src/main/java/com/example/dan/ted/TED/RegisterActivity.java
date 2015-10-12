package com.example.dan.ted.TED;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dan.ted.R;
import com.example.dan.ted.TED.common.Constants;
import com.example.dan.ted.TED.common.UserRequest;
import com.example.dan.ted.TED.common.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * A register screen that offers register via email/password.
 */
public class RegisterActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {
    // private UserRegisterTask mAuthTask = null;

    private static final int CAMERA_REQUEST = 1337;
    private static final String url = Constants.url;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmationView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mAffiliationView;
    private EditText mPhoneView;
    private ImageView mPhotoView;
    private Button mEmailRegisterButton;
    private Button mAddPhotoButton;
    private View mProgressView;
    private View mRegisterFormView;
    private String mCapturedImagePath;
    private Uri outputFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the toolbar
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.header3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(logo);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child != null)
                if (child.getClass() == ImageView.class) {
                    ImageView iv2 = (ImageView) child;
                    if (iv2.getDrawable() == logo) {
                        iv2.setAdjustViewBounds(true);
                    }
                }
        }

        // Set up the register form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordConfirmationView = (EditText) findViewById(R.id.password_confirmation);
        mFirstNameView = (EditText) findViewById(R.id.first_name);
        mLastNameView = (EditText) findViewById(R.id.last_name);
        mAffiliationView = (EditText) findViewById(R.id.affiliation);
        mPhoneView = (EditText) findViewById(R.id.phone);
        mPhotoView = (ImageView) findViewById(R.id.photo);
        mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mAddPhotoButton = (Button) findViewById(R.id.photo_button);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String email = extras.getString("email");
            String password = extras.getString("password");
            mEmailView.setText(email);
            mPasswordView.setText(password);
            View focusView = mEmailView;
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                focusView = mPasswordConfirmationView;
            }
            focusView.requestFocus();
        }

        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
        mAddPhotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
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

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            File file = new File(mCapturedImagePath);
            mPhotoView.setImageURI(Uri.fromFile(file));
            mPhotoView.setVisibility(View.VISIBLE);
            mAddPhotoButton.setText(getString(R.string.register_change_photo));
        }
        else
            Toast.makeText(RegisterActivity.this, "Camera error: " + resultCode, Toast.LENGTH_SHORT).show();
    }

      /**
     * Attempts to register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual register attempt is made.
     */
    public void attemptRegister() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mFirstNameView .setError(null);
        mLastNameView.setError(null);
        mAffiliationView.setError(null);
        mPhoneView.setError(null);

        // Store values at the time of the register attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirmation = mPasswordConfirmationView.getText().toString();
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String affiliation = mAffiliationView.getText().toString();
        String phone = mPhoneView.getText().toString();

        boolean cancel = false;
        View focusView = null;
//TODO: Low priority: Turn these into a method call for each (aside from Password, which is special)
        //Check for a valid phone number.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        } else if (!Utility.isPhoneValid(phone)) {
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneView;
            cancel = true;
        }

        //Check for a valid affiliation.
        if (TextUtils.isEmpty(affiliation)) {
            mAffiliationView.setError(getString(R.string.error_field_required));
            focusView = mAffiliationView;
            cancel = true;
        } else if (!Utility.isAffiliationValid(affiliation)) {
            mAffiliationView.setError(getString(R.string.error_invalid_affiliation));
            focusView = mAffiliationView;
            cancel = true;
        }

        //Check for a valid last name.
        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        } else if (!Utility.isNameValid(lastName)) {
            mLastNameView.setError(getString(R.string.error_invalid_name));
            focusView = mLastNameView;
            cancel = true;
        }

        //Check for a valid first name.
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        } else if (!Utility.isNameValid(firstName)) {
            mFirstNameView.setError(getString(R.string.error_invalid_name));
            focusView = mFirstNameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one. Then, check if password and confirmation match.
        if (TextUtils.isEmpty(password) || !Utility.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!password.equals(passwordConfirmation)) {
            mPasswordView.setError(getString(R.string.error_confirmation_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Utility.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            showProgress(true);
            UserRegister(email, password, firstName, lastName, affiliation, phone);
        }
    }

    private boolean isAffiliationValid(String affiliation) {
        return (affiliation.matches("[a-zA-Z0-9 -]*") && affiliation.length() > 1);
    }

    private boolean isPhoneValid(String phone) {
        return (phone.matches("[0-9 ()+-]*") && phone.length() > 1);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isNameValid(String name) {
        return (name.matches("[a-zA-Z -]*") && name.length() > 1);
    }

    /**
     * Shows the progress UI and hides the register form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) { }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    public void UserRegister(final String email, final String password, final String firstName,
                             final String lastName, final String affiliation, final String phone) {
        final Context context = getApplicationContext();
        AsyncHttpClient client = new AsyncHttpClient();
        //Replace with a more secure root user/pass method
        client.setBasicAuth("yxsn4kHuZq-936ZM", "YqArG33c-BF4t6xL");
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("email", email.trim());
            jsonParams.put("password", password.trim());
            jsonParams.put("first_name", firstName.trim());
            jsonParams.put("last_name", lastName.trim());
            jsonParams.put("phone", phone.trim());
            jsonParams.put("affiliation", affiliation.trim());
        }
        catch (JSONException e) {
            Toast.makeText(RegisterActivity.this, "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(RegisterActivity.this, "StringEntity Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        String URL = url + "users";
        client.post(context, URL, entity, "application/json",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] response) {
                        try {
                            //JSON Object
                            String str = new String(response);
                            JSONObject obj = new JSONObject(str);
                            String photoPostURL = obj.getString("photo_url"); //This photo URL is the URL for posting pictures. It is not the user's photoURL.
                            if (mCapturedImagePath != null) {
                                UserRequest.postImage(context, mCapturedImagePath, photoPostURL, email, password);
                            } else
                                UserRequest.getToken(context, email, password);
                        } catch (JSONException e) {
                            Toast.makeText(RegisterActivity.this, "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] response, Throwable throwable) {
                        if (response != null) {
                            String str = new String(response);
                            try {
                                JSONObject obj = new JSONObject(str);
                                String message = obj.getString("message");
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(RegisterActivity.this, "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Timed out.", Toast.LENGTH_SHORT).show();
                        }
                        showProgress(false);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.hold, R.anim.fade_out);
    }
}

