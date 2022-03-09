package com.nova.android.shield.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nova.android.shield.R;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;

public class PermissionLogic {

    private static final String TAG = "[Nova][Shield][PermissionLogic]";

    public static void permissionLogic(int requestCode, String[] permissions, int[] grantResults, Activity activity) {
        Log.i(TAG, "on request permission " + requestCode);
        for (int i2 = 0; i2 < grantResults.length; i2++) {
            Log.i(TAG, "grant results " + permissions[i2] + ", " + grantResults[i2]);
        }
        int fineLocationResult = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (!permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (fineLocationResult == -1) {
                boolean shouldAsk = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);
                if (shouldAsk) {
                    Log.e(TAG, "should ask");
                    showRationaleDialog(activity, requestCode, Manifest.permission.ACCESS_FINE_LOCATION);
                } else {
                    Log.e(TAG, "should open settings");
                    showOpenSettingsDialog(activity, Manifest.permission.ACCESS_FINE_LOCATION, requestCode);
                }
            }
        }
    }

    private static void showRationaleDialog(final Activity activity, final int requestCode, final String permission) {
        Log.i(TAG, "requestCode: " + requestCode);
        String msg = activity.getString(R.string.permission_location_rationale);
        new MaterialAlertDialogBuilder(activity).setTitle((CharSequence) "Permission denied")
                .setMessage((CharSequence) msg)
                .setNegativeButton((int) R.string.retry, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, new String[]{permission}, 2);
                    }
                })
                .setPositiveButton(R.string.sure, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (requestCode == 1) {
                            ShieldPreferencesHelper.setBluetoothEnabled(activity, false);
                        }
                    }
                }).setCancelable(false).create().show();
    }

    public static void showOpenSettingsDialog(final Activity activity, String permission, final int requestCode) {
        Log.i(TAG, "requestCode: " + requestCode);
        String msg = activity.getString(R.string.permission_location_ask);
        new MaterialAlertDialogBuilder(activity)
                .setTitle((CharSequence) "Permission denied")
                .setMessage((CharSequence) msg)
                .setNegativeButton((int) R.string.no, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (requestCode == 1) {
                            ShieldPreferencesHelper.setBluetoothEnabled(activity, false);
                        }
                    }
                }).setPositiveButton((int) R.string.yes, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", activity.getPackageName(), (String) null));
                activity.startActivity(intent);
            }
        }).setCancelable(false).create().show();
    }
}
