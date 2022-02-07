package com.nova.android.shield.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.nova.android.shield.main.ShieldConstants;
import com.nova.android.shield.ui.home.MainActivity;
import com.nova.android.shield.utils.Constants;

import static android.content.Intent.FLAG_RECEIVER_FOREGROUND;
import static com.nova.android.shield.utils.Constants.NOTIFICATION_CHANNEL;

public class ShieldService extends Service {

    private static final String TAG = "[Nova][ShieldService]";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), ShieldConstants.drawable.shld_launcher);
        switch (action) {
            case Constants
                    .SHIELD_APP_FOREGROUND: {
                shieldStopForeground();
            }
            case Constants.SHIELD_APP_BACKGROUND: {
                Intent clickIntent = new Intent(this, MainActivity.class);
                clickIntent.setAction(Constants.SHIELD_APP_FOREGROUND);
                clickIntent.setFlags(FLAG_RECEIVER_FOREGROUND);
                PendingIntent activity = PendingIntent.getActivity(this, 0, clickIntent, 0);
                Intent stopIntent = new Intent(this, ShieldService.class);
                stopIntent.setAction(Constants.SHIELD_STOP);
                Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                        .setContentTitle(getString(ShieldConstants.string.app_name))
                        .setTicker(getString(ShieldConstants.string.app_name))
                        .setContentText(getString(ShieldConstants.string.foreground_notification_content_title))
                        .setSmallIcon(ShieldConstants.drawable.shld)
                        .setLargeIcon(largeIcon)
                        .setContentIntent(activity)
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .addAction(1, getString(ShieldConstants.string.foreground_notification_action_stop), PendingIntent.getService(this, 0, stopIntent, 0))
                        .build();
                startForeground(Constants.FOREGROUND_SERVICE, notification);
                break;
            }
            case Constants.SHIELD_STOP: {
                shieldStopForeground();
            }
        }

        return START_NOT_STICKY;

    }

    private void shieldStopForeground() {
        if (Build.VERSION.SDK_INT >= 24) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
