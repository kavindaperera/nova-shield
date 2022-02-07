package com.nova.android.shield.main;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.data.model.PhoneNumber;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nova.android.shield.auth.ShieldSession;
import com.nova.android.shield.utils.Constants;

public class ShieldFirebaseAuth implements FirebaseAuth.AuthStateListener, FirebaseAuth.IdTokenListener {

    private static String TAG = "[Nova][Shield][FirebaseAuth]";
    protected SharedPreferences settings;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private PhoneNumber phoneNumber;

    public ShieldFirebaseAuth(Context context) {
        this.context = context;
        FirebaseAuth instance = FirebaseAuth.getInstance();
        this.firebaseAuth = instance;
        this.firebaseAuth.addIdTokenListener(this);
        this.firebaseAuth.addAuthStateListener(this);
        this.settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            String phoneNumber = firebaseUser.getPhoneNumber();
            if (phoneNumber != null && !phoneNumber.trim().equals("")) {
                ShieldSession.setPhoneNumber(phoneNumber);
                this.settings.edit().putString(Constants.PREFS_USER_PHONE, phoneNumber).apply();
            }
        }

    }

    @Override
    public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {

    }
}
