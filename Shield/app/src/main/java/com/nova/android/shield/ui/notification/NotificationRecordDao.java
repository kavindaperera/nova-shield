package com.nova.android.shield.ui.notification;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationRecordDao {

    // insert record
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(NotificationRecord notificationRecord);

    // get all records
    @Query("SELECT * FROM notification_record_table")
    List<NotificationRecord> getAllNotificationRecords();

    // get records between time interval
    @Query("SELECT * FROM notification_record_table WHERE timestamp_start BETWEEN :timestamp1 AND :timestamp2")
    List<NotificationRecord> getNotificationRecordsBetweenTimestamp(long timestamp1, long timestamp2);

    // get sorted records
    @Query("SELECT * FROM notification_record_table ORDER BY timestamp_start DESC")
    LiveData<List<NotificationRecord>> getSortedNotificationRecords();

    // delete all
    @Query("DELETE FROM notification_record_table")
    void deleteAll();

    // delete old records
    @Query("DELETE FROM notification_record_table WHERE timestamp_start < :timestamp")
    void deleteEarlierThan(long timestamp);

    // delete by id
    @Query("DELETE FROM notification_record_table WHERE id = :id")
    void deleteById(int id);

}
