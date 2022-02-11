package com.nova.android.ble.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.nova.android.ble.api.BleUtils;
import com.nova.android.ble.core.exceptions.ConnectionException;
import com.nova.android.ble.api.BleException;
import com.nova.android.ble.logs.Log;

public class BleReceiver extends BroadcastReceiver {

    private final String TAG = "[Nova][Ble][BleReceiver]";

    private Context context;
    private boolean isRegistered = false;
    private BluetoothController bluetoothController;

    public BleReceiver(Context context) throws BleException {
        this.context = context;
        createBluetoothController(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        onReceiveAction(context, intent);
    }

    private void onReceiveAction(Context context, Intent intent) {
        this.bluetoothController.onReceiveAction(intent, context);
    }

    private void createBluetoothController(Context context) throws BleException {
        this.bluetoothController = new BluetoothController(context);
    }

    public void registerReceiver(Context context) {
        if (!this.isRegistered) {
            context.registerReceiver(this, getIntentFilter());
            this.isRegistered = true;
        }
    }

    public void unregisterReceiver(Context context) {
        if (this.isRegistered) {
            context.unregisterReceiver(this);
            this.isRegistered = false;
        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        this.addBleAction(intentFilter);
        return intentFilter;
    }

    private void addBleAction(IntentFilter intentFilter) {
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    public void startServer() {
        Log.d(TAG, "startServer: ");
        BluetoothAdapter bluetoothAdapter = BleUtils.getBluetoothAdapter(this.context);
        if (bluetoothAdapter.isEnabled()) {
            try {
                this.bluetoothController.startServer(this.context);
            } catch (ConnectionException e) {

            }
        }
    }

    public void stopServer() {
        this.onBluetoothServerStop();
    }

    private void onBluetoothServerStop() {
        try {
            this.bluetoothController.stopServer();
        }
        catch (ConnectionException connectionException) {
            Log.e(TAG, "onBluetoothServerStop: " + connectionException.getMessage());
        }
    }

    public void startDiscovery() {
        this.bluetoothController.startDiscovery(this.context);
    }

    public void stopDiscovery() {
        this.bluetoothController.stopDiscovery(this.context);
    }

}
