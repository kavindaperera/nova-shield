package com.nova.android.shield.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.nova.android.ble.api.BleManager;
import com.nova.android.ble.api.Device;
import com.nova.android.ble.api.callback.StateListener;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.utils.Constants;
import com.nova.android.shield.utils.TimeUtils;

import java.util.HashMap;
import java.util.HashSet;

public class BluetoothUtils {

    private static final String TAG = "[Nova][Shield][BleUtils]";

    public static boolean isBluetoothOn() { // check whether bluetooth is ON
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    public static final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (!action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                return;
            }

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            int state =  mBluetoothAdapter.getState();
            switch (state){
                case BluetoothAdapter.STATE_ON: {
                    Log.d(TAG, "BluetoothAdapter.STATE_ON");
                    ShieldPreferencesHelper.setBluetoothEnabled(context);
                    break;
                }
                case BluetoothAdapter.STATE_OFF: {
                    Log.d(TAG, "BluetoothAdapter.STATE_OFF");
                    ShieldPreferencesHelper.setBluetoothEnabled(context, false);
                    break;
                }
            }
        }
    };

    public static final StateListener stateListener = new StateListener() {
        @Override
        public void onStartError(@NonNull String message, @NonNull int errorCode) {
            Log.e(TAG, "onStartError(): ");

        }

        @Override
        public void onStarted() {
            Log.e(TAG, "onStarted(): ");

        }

        @Override
        public void onRssiRead(@NonNull Device device, int rssi) {
            Log.e(TAG, "onRssiRead(): " + "| uuid: " + device.getUserId() + " | RSSI: " + rssi);

            String contactUuid = device.getUserId();
            int contactRssi = rssi;
            Constants.scanResultsUUIDs.add(contactUuid);
            Constants.scanResultsUUIDsRSSIs.put(contactUuid, contactRssi);
            Constants.scanResultsUUIDsTimes.put(contactUuid, Long.valueOf(TimeUtils.getTime()));
        }
    };

    public static void startBle(Context context) {
        Constants.scanResultsUUIDs = new HashSet<>();
        Constants.scanResultsUUIDsRSSIs = new HashMap<>();
        Constants.scanResultsUUIDsTimes = new HashMap<>();

        BleManager.initialize(context, ShieldPreferencesHelper.getUserUuid(context));
        BleManager.start(stateListener);
    }

    public static void stopBle(Context context) {
        BleManager.stop();
    }

    private static boolean checkRssiThreshold() {

        //pass to machine learning model

        return true;
    }

}
