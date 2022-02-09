package com.nova.android.shield.ui.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;


import com.nova.android.shield.ble.BluetoothUtils;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.utils.Constants;
import com.nova.android.shield.utils.Utils;

public class PermissionUtils {

    private static final String TAG = "[Nova][Shield][PermissionUtils]";

    @SuppressLint("MissingPermission")
    public static void bluetoothSwitchLogic(Activity activity) {
        Constants.bluetoothAdapter = ((BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (!Utils.hasBlePermissions(activity) || !BluetoothUtils.isBluetoothOn()) {
            if (Constants.bluetoothAdapter != null && !Constants.bluetoothAdapter.isEnabled()) {
                activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
            }
            return;
        }

        Log.i(TAG, "bluetoothSwitchLogic(): bluetooth_enabled = true");
        ShieldPreferencesHelper.setBluetoothEnabled(activity);
    }

}
