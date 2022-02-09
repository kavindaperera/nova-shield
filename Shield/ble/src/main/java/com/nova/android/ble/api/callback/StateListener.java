package com.nova.android.ble.api.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

public interface StateListener {

    /**
     * Method called when there is an error at the start
     *
     * @param message   a string describing the error occurred
     * @param errorCode an integer which represents the error
     */
    void onStartError(@NonNull String message, @NonNull int errorCode);

    /**
     * Method called when this is successfully started
     */
    void onStarted();

    /**
     * Method called when the RSSI value has been read.
     *
     * @param bluetoothDevice the target device.
     * @param rssi the current RSSI value, in dBm.
     */
    void onRssiRead(@NonNull final BluetoothDevice bluetoothDevice, @IntRange(from = -128, to = 20) final int rssi);

}
