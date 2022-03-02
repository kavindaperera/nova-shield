package com.nova.android.shield.ble;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.gson.Gson;

@Entity(tableName = "ble_record_table", primaryKeys = {"uuid", "timestamp"})
public class BleRecord {

    @NonNull
    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @NonNull
    @ColumnInfo(name = "uuid")
    private String uuid;

    @ColumnInfo(name = "rssi")
    private int rssi;

    public BleRecord(@NonNull String uuid, @NonNull long timestamp, int rssi) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.rssi = rssi;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
