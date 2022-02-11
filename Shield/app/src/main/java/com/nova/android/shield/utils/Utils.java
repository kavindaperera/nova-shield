package com.nova.android.shield.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.nova.android.shield.ble.BluetoothUtils;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.service.ShieldService;

public class Utils {
    public static boolean hasBlePermissions(Context context) { // check for ble permissions
        if (!(context == null || Constants.BLE_PERMISSIONS == null)) {
            for (String permission : Constants.BLE_PERMISSIONS) {
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

    public static void startShieldingService(Activity activity) {
        if (Constants.ShieldingServiceRunning) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            activity.startForegroundService(new Intent(activity, ShieldService.class));
        } else {
            activity.startService(new Intent(activity, ShieldService.class));
        }
    }

    public static void stopShieldingService(Activity activity, View view) {

        if (Constants.ShieldingServiceRunning && view != null) {
            // show toast or snack bar
        }

        if (Constants.ShieldingServiceRunning) {
            activity.stopService(new Intent(activity, ShieldService.class));
        }

        BluetoothUtils.stopBle(activity);
        ShieldPreferencesHelper.setBluetoothEnabled(activity, false);
    }


}
