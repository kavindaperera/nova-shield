package com.nova.android.ble.core;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.ParcelUuid;

import com.nova.android.ble.api.BleManager;
import com.nova.android.ble.api.Device;
import com.nova.android.ble.logs.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;

public class BluetoothLeDiscovery extends Discovery {

    private static String TAG = "[Nova][Ble][BluetoothLeDiscovery]";
    private final String bleUuid = BluetoothUtils.getHashedBluetoothLeUuid(false).toString();   // j
    private final String btUuid = BluetoothUtils.getHashedBluetoothUuid(false).toString();      // k
    private final String bleUuid2 = BluetoothUtils.getHashedBluetoothLeUuid(BleManager.getInstance().getBleClient().getAppKey());          // l
    private BluetoothAdapter bluetoothAdapter;
    private HashMap<String, String> discoveredDevices;
    private HashMap<String, ScheduledFuture> scheduledDiscoveredDevices;
    private CompositeDisposable disposable = new CompositeDisposable();
    private ScanCallback scanCallback;
    private ScheduledThreadPoolExecutor threadPoolExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(40);


    public BluetoothLeDiscovery(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        if (this.bluetoothAdapter == null) {
            Log.e(TAG, " BluetoothAdapter was NULL");
        }
        this.discoveredDevices = new HashMap<>();
        this.scheduledDiscoveredDevices = new HashMap<>();
    }

    @SuppressLint({"CheckResult", "MissingPermission"})
    @Override
    public void startDiscovery(Context context) {
        Log.i(TAG, "startDiscovery ");
        if (BluetoothLeDiscovery.this.bluetoothAdapter != null && BluetoothLeDiscovery.this.bluetoothAdapter.isEnabled()) {
            if (BluetoothLeDiscovery.this.bluetoothAdapter.getBluetoothLeScanner() == null) {
                Log.w(TAG, "getBluetoothLeScanner() was null, sleeping for 300 ms.");
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            try {
                BluetoothLeDiscovery.this.bluetoothAdapter.getBluetoothLeScanner().startScan(
                        BluetoothUtils.getBluetoothLeScanFilter(),
                        BluetoothUtils.getScanSettings(),
                        BluetoothLeDiscovery.this.scanCallback = new ScanCallback() {
                            @Override
                            public void onScanResult(int callbackType, ScanResult result) {
                                Log.e(TAG, "onScanResult ");
                                super.onScanResult(callbackType, result);
                                BluetoothLeDiscovery.this.onScanResultAction(result);
                            }

                            @Override
                            public void onBatchScanResults(List<ScanResult> results) {
                                Log.e(TAG, "onBatchScanResults: ");
                                super.onBatchScanResults(results);

                            }

                            @Override
                            public void onScanFailed(int errorCode) {
                                Log.e(TAG, "onScanFailed: ");
                                super.onScanFailed(errorCode);
                                BluetoothLeDiscovery.this.scanFailedAction(errorCode);
                            }
                        });

                BluetoothLeDiscovery.this.setDiscoveryRunning(true);

            } catch (IllegalStateException illegalStateException) {
                Log.e(TAG, "error: " + illegalStateException.getMessage());
            }

            super.startDiscovery(context);

            Completable.timer(60L, TimeUnit.SECONDS).subscribe(() -> {
                Log.i(TAG, "startDiscovery: resetting");
                this.stopDiscovery(null);
                this.startDiscovery(null);
            }, throwable -> Log.e(TAG, "error: " + throwable.getMessage()));

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void stopDiscovery(Context context) {
        super.stopDiscovery(context);
        this.disposable.clear();
        if (this.bluetoothAdapter != null && this.scanCallback != null && this.bluetoothAdapter.isEnabled() && this.bluetoothAdapter.getBluetoothLeScanner() != null) {
            Log.i(TAG, "stopDiscovery: stopping scan");
        } else {
            // TODO
        }
        this.setDiscoveryRunning(false);
    }

    @SuppressLint("MissingPermission")
    private void onScanResultAction(ScanResult scanResult) {
        Log.e(TAG, "onScanResultAction ");
        String string = this.processScanResult(scanResult);
        if (string != null && this.bluetoothAdapter != null && this.bluetoothAdapter.isEnabled()) {
            Device device = this.processPresenceResult(string, scanResult.getDevice(), scanResult.getRssi());
            this.addToScheduledFuture(device, string);
        }
    }

    @SuppressLint("MissingPermission")
    synchronized Device processPresenceResult(String string, BluetoothDevice bluetoothDevice, int rssi) {
        if (this.bluetoothAdapter.isEnabled() && bluetoothDevice != null) {
            String userUuid = BleManager.getInstance().getBleClient().getUserUuid();
            boolean bl = false;
            Device device = new Device(bluetoothDevice, true);
            device.setDeviceName(string);
            device.setUserId(string);
            device.setRssi(rssi);
            return device;
        }
        Log.e(TAG, "processPresenceResult: could not process Bluetooth device");
        return null;
    }

    private String processScanResult(ScanResult scanResult) {
        String string;
        int rssi;

        blockX:
        {
            BluetoothDevice bluetoothDevice;
            Map<ParcelUuid, byte[]> map;
            blockY:
            {
                List list = scanResult.getScanRecord().getServiceUuids();
                map = scanResult.getScanRecord().getServiceData();
                bluetoothDevice = scanResult.getDevice();
                rssi = scanResult.getRssi();
                Log.i(TAG, "scanResult: " + map.toString() + " | Rssi: " + rssi);
                string = null;
                if (list == null || list.isEmpty()) break blockY;
                break blockX;
            }
            if (map == null || map.entrySet() == null) break blockX;
            for (Map.Entry entry : map.entrySet()) {
                String string3 = new String((byte[]) entry.getValue());
                UUID uuid = ((ParcelUuid) entry.getKey()).getUuid();
                string = this.discoveredDevices.get(bluetoothDevice.getAddress());
                if (string != null) continue;
                Log.w(TAG, "\nAPPKEY: " + BleManager.getInstance().getBleClient().getAppKey() +
                        "\nDeviceData: " + string3 +
                        "\nService UUID: " + uuid.toString() +
                        "\nCustom UUID: " + this.bleUuid2 +
                        "\nBT UUID: " + this.btUuid +
                        "\nBLE UUID: " + this.bleUuid);
                if (!uuid.toString().equalsIgnoreCase(this.bleUuid) && !uuid.toString().equalsIgnoreCase(this.btUuid) && !uuid.toString().equalsIgnoreCase(this.bleUuid2))
                    continue;
                string = BluetoothUtils.getUuidFromDataString(string3);
                this.discoveredDevices.put(bluetoothDevice.getAddress(), string);
                Log.i(TAG, "Device Found " + string3 + " userUuid: " + string);
            }
        }
        return string;
    }

    private void addToScheduledFuture(Device device, String string) {
        Log.i(TAG, "Device addToScheduledFuture " + device + " userUuid: " + string);
        //TODO
    }

    private void scanFailedAction(int errorCode) {
        this.setDiscoveryRunning(false);
        switch (errorCode) {
            case 1: {
                Log.e(TAG, "onScanFailed: Fails to start scan as BLE scan with the same settings is already started by the app | SCAN_FAILED_ALREADY_STARTED | code: " + errorCode);
                break;
            }
            case 2: {
                Log.e(TAG, "onScanFailed: Fails to start scan as app cannot be registered | SCAN_FAILED_APPLICATION_REGISTRATION_FAILED | code: " + errorCode);
                break;
            }
            case 23: {
                Log.e(TAG, "onScanFailed: Fails to start scan due an internal error | SCAN_FAILED_INTERNAL_ERROR | code: " + errorCode);
                break;
            }
            case 4: {
                Log.e(TAG, "onScanFailed: Fails to start power optimized scan as this feature is not supported | SCAN_FAILED_FEATURE_UNSUPPORTED | code: " + errorCode);
            }
        }
        Log.e(TAG, "SCANNING_FAILED_WITH_ERROR | code: " + errorCode);
    }

    private class scheduleDevice implements Runnable {
        private Device device;

        scheduleDevice(Device device) {
            this.device = device;
        }

        @Override
        public void run() {

        }
    }

}
