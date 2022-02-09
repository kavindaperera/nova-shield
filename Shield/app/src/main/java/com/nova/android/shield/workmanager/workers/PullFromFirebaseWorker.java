package com.nova.android.shield.workmanager.workers;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.nova.android.shield.logs.Log;
import com.nova.android.shield.utils.Constants;

public class PullFromFirebaseWorker extends Worker {

    private static final String TAG = "[Nova][Shield][PullFromFirebaseWorker]";

    public PullFromFirebaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {

        Log.i(TAG, "doWork(): started");

        Constants.PullFromFirebaseServiceRunning = true;

        Constants.PullFromFirebaseServiceRunning = false;

        return ListenableWorker.Result.success();
    }
}
