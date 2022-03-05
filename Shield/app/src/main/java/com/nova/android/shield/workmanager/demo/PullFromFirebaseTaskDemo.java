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
import com.nova.android.shield.ui.notification.NotificationRecord;
import com.nova.android.shield.ui.notification.NotificationRepository;
import com.nova.android.shield.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PullFromFirebaseTaskDemo extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "[Nova][Shield][PullFromFirebaseTaskDemo]";

    Context context;

    BleRecordRepository recordRepository;
    NotificationRepository notificationRepository;

    public PullFromFirebaseTaskDemo(Context context) {
        Constants.PullFromFirebaseServiceRunning = true;
        Log.i(TAG, "PullFromFirebaseServiceRunning: " + "true");
        this.context = context;
        this.recordRepository = new BleRecordRepository(context);
        this.notificationRepository = new NotificationRepository(context);
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


        HashMap<String, ArrayList<Long>> groupedRecords = new HashMap<>();
        for (BleRecord record : getAllBleRecords()) {
            Log.d(TAG, record.toString() );

            if (groupedRecords.containsKey(record.getUuid())) {
                groupedRecords.get(record.getUuid()).add(record.getTimestamp());
            } else {
                ArrayList<Long> timestamps = new ArrayList<>();
                timestamps.add(record.getTimestamp());
                groupedRecords.put(record.getUuid(), timestamps);
            }

        }


        List<String> exposedMsgs = new ArrayList<>();
        List<Long> startTimes = new ArrayList<>();
        List<Long> endTimes = new ArrayList<>();

        Iterator<String> uuids = groupedRecords.keySet().iterator();
        String uuid;
        String exposedMsg;
        Date startTime, endTime;
        while (uuids.hasNext()) {
            uuid = uuids.next();
            long[] exposedTimes = isExposed(groupedRecords.get(uuid));
            if (exposedTimes != null) {
                startTime = new Date(exposedTimes[0]);
                endTime = new Date(exposedTimes[1]);
                exposedMsg = "You were exposed during the period => " + startTime.toString() + " to " + endTime.toString();

                exposedMsgs.add(exposedMsg);
                startTimes.add(exposedTimes[0]);
                endTimes.add(exposedTimes[1]);
            }
        }

        notifyBulk(Constants.NotifType.EXPOSURE, exposedMsgs, startTimes, endTimes);

        return null;
    }

    private List<BleRecord> getAllBleRecords(){
        return this.recordRepository.getAllRecords();
    }

    public void notifyBulk(Constants.NotifType notifType, List<String> msgs, List<Long> timeStart, List<Long> timeEnd) {

        NotificationRecord notificationRecord;
        String msg;
        long start;
        long end;
        for (int i = 0; i < msgs.size(); i++) {
            msg = msgs.get(i);
            start = timeStart.get(i);
            end = timeEnd.get(i);

            notificationRecord = new NotificationRecord(start, end, msg, notifType.ordinal(), true);
            this.notificationRepository.insert(notificationRecord);

            // - send notification
        }
    }

    public static long[] isExposed(List<Long> timestamps){
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Constants.PullFromFirebaseServiceRunning = false;
        Log.i(TAG, "PullFromFirebaseServiceRunning: " + "false");
    }
}
