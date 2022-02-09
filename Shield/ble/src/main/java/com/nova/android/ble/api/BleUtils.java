package com.nova.android.ble.api;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;

import com.nova.android.ble.exception.BleException;

public class BleUtils {

    private static boolean checkHardware(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
    }

    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        return ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    public static boolean isLocationAvailable(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled("gps") || locationManager.isProviderEnabled("network")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static void initialize(Context context) {
        if (!BleUtils.checkHardware(context)) {
            throw new BleException(Constants.BLE_NOT_SUPPORTED, Constants.BLE_NOT_SUPPORTED_STRING);
        }
    }

    public static boolean checkLocationPermissions(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED || context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkBluetoothPermission(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED || context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    public static void enableBluetooth(Context context) {
        BleUtils.getBluetoothAdapter(context).enable();
    }

    private static void checkPermissions(Context context) throws BleException {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkLocationPermissions(context)) {
                throw new BleException(Constants.INSUFFICIENT_PERMISSIONS, Constants.INSUFFICIENT_LOCATION_PERMISSIONS_STRING);
            } else if (!isLocationAvailable(context)) {
                throw new BleException(Constants.LOCATION_SERVICES_DISABLED, Constants.LOCATION_SERVICES_STRING);
            }
        }

        if (!checkBluetoothPermission(context)) {
            throw new BleException(Constants.INSUFFICIENT_PERMISSIONS, Constants.INSUFFICIENT_BLUETOOTH_PERMISSIONS_STRING);
        }
    }

}
