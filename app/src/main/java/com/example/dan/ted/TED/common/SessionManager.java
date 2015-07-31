package com.example.dan.ted.TED.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.dan.ted.TED.LoginActivity;

import java.util.HashMap;

/**
 * Created by Dan on 6/27/2015.
 */
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_AFFILIATION = "affiliation";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_PHOTO_URL = "photo_url";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String email, String name, String phone,
                                   String affiliation, String token, int id, String photoUrl){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing values in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_AFFILIATION, affiliation);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_PHOTO_URL, photoUrl);

        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, Object> getUserDetails(){
        HashMap<String, Object> user = new HashMap<String, Object>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_ID, pref.getInt(KEY_ID, 0));
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));
        user.put(KEY_AFFILIATION, pref.getString(KEY_AFFILIATION, null));
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        user.put(KEY_PHOTO_URL, pref.getString(KEY_PHOTO_URL, null));

        return user;
    }

    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // if user is not logged in, redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Starting Login Activity
            _context.startActivity(i);
        }
    }
/**
 * Clear session details
 * */
        public void logoutUser(){
            // Clearing all data from Shared Preferences
            editor.clear();
            editor.commit();

            // After logout redirect user to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    // Get Login State
    public boolean isLoggedIn(){
        getUserDetails().get("token");

        return pref.getBoolean(IS_LOGIN, false);
    }
}
