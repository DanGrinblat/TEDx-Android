package com.example.dan.ted.TED.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Dan on 7/5/2015.
 */
public class Utility {
    private Context context;

    public Utility(Context context) {
        this.context = context;
    }

    public static boolean isAffiliationValid(String affiliation) {
        return (affiliation.matches("[a-zA-Z0-9 -]*") && affiliation.length() > 1);
    }

    public static boolean isPhoneValid(String phone) {
        return (phone.matches("[0-9 ()+-]*") && phone.length() > 1);
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isNameValid(String name) {
        return (name.matches("[a-zA-Z -]*") && name.length() > 1);
    }
}
