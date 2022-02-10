package com.nova.android.ble.core;

import com.nova.android.ble.api.BleManager;
import com.nova.android.ble.core.exceptions.ConnectionException;
import com.nova.android.ble.logs.Log;

public class ServerFactory {

    private static final String TAG = "[Nova][Ble][ServerFactory]";

    private static BluetoothLeServer bluetoothLeServer;

    public static ThreadServer getServerInstance(boolean isNew) {
        if (bluetoothLeServer == null && isNew) {
            try {
                Log.d(TAG, "getServerInstance: new bluetooth le server created");
                bluetoothLeServer = new BluetoothLeServer(BleManager.getInstance().getBleCore().getContext());
            } catch (ConnectionException exception) {
                Log.e(TAG, "getServerInstance: Error occurred while initiating BluetoothServer", exception);
            }
        }
        return bluetoothLeServer;
    }

    static void setBluetoothLeServer(BluetoothLeServer bluetoothLeServer) {
        ServerFactory.bluetoothLeServer = bluetoothLeServer;
    }

}
