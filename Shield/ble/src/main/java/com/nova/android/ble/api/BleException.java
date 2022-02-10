package com.nova.android.ble.api;

public class BleException extends RuntimeException {

    private int errorCode;

    public BleException(Throwable cause) {
        super(cause);
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public BleException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
