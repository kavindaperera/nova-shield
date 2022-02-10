package com.nova.android.ble.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.nova.android.ble.api.callback.StateListener;
import com.nova.android.ble.logs.Log;

public class BleCore {

    /*Shared Preference Keys*/
    public static final String PREFS_NAME = "com.nova.android.ble.client";
    public static final String PREFS_USER_UUID = "com.nova.android.ble.uuid";
    public static final String PREFS_APP_KEY = "com.nova.android.ble.APP_KEY";

    private static final String TAG = "[Nova][Ble][BleCore]";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Context context;
    private StateListener stateListener;
    private BleReceiver bleReceiver;

    public BleCore(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(BleCore.PREFS_NAME, 0);
        this.editor = sharedPreferences.edit();
        this.bleReceiver = new BleReceiver(context);
    }

    public void initializeServices() {
        Log.d(TAG, "initializeServices(): ");
        this.bleReceiver.registerReceiver(this.context);
        this.bleReceiver.startServer();
        this.bleReceiver.startDiscovery();
    }

    public void shutdownServices() {
        Log.d(TAG, "shutdownServices():");

    }

    public SharedPreferences getSharedPreferences() {
        return this.sharedPreferences;
    }

    public SharedPreferences.Editor getEditor() {
        return this.editor;
    }

    public Context getContext() {
        return this.context;
    }

    public StateListener getStateListener() {
        return this.stateListener;
    }

    public void setStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

}
