package com.nova.android.shield.ui.notification;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//@Database(entities = {NotificationRecord.class,}, version = 1)
public abstract class NotificationRecordRoomDatabase extends RoomDatabase {

    private static volatile NotificationRecordRoomDatabase DB_INSTANCE;

    @VisibleForTesting
    public static final String DB_NAME = "notification_database";

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
