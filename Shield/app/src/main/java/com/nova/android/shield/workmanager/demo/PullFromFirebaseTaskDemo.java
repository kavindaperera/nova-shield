package com.nova.android.shield.workmanager.demo;

import android.content.Context;
import android.os.AsyncTask;

import com.nova.android.shield.logs.Log;
import com.nova.android.shield.utils.Constants;

public class PullFromFirebaseTaskDemo extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "[Nova][Shield][PullFromFirebaseTaskDemo]";

    Context context;

    public PullFromFirebaseTaskDemo(Context context) {
        Constants.PullFromFirebaseServiceRunning = true;
        Log.e(TAG, "PullFromFirebaseServiceRunning: " + "true");
        this.context = context;
    }


    @Override
    protected Void doInBackground(Void... voids) {

        //TODO @JudeRanidu

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Constants.PullFromFirebaseServiceRunning = false;
        Log.e(TAG, "PullFromFirebaseServiceRunning: " + "false");
    }
}
