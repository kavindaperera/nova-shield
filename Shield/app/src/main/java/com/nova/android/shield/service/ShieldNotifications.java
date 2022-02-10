package com.nova.android.shield.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nova.android.shield.R;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.main.ShieldApp;
import com.nova.android.shield.main.ShieldConstants;
import com.nova.android.shield.ui.home.TabbedMainActivity;
import com.nova.android.shield.utils.Constants;

public class ShieldNotifications extends FirebaseMessagingService {

    private static final String TAG = "[Nova][Shield][ShieldNotifications]";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();

        Log.e(TAG, "New notification: " + text);

        Intent clickIntent = new Intent(this, TabbedMainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(ShieldApp.getInstance().getResources(), ShieldConstants.drawable.shld_launcher);
        NotificationCompat.Builder contentIntent = new NotificationCompat.Builder((Context) ShieldApp.getInstance(), Constants.NOTIFICATION_CHANNEL)
                .setContentTitle(title)
                .setContentText(text)
                .setColor(ShieldApp.getInstance().getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(ShieldConstants.drawable.shld)
                .setContentIntent(pendingIntent)
                .setPriority(4)
                .setLights(ShieldApp.getInstance().getResources().getColor(R.color.colorPrimary), 1000, 3000)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= 21) {
            contentIntent.setLargeIcon(largeIcon);
        }

        ((NotificationManager) ShieldApp.getInstance().getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, contentIntent.build());

        super.onMessageReceived(remoteMessage);
    }


}
