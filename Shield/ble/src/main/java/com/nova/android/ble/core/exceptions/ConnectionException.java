package com.nova.android.ble.core.exceptions;

public class ConnectionException extends Exception {
    public ConnectionException(String str, Exception e) {
        super(str, e);
    }

    public ConnectionException(String str) {
        super(str);
    }

    public ConnectionException(Exception e) {
        super(e);
    }
}
