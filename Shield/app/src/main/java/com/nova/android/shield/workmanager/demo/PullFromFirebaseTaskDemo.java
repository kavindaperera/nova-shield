package com.nova.android.shield.workmanager.demo;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Timestamp lastCheckedTime;
        Timestamp currentTime = Timestamp.now();
        long seconds = ShieldPreferencesHelper.getLastCheckedTime(context);

        if(seconds != 0L) {
            lastCheckedTime = new Timestamp(seconds,0);
        } else {
            lastCheckedTime = currentTime;
        }

        ShieldPreferencesHelper.setLastCheckedTime(context, currentTime.getSeconds());

        db.collection("infected-users")
                .whereGreaterThan("date", lastCheckedTime)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: "+ task.getException());
                    }
                });

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Constants.PullFromFirebaseServiceRunning = false;
        Log.e(TAG, "PullFromFirebaseServiceRunning: " + "false");
    }
}
