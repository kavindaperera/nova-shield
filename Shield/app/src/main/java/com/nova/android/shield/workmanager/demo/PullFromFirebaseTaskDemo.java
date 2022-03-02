package com.nova.android.shield.workmanager.demo;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nova.android.shield.ble.BleRecord;
import com.nova.android.shield.ble.BleRecordRepository;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class PullFromFirebaseTaskDemo extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "[Nova][Shield][PullFromFirebaseTaskDemo]";

    Context context;

    BleRecordRepository recordRepository;

    public PullFromFirebaseTaskDemo(Context context) {
        Constants.PullFromFirebaseServiceRunning = true;
        Log.i(TAG, "PullFromFirebaseServiceRunning: " + "true");
        this.context = context;
        this.recordRepository = new BleRecordRepository(context);
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
                        Log.e(TAG, "Error getting documents: "+ task.getException());
                    }
                });


        // get all ble records
        for (BleRecord record : getAllBleRecords()) {
            Log.d(TAG, record.toString() );

            // group records by uuid using a hashmap <uuid, timestamp list>

        }


        List<String> exposedMsgs = new ArrayList<>();
        List<Long> startTimes = new ArrayList<>();
        List<Long> endTimes = new ArrayList<>();

        // check isExposed() - return expose start time and end time

        // add message to exposedMsgs array

        // add start time to startTimes array

        // add end time to endTimes array

        notifyBulk(Constants.NotifType.EXPOSURE, exposedMsgs, startTimes, endTimes);

        return null;
    }

    private List<BleRecord> getAllBleRecords(){
        return this.recordRepository.getAllRecords();
    }

    public void notifyBulk(Constants.NotifType notifType, List<String> msgs, List<Long> timeStart, List<Long> timeEnd) {

        // TODO - For each msg
        // - write  to database asynchronously
        // - send notification

    }

    public static long[] isExposed(){
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Constants.PullFromFirebaseServiceRunning = false;
        Log.i(TAG, "PullFromFirebaseServiceRunning: " + "false");
    }
}
