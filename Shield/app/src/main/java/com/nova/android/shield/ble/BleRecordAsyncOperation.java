package com.nova.android.shield.ble;

import android.content.Context;
import android.os.AsyncTask;

import com.nova.android.shield.utils.Constants;

public class BleRecordAsyncOperation extends AsyncTask<Void, Void, Void> {

    private Constants.DatabaseOps operation;
    private BleRecordRepository mRepository;
    private BleRecord bleRecord;

    public BleRecordAsyncOperation(Context context, String uuid, int rssi, long timestamp) {
        this.mRepository = new BleRecordRepository(context);
        this.bleRecord = new BleRecord(uuid, timestamp, rssi);
        this.operation = Constants.DatabaseOps.INSERT;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        switch (operation) {
            case INSERT:{
                this.mRepository.insert(bleRecord);
                return null;
            }
            case DELETE_ALL:{
                this.mRepository.deleteAll();
                return null;
            }
            default:{
                return null;
            }
        }
    }



}

