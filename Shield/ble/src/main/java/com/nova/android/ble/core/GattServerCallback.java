package com.nova.android.ble.core;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;

import com.nova.android.ble.logs.Log;


public class GattServerCallback extends BluetoothGattServerCallback {

    final String TAG = "[Nova][Ble][GattServerCallback]";

    public GattServerCallback() {
    }

    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        super.onConnectionStateChange(device, status, newState);
        Log.i(TAG,"onConnectionStateChange()" + " | newState: " + newState + " | status: " + status);
    }

    @Override
    public void onServiceAdded(int status, BluetoothGattService service) {
        super.onServiceAdded(status, service);
        Log.e(TAG,"onServiceAdded()" + " | service: " + service.getUuid().toString() + " | status: " + status);
    }

    @Override
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        Log.e(TAG,"onCharacteristicReadRequest()" + " | device: " + device + " | requestId: " + requestId + " | offset: "  + offset + " | characteristic: " + characteristic);
    }

    @Override
    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
        Log.e(TAG,"onCharacteristicWriteRequest()" + " | device: " + device + " | requestId: " + requestId + " | offset: "  + offset + " | characteristic: " + characteristic);
    }

    @Override
    public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
        super.onDescriptorReadRequest(device, requestId, offset, descriptor);
        Log.e(TAG,"onDescriptorReadRequest()" + " | device: " + device + " | requestId: " + requestId + " | offset: "  + offset + " | descriptor: " + descriptor);
    }

    @Override
    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
        Log.e(TAG,"onDescriptorWriteRequest()" + " | device: " + device + " | requestId: " + requestId + " | descriptor: " + descriptor.getValue());
    }

    @Override
    public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
        super.onExecuteWrite(device, requestId, execute);
        Log.e(TAG,"onExecuteWrite()" + " | device: " + device + " | requestId: " + requestId + " | execute: " + execute);
    }

    @Override
    public void onNotificationSent(BluetoothDevice device, int status) {
        super.onNotificationSent(device, status);
        Log.e(TAG,"onNotificationSent()" + " | device: " + device + " | status: " + status);
    }

    @Override
    public void onMtuChanged(BluetoothDevice device, int mtu) {
        super.onMtuChanged(device, mtu);
        Log.e(TAG,"onMtuChanged()" + " | mtu: " + mtu + " | device: " + device);
    }

    @Override
    public void onPhyUpdate(BluetoothDevice device, int txPhy, int rxPhy, int status) {
        super.onPhyUpdate(device, txPhy, rxPhy, status);
        Log.e(TAG, "onPhyUpdate(): txPhy: " + txPhy + " | rxPhy: " + rxPhy + " | status: " + status);
    }

    @Override
    public void onPhyRead(BluetoothDevice device, int txPhy, int rxPhy, int status) {
        super.onPhyRead(device, txPhy, rxPhy, status);
        Log.e(TAG, "onPhyRead(): txPhy: " + txPhy + " | rxPhy: " + rxPhy + " | status: " + status);
    }

}
