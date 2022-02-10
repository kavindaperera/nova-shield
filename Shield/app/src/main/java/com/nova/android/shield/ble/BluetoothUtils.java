package com.nova.android.shield.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.nova.android.ble.api.BleManager;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;

public class BluetoothUtils {
    public static boolean isBluetoothOn() { // check whether bluetooth is ON
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    public static void startBle(Context context) {
        BleManager.initialize(context, ShieldPreferencesHelper.getUserUuid(context));
    }

    public static void haltBle(Context context) {

    }

}
