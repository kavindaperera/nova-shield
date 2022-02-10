package com.nova.android.ble.api;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;


public class Device implements Parcelable {

    private String deviceName;

    private String deviceAddress;

    private BluetoothDevice bluetoothDevice;

    private String userId;

    private int rssi;

    public Device() {
    }

    protected Device(Parcel in) {
        this.deviceName = in.readString();
        this.deviceAddress = in.readString();
        this.bluetoothDevice = (BluetoothDevice) in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    public Device(String userId) {
        this.userId = userId;
    }

    public Device(String deviceName, String deviceAddress, String userId) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.userId = userId;
    }

    @SuppressLint("MissingPermission")
    public Device(BluetoothDevice bluetoothDevice, boolean isBLE) {
        this.bluetoothDevice = bluetoothDevice;
        this.deviceName = bluetoothDevice.getName();
        this.deviceAddress = bluetoothDevice.getAddress();
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceName);
        dest.writeString(this.deviceAddress);
        dest.writeParcelable(this.bluetoothDevice, flags);
    }


    /*getters*/


    public BluetoothDevice getBluetoothDevice() {
        return this.bluetoothDevice;
    }

    public String getDeviceAddress() {
        return this.deviceAddress;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public String getUserId() {
        return this.userId;
    }

    public int getRssi() {
        return this.rssi;
    }


    /*setters*/

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.deviceAddress = bluetoothDevice.getAddress();
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Device) {
            Device device = (Device) obj;
            return device.getDeviceAddress() != null && device.getDeviceAddress().trim().equalsIgnoreCase(getDeviceAddress().trim());
        }
        throw new IllegalArgumentException(obj.getClass().getName() + " is not a Device");
    }
}
