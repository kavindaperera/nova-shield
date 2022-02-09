package com.nova.android.shield.ble;

public class BleRecord {

    private long timestamp;

    private String uuid;

    private int rssi;

    public BleRecord(String uuid2, long timestamp2, int rssi2) {
        this.uuid = uuid2;
        this.timestamp = timestamp2;
        this.rssi = rssi2;
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

}
