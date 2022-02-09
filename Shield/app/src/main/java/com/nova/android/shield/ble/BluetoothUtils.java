package com.nova.android.shield.ble;

import android.bluetooth.BluetoothAdapter;

public class BluetoothUtils {
    public static boolean isBluetoothOn() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return false;
        }
        return true;
    }
}
