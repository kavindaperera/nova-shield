package com.nova.android.shield.utils;

import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;

import com.nova.android.shield.ble.BluetoothUtils;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;

public class Utils {
    public static boolean hasBlePermissions(Context context) { // check for ble permissions
        if (!(context == null || Constants.blePermissions == null)) {
            for (String permission : Constants.blePermissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void updateBluetoothSwitchState(Activity activity) {
        boolean hasPermission = hasBlePermissions(activity);
        boolean isBluetoothOn = BluetoothUtils.isBluetoothOn();
        if (!hasPermission || !isBluetoothOn) {
            ShieldPreferencesHelper.setBluetoothEnabled(activity, false);
        }
    }
}
