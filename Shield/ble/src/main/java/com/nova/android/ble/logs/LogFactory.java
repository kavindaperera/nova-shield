package com.nova.android.ble.logs;


import com.nova.android.ble.logs.logentities.LogEntity;
import com.nova.android.ble.logs.logentities.RssiLog;

public class LogFactory {
    public static LogEntity build(String uuid, int rssi, RssiLog.Event event) {
        return new RssiLog(uuid, rssi, event);
    }

    public static LogEntity build(String uuid, int rssi, RssiLog.ErrorEvent event) {
        return new RssiLog(uuid, rssi, event);
    }

}
