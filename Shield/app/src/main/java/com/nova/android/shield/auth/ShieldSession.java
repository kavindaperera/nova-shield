package com.nova.android.shield.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.nova.android.shield.utils.Constants;

public class ShieldSession {

    private static final String TAG = "[Nova][ShieldSession]";

    private static ShieldSession instance;
    private final String username;
    private String uuid;
    private String phone;
    private SharedPreferences preferences;
    private boolean isRegistered = false;

    private ShieldSession(String username, String uuid, boolean z) {
        this.username = username;
        this.uuid = uuid;
        this.isRegistered = z;
    }

    private ShieldSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        this.preferences = sharedPreferences;
        this.uuid = this.preferences.getString(Constants.PREFS_USER_UUID, (String) null);
        this.username = this.preferences.getString(Constants.PREFS_USERNAME, (String) null);
        this.phone = this.preferences.getString(Constants.PREFS_USER_PHONE, (String) null);
    }

    public static void loadSession(Context context) {
        instance = new ShieldSession(context);
        if (isLoggedIn()) {
            //TODO
        }
    }

    public static boolean isVerified() {
        String str = instance.phone;
        return str != null && !str.trim().equals("");
    }

    public static void setPhoneNumber(String phoneNumber) {
        instance.phone = phoneNumber;
    }

    public static boolean isLoggedIn() {
        return instance.uuid != null;
    }

    public static boolean isRegistered() {
        return instance.isRegistered;
    }

    public static String getUuid() {
        return instance.uuid;
    }

    public static String getUsername() {
        return instance.username;
    }

    public static void setSession(Context context, String username, String uuid) {
        instance = new ShieldSession(username, uuid, isRegistered());
        SharedPreferences.Editor edit = context.getSharedPreferences(Constants.PREFS_NAME, 0).edit();
        edit.putString(Constants.PREFS_USER_UUID, uuid);
        edit.putString(Constants.PREFS_USERNAME, username);
        edit.commit();
    }

}
