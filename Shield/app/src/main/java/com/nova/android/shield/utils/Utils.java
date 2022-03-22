package com.nova.android.shield.utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.nova.android.shield.R;
import com.nova.android.shield.ble.BleRecordAsyncOperation;
import com.nova.android.shield.ble.BluetoothUtils;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.main.ShieldApp;
import com.nova.android.shield.main.ShieldConstants;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.service.ShieldService;
import com.nova.android.shield.ui.home.TabbedMainActivity;

public class Utils {

    private static final String TAG = "[Nova][Shield][Utils]";

    public static boolean hasBlePermissions(Context context) { // check for ble permissions
        Log.i(TAG, "check for ble permission");
        if (!(context == null || Constants.BLE_PERMISSIONS == null)) {
            for (String permission : Constants.BLE_PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != 0) {
                    Log.e(TAG, "return false on " + permission);
                    return false;
                }
            }
        }
        Log.i(TAG, "check for location also");
        return hasLocationPermissions(context);
    }

    public static boolean hasLocationPermissions(Context context) {
        Log.i(TAG, "check for location permission");
        if (Build.VERSION.SDK_INT >= 29) {
            if (context == null || Constants.LOCATION_PERMISSIONS == null) {
                return true;
            }
            for (String permission : Constants.LOCATION_PERMISSIONS) {
                int result = ActivityCompat.checkSelfPermission(context, permission);
                if (result != 0) {
                    Log.e(TAG, "return false on " + permission);
                    return false;
                }
            }
            return true;
        } else if (context == null || Constants.LOCATION_PERMISSIONS_LOWER == null) {
            return true;
        } else {
            for (String permission2 : Constants.LOCATION_PERMISSIONS_LOWER) {
                int result2 = ActivityCompat.checkSelfPermission(context, permission2);
                if (result2 != 0) {
                    Log.e(TAG, "return false on " + permission2);
                    return false;
                }
            }
            return true;
        }
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

    public static void sendNotification(Context context, String title, String msg, int type) {

        if (ShieldPreferencesHelper.isNotificationEnabled(context)){

            Bitmap largeIcon = BitmapFactory.decodeResource(ShieldApp.getInstance().getResources(), ShieldConstants.drawable.shld_launcher);
            NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context.getApplicationContext(), Constants.NOTIFICATION_CHANNEL);

            Intent clickIntent = new Intent(context.getApplicationContext(), TabbedMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(msg);
            bigText.setBigContentTitle(title);
            notifBuilder.setContentIntent(pendingIntent);
            notifBuilder.setSmallIcon(ShieldConstants.drawable.shld);
            notifBuilder.setContentTitle(title);
            notifBuilder.setContentText(msg);
            notifBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
            notifBuilder.setStyle(bigText);

            switch (type){
                case 0:{
                    notifBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary)); // default
                    break;
                }
                case 1:{
                    notifBuilder.setColor(ContextCompat.getColor(context, R.color.notif_lvl_1)); // red warning
                    break;
                }
                case 2:{
                    notifBuilder.setColor(ContextCompat.getColor(context, R.color.notif_lvl_2)); // orange warning
                    break;
                }
                case 3:{
                    notifBuilder.setColor(ContextCompat.getColor(context, R.color.notif_lvl_3)); // yellow warning
                    break;
                }
            }

            NotificationManager mNotificationManager = (NotificationManager) ShieldApp.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel("shield_channel_id", "Shield Notifications Channel",  NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
                channel.setShowBadge(true);
                notifBuilder.setChannelId("shield_channel_id");
            }

            if (Build.VERSION.SDK_INT >= 21) {
                notifBuilder.setLargeIcon(largeIcon);
            }

            int notifId = ShieldPreferencesHelper.getNotifId(context);
            mNotificationManager.notify(notifId, notifBuilder.build());
            ShieldPreferencesHelper.setNotifId(context, notifId);

        }
    }

    public static void bleRecordToDatabase(Context context, String uuid, int rssi, long timestamp) {
        new BleRecordAsyncOperation(context, uuid, rssi, timestamp).execute(new Void[0]);
    }

    public static String[] getBlePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            return Constants.BLE_PERMISSIONS;
        }
        String[] out = new String[(Constants.BLE_PERMISSIONS.length + Constants.LOCATION_PERMISSIONS.length)];
        int counter = 0;
        int i = 0;
        while (i < Constants.LOCATION_PERMISSIONS.length) {
            out[counter] = Constants.LOCATION_PERMISSIONS[i];
            i++;
            counter++;
        }
        int i2 = 0;
        while (i2 < Constants.LOCATION_PERMISSIONS.length) {
            out[counter] = Constants.LOCATION_PERMISSIONS[i2];
            i2++;
            counter++;
        }
        return out;
    }

    public static boolean hasNetworkConnection(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        }

        return false;
    }

    public static int argmax(float[] array) {
        float max = array[0];
        int re = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
                re = i;
            }
        }
        return re;
    }

}
