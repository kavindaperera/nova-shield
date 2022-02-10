package com.nova.android.shield.main;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.nova.android.shield.BuildConfig;
import com.nova.android.shield.auth.ShieldSession;
import com.nova.android.shield.utils.Constants;
import com.nova.android.shield.workmanager.periodictasks.PeriodicTasksHandler;

import java.util.Objects;

public class ShieldApp extends Application {

    private static ShieldApp shieldApp;
    SharedPreferences sharedPreferences;
    ShieldFirebaseAuth shieldFirebaseAuth;
    public static boolean debug = false;

    public static ShieldApp getInstance() {
        return shieldApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ShieldApp.debug = BuildConfig.DEBUG; // TODO - remove in production

        this.sharedPreferences = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        shieldApp = this;
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(getApplicationContext());
        Objects.requireNonNull(firebaseApp);
        FirebaseAuth.getInstance(firebaseApp);
        shieldFirebaseAuth = new ShieldFirebaseAuth(getApplicationContext());
        if (this.sharedPreferences.getLong(Constants.PREFS_FIRST_DATE, 0) == 0) {
            this.sharedPreferences.edit().putLong(Constants.PREFS_FIRST_DATE, System.currentTimeMillis()).apply();
        }

        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel(); // create notification channel
        }

        ShieldSession.loadSession(this);

        new PeriodicTasksHandler(this).initializeAllPeriodicRequests(); // create task work manager

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        String channelName = getString(ShieldConstants.string.channel_name);
        String channelDescription = getString(ShieldConstants.string.channel_description);
        NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        (getSystemService(NotificationManager.class)).createNotificationChannel(notificationChannel);
    }

}
