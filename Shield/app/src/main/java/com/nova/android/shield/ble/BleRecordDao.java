package com.nova.android.shield.ble;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BleRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(BleRecord bleRecord);

    @Query("DELETE FROM ble_record_table")
    void deleteAll();

    @Query("SELECT * FROM ble_record_table")
    List<BleRecord> getAllBleRecords();

    @Query("SELECT * FROM ble_record_table ORDER BY timestamp DESC")
    List<BleRecord> getSortedBleRecordsByTimestamp();

}
