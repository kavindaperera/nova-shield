package com.nova.android.shield.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nova.android.shield.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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

    public static boolean isBluetoothEnabled(Context context, boolean defaultFlag) {
        return getSharedPreferences(context).getBoolean(Constants.PREFS_BLUETOOTH_ENABLED, defaultFlag);
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

    public static boolean isNotificationEnabled(Context context) {
        return getSharedPreferences(context).getBoolean(Constants.PREFS_NOTIFICATION_ENABLED, false);
    }

    public static void setNotificationEnabled(Context context, boolean val) {
        getSharedPreferences(context).edit().putBoolean(Constants.PREFS_NOTIFICATION_ENABLED, val).apply();
    }

    public static Boolean getLogPermission(Context context) {
        return  getSharedPreferences(context).getBoolean(Constants.PREFS_LOGS_ENABLED, false);
    }

    public static Boolean addToWhitelist(Context context, String scannedUUID) {
        Set<String> whitelistedSet = new HashSet<>(getWhitelist(context));
        if (whitelistedSet.contains(scannedUUID)){
            return true;
        }
        whitelistedSet.add(scannedUUID);
        getSharedPreferences(context).edit().putStringSet(Constants.PREFS_WHITELIST, whitelistedSet).apply();
        return false;
    }

    public static Set<String> getWhitelist(Context context) {
        return getSharedPreferences(context).getStringSet(Constants.PREFS_WHITELIST, new HashSet<>());
    }

    public static void resetWhitelist(Context context) {
        getSharedPreferences(context).edit().putStringSet(Constants.PREFS_WHITELIST, new HashSet<>()).apply();
    }

    public static void setLastCheckedTime(Context context, long seconds) {
        getSharedPreferences(context).edit().putLong(Constants.PREFS_LAST_CHECKED, seconds).apply();
    }

    public static long getLastCheckedTime(Context context) {
        return  getSharedPreferences(context).getLong(Constants.PREFS_LAST_CHECKED, 0L);
    }
}
