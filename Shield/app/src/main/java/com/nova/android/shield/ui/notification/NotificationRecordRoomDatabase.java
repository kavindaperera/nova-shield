package com.nova.android.shield.ui.notification;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {NotificationRecord.class,}, version = 1)
public abstract class NotificationRecordRoomDatabase extends RoomDatabase {

    private static volatile NotificationRecordRoomDatabase DB_INSTANCE;

    @VisibleForTesting
    public static final String DB_NAME = "notification_database";

    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract NotificationRecordDao notificationRecordDao();

    public static NotificationRecordRoomDatabase getDatabase(final Context context) {

        if (DB_INSTANCE == null) {
            synchronized (NotificationRecordRoomDatabase.class) {
                if (DB_INSTANCE == null) {
                    DB_INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NotificationRecordRoomDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        return DB_INSTANCE;
    }

}
