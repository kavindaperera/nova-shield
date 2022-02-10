package com.nova.android.shield.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.nova.android.shield.utils.Constants;

import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class ShieldPreferencesHelper {

    public static final SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    }

    public static void setBluetoothEnabled(Context context) {
        setBluetoothEnabled(context, true);
    }

    public static boolean isBluetoothEnabled(Context context) {
        return getSharedPreferences(context).getBoolean(Constants.PREFS_BLUETOOTH_ENABLED, false);
    }

    public static void setBluetoothEnabled(Context context, boolean val) {
        getSharedPreferences(context).edit().putBoolean(Constants.PREFS_BLUETOOTH_ENABLED, val).apply();
    }

    public static void setUsername(Context context, String username) {
        getSharedPreferences(context).edit().putString(Constants.PREFS_USERNAME, username).apply();
    }

    public static void setUserUuid(Context context, String uuid) {
        getSharedPreferences(context).edit().putString(Constants.PREFS_USER_UUID, uuid).apply();
    }

    public static UUID getUserUuid(Context context) {
        return  UUID.fromString(getSharedPreferences(context).getString(Constants.PREFS_USER_UUID, null));
    }

}
