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

        //TODO @JudeRanidu
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
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: "+ task.getException());
                        }
                    }
                });


        /*Map<String, Object> data = new HashMap<>();
        data.put("uuid", ShieldPreferencesHelper.getUserUuid(context).toString());
        data.put("date", FieldValue.serverTimestamp());

        db.collection("infected-users")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document "+ e);
                    }
                });*/

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Constants.PullFromFirebaseServiceRunning = false;
        Log.e(TAG, "PullFromFirebaseServiceRunning: " + "false");
    }
}
