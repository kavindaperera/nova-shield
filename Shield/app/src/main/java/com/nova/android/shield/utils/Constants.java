package com.nova.android.shield.utils;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;

import com.nova.android.shield.R;
import com.nova.android.shield.logs.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class Constants {

    private static final String TAG = "[Nova][Shield][Constants]";

    public static BluetoothAdapter bluetoothAdapter;
    public static boolean BLUETOOTH_ENABLED = false;
    public static String[] BLE_PERMISSIONS = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH};
    public static HashMap<Integer, Integer> bleThresholds = new HashMap<>();
    public static final String[] DAYS_OF_WEEK = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    public static boolean DEBUG = true; // TODO - set to false in production
    public static final String DEEP_LINK_QR = "qr";
    public static int deviceID;
    public static List<String> deviceNames = new LinkedList();
    public static final int FOREGROUND_SERVICE = 444;
    public static final String LOCATION_PERMISSION = "locationPermission";
    public static List<String> manufacturerNames = new LinkedList();
    public static final String MSG_DATE_YESTERDAY = "Yesterday";
    public static final String NOTIFICATION_CHANNEL = "shield_channel";
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String PACKAGE_NAME = "com.nova.android.shield";
    public static final String PREFS_CONFIG_PROFILE = "settings_profile";
    public static final String PREFS_FIRST_DATE = "first_date";
    public static final String PREFS_NAME = "com.nova.android.shield.app";
    public static final String PREFS_LOGS_ENABLED = "settings_logs_enabled";
    public static final String PREFS_NOTIFICATION_ENABLED = "settings_notifications_enabled";
    public static final String PREFS_NOTIFICATION_SOUND = "notificationSound";
    public static final String PREFS_BLUETOOTH_ENABLED = "bluetooth_enabled";
    public static final String PREFS_LAST_CHECKED = "last_checked_time";
    public static final String PREFS_USERNAME = "username";
    public static final String PREFS_USER_PHONE = "user_phone";
    public static final String PREFS_USER_UUID = "user_uuid";
    public static final String PREFS_WHITELIST = "whitelisted_devices";
    public static int PULL_FROM_FIREBASE_INTERVAL = (15 * 60) * 1000;
    public static boolean PullFromFirebaseServiceRunning = false;
    public static long SCAN_RESULTS_RESET_INTERVAL = 5000L;
    public static ConcurrentSkipListSet<String> scanResultsUUIDs;
    public static ConcurrentHashMap<String, Integer> scanResultsUUIDsRSSIs;
    public static ConcurrentHashMap<String, Long> scanResultsUUIDsTimes;
    public static boolean ShieldingServiceRunning = false;
    public static int SPLASH_DISPLAY_DURATION = 1500;
    public static int defaultRssiThreshold= -80;

    public enum DatabaseOps {
        INSERT,
        VIEW_ALL,
        DELETE,
        DELETE_ALL
    }

    public enum NotifType {
        EXPOSURE,
        DISTANCE_REMINDER
    }

    public static void init(Context ctx) {
        bleThresholds = FileUtils.readDeviceThresholds(ctx, R.raw.rssi_threshold_data);
        manufacturerNames = FileUtils.readManufacturerList(ctx, R.raw.rssi_threshold_data);
        deviceNames = FileUtils.readDeviceList(ctx, R.raw.rssi_threshold_data);
        getDeviceID();
        Log.i(TAG,"deviceId: " + deviceID);
    }

    private static void getDeviceID() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        String model = Build.MODEL.toLowerCase();

        if (!manufacturerNames.contains(manufacturer)) {
            deviceID = 0;
            return;
        }

        int counter = 1;
        for (String deviceName : deviceNames) {
            if (model.equals(deviceName)) {
                deviceID = counter;
                return;
            }
            counter++;
        }

    }



}
