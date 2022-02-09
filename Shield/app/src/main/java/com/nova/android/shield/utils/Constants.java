package com.nova.android.shield.utils;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;

public class Constants {
    public static BluetoothAdapter bluetoothAdapter;
    public static String[] blePermissions = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH};
    public static final int FOREGROUND_SERVICE = 444;
    public static final String NOTIFICATION_CHANNEL = "shield_channel";
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String SHIELD_APP_BACKGROUND = "com.nova.android.shield.main.service.background";
    public static final String SHIELD_APP_FOREGROUND = "com.nova.android.shield.main.service.foreground";
    public static final String SHIELD_DEVICE = "shieldDevice";
    public static final String SHIELD_STOP = "com.nova.android.shield.main.service.stop";
    public static int SPLASH_DISPLAY_DURATION = 1500;
    public static final String PACKAGE_NAME = "com.nova.android.shield";
    public static final String PREFS_CONFIG_PROFILE = "settings_profile";
    public static final String PREFS_FIRST_DATE = "first_date";
    public static final String PREFS_NAME = "com.nova.android.shield.app";
    public static final String PREFS_LOGS_ENABLED = "settings_logs_enabled";
    public static final String PREFS_NOTIFICATION_ENABLED = "settings_notifications_enabled";
    public static final String PREFS_NOTIFICATION_SOUND = "notificationSound";
    public static final String PREFS_BLUETOOTH_ENABLED = "bluetooth_enabled";
    public static final String PREFS_USERNAME = "username";
    public static final String PREFS_USER_PHONE = "user_phone";
    public static final String PREFS_USER_UUID = "user_uuid";
    public static int PULL_FROM_FIREBASE_INTERVAL = (15*60)*1000;
    public static boolean PullFromFirebaseServiceRunning = false;

}
