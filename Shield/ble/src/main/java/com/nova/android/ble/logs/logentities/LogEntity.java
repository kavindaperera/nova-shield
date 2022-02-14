package com.nova.android.ble.logs.logentities;

public abstract class LogEntity {

    int logType;

    int eventId;

    long timeStamp = System.currentTimeMillis();

    String message;

    String errorMessage;

    public LogEntity(LogType logType, int eventId) {
        this.logType = logType.ordinal();
        this.eventId = eventId;
        this.message = this.getMessage();
    }

    public abstract String serialize();

    public abstract String getMessage();

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLogType() {
        return logType;
    }

    public static enum LogLevel {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        ASSERT
    }

    public static enum LogType {
        RSSI, //0
        RSSI_ERROR, // 1
    }

}
