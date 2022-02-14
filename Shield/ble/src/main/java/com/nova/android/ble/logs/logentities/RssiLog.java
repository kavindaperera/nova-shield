package com.nova.android.ble.logs.logentities;

import com.google.gson.Gson;

public class RssiLog extends LogEntity {

    String uuid;

    int rssi;

    public RssiLog( String uuid, int rssi, Event event) {
        super(LogType.RSSI, event.ordinal());
        this.uuid = uuid;
        this.rssi = rssi;
    }

    public RssiLog( String uuid, int rssi, ErrorEvent event) {
        super(LogType.RSSI_ERROR, event.ordinal());
        this.uuid = uuid;
        this.rssi = rssi;
    }

    @Override
    public String serialize() {
        return new Gson().toJson((Object) this);
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public enum Event {
        RssiRead, //0
    }

    public enum ErrorEvent {
        RssiError;
    }
}
