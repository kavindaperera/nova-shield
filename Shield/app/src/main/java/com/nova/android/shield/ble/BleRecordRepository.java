package com.nova.android.shield.ble;

import android.app.Application;
import android.content.Context;

import java.util.List;

public class BleRecordRepository {

    private BleRecordDao mBleRecordDao;

    public BleRecordRepository(Context context) {
        BleRecordRoomDatabase db = BleRecordRoomDatabase.getDatabase(context);
        mBleRecordDao = db.recordDao();
    }

    public List<BleRecord> getAllRecords() {
        return this.mBleRecordDao.getSortedBleRecordsByTimestamp();
    }

    public void insert(BleRecord bleRecord) {
        BleRecordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mBleRecordDao.insert(bleRecord);
        });
    }

    public void deleteAll() {
        BleRecordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mBleRecordDao.deleteAll();
        });
    }

}
