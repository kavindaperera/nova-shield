package com.nova.android.shield.ble;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {BleRecord.class,}, version = 1)
public abstract class BleRecordRoomDatabase extends RoomDatabase {

    private static volatile BleRecordRoomDatabase INSTANCE = null;

    @VisibleForTesting
    public static final String DATABASE_NAME = "ble_record_database";

    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        public void onOpen(SupportSQLiteDatabase db) {
            super.onOpen(db);
        }

        @Override
        public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
            super.onDestructiveMigration(db);
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    public abstract BleRecordDao recordDao();

    static BleRecordRoomDatabase getDatabase(Context context) {
        Class<BleRecordRoomDatabase> recordRoomDatabaseClass = BleRecordRoomDatabase.class;
        if (INSTANCE == null) {
            synchronized (recordRoomDatabaseClass) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, recordRoomDatabaseClass, DATABASE_NAME)
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }



}
