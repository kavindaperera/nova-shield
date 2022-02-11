package com.nova.android.shield.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.nova.android.shield.R;
import com.nova.android.shield.ble.BluetoothUtils;
import com.nova.android.shield.main.ShieldApp;
import com.nova.android.shield.main.ShieldConstants;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.service.ShieldService;
import com.nova.android.shield.ui.home.TabbedMainActivity;

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

    public static void sendNotification(Context context, String title, String text, int type) {

        if (ShieldPreferencesHelper.isNotificationEnabled(context)){

            Bitmap largeIcon = BitmapFactory.decodeResource(ShieldApp.getInstance().getResources(), ShieldConstants.drawable.shld_launcher);
            NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context.getApplicationContext(), Constants.NOTIFICATION_CHANNEL);

            Intent clickIntent = new Intent(context.getApplicationContext(), TabbedMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(text);
            bigText.setBigContentTitle(title);

            notifBuilder.setContentIntent(pendingIntent);
            notifBuilder.setSmallIcon(ShieldConstants.drawable.shld);
            notifBuilder.setContentTitle(title);
            notifBuilder.setContentText(text);
            notifBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
            notifBuilder.setStyle(bigText);

            switch (type){
                case 1:{
                    notifBuilder.setColor(ContextCompat.getColor(context, R.color.verify_error)); // too close warning
                }
                case 2:{
                    // do nothing
                }
            }


            if (Build.VERSION.SDK_INT >= 21) {
                notifBuilder.setLargeIcon(largeIcon);
            }

            ((NotificationManager) ShieldApp.getInstance().getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, notifBuilder.build());

        }

    }

}
