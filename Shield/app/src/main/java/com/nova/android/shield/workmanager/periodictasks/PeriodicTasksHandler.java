package com.nova.android.shield.workmanager.periodictasks;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.nova.android.shield.logs.Log;
import com.nova.android.shield.utils.Constants;
import com.nova.android.shield.workmanager.workers.PullFromFirebaseWorker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PeriodicTasksHandler {

    private static final String TAG = "[Nova][Shield][PeriodicTasksHandler]";

    private static final String PULL_FROM_FIREBASE_SERVICE_TAG = "pullFromFirebaseService";

    private final Context context;

    private final Map<String, PeriodicWorkRequest> periodicWorkRequests = new HashMap();

    public PeriodicTasksHandler(Context context1) {
        this.context = context1;
    }

    public void initializeAllPeriodicRequests() {
        Log.i(TAG, "initializeAllPeriodicRequests(): ");
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

        this.periodicWorkRequests.put(
                PULL_FROM_FIREBASE_SERVICE_TAG,
                (PeriodicWorkRequest) ((PeriodicWorkRequest.Builder) ((PeriodicWorkRequest.Builder) new PeriodicWorkRequest.Builder((Class<? extends ListenableWorker>) PullFromFirebaseWorker.class, (long) Constants.PULL_FROM_FIREBASE_INTERVAL, TimeUnit.MILLISECONDS).addTag(PULL_FROM_FIREBASE_SERVICE_TAG)).setConstraints(constraints)).build());

        startWork();
    }

    private void startWork() {
        Log.i(TAG, "startWork(): ");
        for (Map.Entry<String, PeriodicWorkRequest> entry : this.periodicWorkRequests.entrySet()) {
            WorkManager instance = WorkManager.getInstance(this.context);
            Log.i(TAG, "startWork(): " + entry.getKey() + " " + entry.getValue().getId());
            instance.enqueueUniquePeriodicWork(entry.getKey(), ExistingPeriodicWorkPolicy.KEEP, entry.getValue());

        }
    }
}
