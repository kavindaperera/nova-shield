package com.nova.android.shield.database;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nova.android.shield.database.dao.ContactRecordDao;
import com.nova.android.shield.database.dao.NotificationRecordDao;
import com.nova.android.shield.models.ContactRecord;
import com.nova.android.shield.models.NotificationRecord;

@Database(entities = {NotificationRecord.class, ContactRecord.class}, version = 1)
public abstract class ShieldRoomDatabase extends RoomDatabase {

    private static volatile ShieldRoomDatabase DB_INSTANCE;

    @VisibleForTesting
    public static final String DB_NAME = "shield_database";

    public abstract NotificationRecordDao notificationRecordDao();

    public abstract ContactRecordDao contactRecordDao();

    public static ShieldRoomDatabase getDatabase(final Context context) {

        if (DB_INSTANCE == null) {
            synchronized (ShieldRoomDatabase.class) {
                if (DB_INSTANCE == null) {
                    DB_INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ShieldRoomDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        return DB_INSTANCE;
    }

}
