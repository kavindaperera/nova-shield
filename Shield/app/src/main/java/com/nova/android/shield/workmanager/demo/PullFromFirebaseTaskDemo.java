package com.nova.android.shield.workmanager.demo;

import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nova.android.shield.R;
import com.nova.android.shield.ble.BleRecord;
import com.nova.android.shield.ble.BleRecordRepository;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.main.ShieldConstants;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.ui.notification.NotificationRecord;
import com.nova.android.shield.ui.notification.NotificationRepository;
import com.nova.android.shield.utils.Constants;
import com.nova.android.shield.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PullFromFirebaseTaskDemo extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "[Nova][Shield][PullFromFirebaseTaskDemo]";

    Context context;

    BleRecordRepository recordRepository;
    NotificationRepository notificationRepository;

    public PullFromFirebaseTaskDemo(Context context) {
        Constants.PullFromFirebaseServiceRunning = true;
        Log.i(TAG, "PullFromFirebaseServiceRunning: " + "started");
        this.context = context;
        this.recordRepository = new BleRecordRepository(context);
        this.notificationRepository = new NotificationRepository(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        HashMap<String, List<Long>> groupedRecords = new HashMap<>();
        for (BleRecord record : getAllBleRecords()) {
            if (groupedRecords.containsKey(record.getUuid())) {
                groupedRecords.get(record.getUuid()).add(record.getTimestamp());
            } else {
                ArrayList<Long> timestamps = new ArrayList<>();
                timestamps.add(record.getTimestamp());
                groupedRecords.put(record.getUuid(), timestamps);
            }
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Timestamp lastCheckedTime;
        Timestamp currentTime = Timestamp.now();
        long seconds = ShieldPreferencesHelper.getLastCheckedTime(context);

        if (seconds != 0L) {
            lastCheckedTime = new Timestamp(seconds, 0);
        } else {
            lastCheckedTime = currentTime;
        }

        ShieldPreferencesHelper.setLastCheckedTime(context, currentTime.getSeconds());

        List<String> receivedUUIDs = new ArrayList<>();

        db.collection("infected-users")
                .whereGreaterThan("date", lastCheckedTime)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getData().containsKey("uuid")) {
                                receivedUUIDs.add((String) document.getData().get("uuid"));
                            }
                        }

                        if (receivedUUIDs.isEmpty()) return;

                        List<String> exposedMsgs = new ArrayList<>();
                        List<Long> startTimes = new ArrayList<>();
                        List<Long> endTimes = new ArrayList<>();

                        for (String receivedUUID : receivedUUIDs) {

                            long[] exposedTimes = isExposed(receivedUUID, groupedRecords);

                            if (exposedTimes != null) {
                                exposedMsgs.add("You were exposed");
                                startTimes.add(exposedTimes[0]);
                                endTimes.add(exposedTimes[1]);
                            }

                        }

                        notifyBulk(Constants.NotifType.EXPOSURE, exposedMsgs, startTimes, endTimes);

                    } else {
                        Log.e(TAG, "Error retrieving infected user UUIDs: " + task.getException());
                    }
                });


        return null;
    }

    private long[] isExposed(String receivedUUID, HashMap<String, List<Long>> groupedRecords) {

        Long contactTimesStart;
        Long contactTimesEnd;

        if (groupedRecords.containsKey(receivedUUID)) {
            List<Long> matches = groupedRecords.get(receivedUUID);
            contactTimesStart = (matches.get(0));
            contactTimesEnd = (matches.get(matches.size() - 1));
            return new long[]{contactTimesStart, contactTimesEnd};
        } else {
            return null;
        }

    }

    private List<BleRecord> getAllBleRecords() {
        return this.recordRepository.getAllRecords();
    }

    public void notifyBulk(Constants.NotifType notifType, List<String> msgs, List<Long> timeStart, List<Long> timeEnd) {

        NotificationRecord notificationRecord;
        String msg;
        long start;
        long end;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        for (int i = 0; i < msgs.size(); i++) {
            msg = msgs.get(i);
            start = timeStart.get(i);
            end = timeEnd.get(i);

            notificationRecord = new NotificationRecord(start, end, msg, notifType.ordinal(), true);
            this.notificationRepository.insert(notificationRecord);

            msg = msg + this.context.getString(R.string.exposed_text2) + " " + dateFormat.format(Long.valueOf(start)) + ".";
            Utils.sendNotification(context, context.getString(ShieldConstants.string.exposed), msg, 1);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Constants.PullFromFirebaseServiceRunning = false;
        Log.i(TAG, "PullFromFirebaseServiceRunning: " + "completed");
    }
}
