package com.nova.android.shield.ui.notification;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {
    private NotificationRecordDao notificationRecordDao;

    public NotificationRepository(Context context) {
        NotificationRecordRoomDatabase db = NotificationRecordRoomDatabase.getDatabase(context);
        notificationRecordDao = db.notificationRecordDao();
    }

    public void insert(NotificationRecord notificationRecord) {
        NotificationRecordRoomDatabase.databaseWriteExecutor.execute(() -> {
            notificationRecordDao.insert(notificationRecord);
        });
    }

    public List<NotificationRecord> getAllRecords() {
        return this.notificationRecordDao.getAllNotificationRecords();
    }

    public LiveData<List<NotificationRecord>> getAllSortedRecords() {
        return this.notificationRecordDao.getSortedNotificationRecords();
    }

    public List<NotificationRecord> getRecordsBetweenTimestamp(long timestamp1, long timestamp2) {
        return this.notificationRecordDao.getNotificationRecordsBetweenTimestamp(timestamp1, timestamp2);
    }

    public void deleteOldRecords(long timestamp) {
        NotificationRecordRoomDatabase.databaseWriteExecutor.execute(() -> {
            notificationRecordDao.deleteEarlierThan(timestamp);
        });
    }

    public void deleteAll() {
        NotificationRecordRoomDatabase.databaseWriteExecutor.execute(() -> {
            notificationRecordDao.deleteAll();
        });
    }

    public void deleteById(int id) {
        NotificationRecordRoomDatabase.databaseWriteExecutor.execute(() -> {
            notificationRecordDao.deleteById(id);
        });
    }
}
