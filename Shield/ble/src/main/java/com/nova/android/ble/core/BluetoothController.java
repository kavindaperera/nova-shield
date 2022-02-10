package com.nova.android.ble.core;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;

import com.nova.android.ble.api.BleManager;
import com.nova.android.ble.api.BleUtils;
import com.nova.android.ble.core.exceptions.ConnectionException;
import com.nova.android.ble.logs.Log;

import com.nova.android.ble.api.BleException;

public class BluetoothController {

    private static final String TAG = "[Nova][Ble][BluetoothController]";

    public static int state = 0; // BLE Advertising | state:0 => stopped |  state:1 => failed |  state3 => running
    private static GattManager gattManager;
    private final Context context;
    private boolean isBLE = true; // BLE support check
    private BluetoothLeDiscovery bluetoothLeDiscovery;
    private ThreadServer threadServerBle;
    private BluetoothAdapter bluetoothAdapter;
    private BleAdvertiseCallback advertiseCallback;


    public BluetoothController(Context context) throws BleException {
        this.context = context;
        this.hardwareCheck();
        this.setBluetoothAdapter();
    }

    private void hardwareCheck() throws BleException {
        if (!getContext().getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            this.isBLE = false;
            Log.e(TAG, "Bluetooth Low Energy not supported.");
            throw new BleException(0, "Bluetooth Low Energy not supported.");
        } else {
            gattManager = new GattManager();
        }
    }

    private void setBluetoothAdapter() {
        BluetoothAdapter bluetoothAdapter = BleUtils.getBluetoothAdapter(getContext());
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public Context getContext() {
        return this.context;
    }

    public static GattManager getGattManager() {
        return gattManager;
    }


    public void onReceiveAction(Intent intent, Context context) {
        String actionLog = intent.getAction();
        Log.d(TAG, "onReceiveAction: " + actionLog);

        switch (intent.getAction()) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:{
                this.stateChangeAction(intent, context);
                break;
            }
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED: {

            }
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED: {

            }
            case BluetoothDevice.ACTION_FOUND:{

            }
            case BluetoothDevice.ACTION_UUID:{

            }
        }
    }

    private void stateChangeAction(Intent intent, Context context) {
        BluetoothController bluetooth_controller = this;
        synchronized (bluetooth_controller) {
            switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                case BluetoothAdapter.STATE_ON: {
                    Log.d(TAG, "BluetoothAdapter.STATE_ON");
                    try {
                        this.startServer(context.getApplicationContext());
                    } catch (ConnectionException e) {
                        e.printStackTrace();
                    }
                }
                case BluetoothAdapter.STATE_OFF: {
                    Log.d(TAG, "BluetoothAdapter.STATE_TURNING_OFF");
                    try {
                        this.stopServer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void startServer(Context context) throws ConnectionException {
        Log.d(TAG, "startServer: ");
        startBluetoothLeServer(context);
    }

    private void startBluetoothLeServer(Context context) {
        Log.d(TAG, "startBluetoothLeServer: " + isBLE );
        if (this.isBLE && this.bluetoothAdapter.getBluetoothLeAdvertiser() != null) {
            this.threadServerBle = ServerFactory.getServerInstance( true);
            try {
                this.threadServerBle.startServer();
            }
            catch (ConnectionException connectionException) {
                Log.e(TAG, "startBluetoothLeServer:", connectionException);
            }
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            String userUuid = BleManager.getInstance().getBleClient().getUserUuid();
            this.startAdvertising(userUuid);

        } else {
            //empty
            state = 1;
        }
    }


    @SuppressLint("MissingPermission")
    public boolean startAdvertising(String userUuid) throws IllegalStateException {
        Log.d(TAG, "startAdvertising(): state = " + state);
        if (state != 3) {
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if (this.bluetoothAdapter != null && this.bluetoothAdapter.getBluetoothLeAdvertiser() != null && this.threadServerBle != null && this.threadServerBle.getServerSocket() != null ) {
                    AdvertiseSettings advertiseSettings = BluetoothUtils.getAdvertiseSettings();
                    AdvertiseData advertiseData = BluetoothUtils.getAdvertiseData(userUuid);
                    BluetoothLeAdvertiser bluetoothLeAdvertiser = this.bluetoothAdapter.getBluetoothLeAdvertiser();
                    if (bluetoothLeAdvertiser != null) {
                        this.advertiseCallback = new BleAdvertiseCallback();
                        Log.i(TAG, "startAdvertising: " + advertiseData.toString());
                        bluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, this.advertiseCallback);
                    }
                    state = 3;
                    return true;
                }
                state = 1;
                return false;
            }
            catch (IllegalStateException il) {
                state = 0;
                return  false;
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        Log.e(TAG, "startAdvertising: advertising already running");
        return false;
    }

    @SuppressLint("MissingPermission")
    protected void stopAdvertising() {
        state = 0;
        try {
            if (this.bluetoothAdapter != null && this.bluetoothAdapter.getBluetoothLeAdvertiser() != null) {
                BluetoothLeAdvertiser bluetoothLeAdvertiser = this.bluetoothAdapter.getBluetoothLeAdvertiser();
                if (this.advertiseCallback != null && bluetoothLeAdvertiser != null) {
                    Log.i(TAG, "stopAdvertising: ");
                    bluetoothLeAdvertiser.stopAdvertising(this.advertiseCallback);
                }
            }
        }
        catch (NullPointerException nullPointerException) {
            Log.e(TAG, "stopAdvertising: exception " + nullPointerException.getMessage());
        }
    }

    public void stopServer() throws ConnectionException {
        Log.i(TAG, "stopServer: ");
        this.stopBluetoothLeServer();
    }

    private void stopBluetoothLeServer() {
        this.threadServerBle = ServerFactory.getServerInstance( false);
        if (this.threadServerBle != null) {
            try {
                this.threadServerBle.stopServer();
            }
            catch (ConnectionException connectionException) {
                connectionException.printStackTrace();
            }
            this.threadServerBle = null;
            ServerFactory.setBluetoothLeServer(null);
        }
        this.stopAdvertising();
    }


    public void startDiscovery(Context context) {
        this.startBluetoothLeDiscovery(context);
    }

    private void startBluetoothLeDiscovery(Context context) {
        Log.d(TAG, "startBluetoothLeDiscovery:");
        if (this.isBLE) {
            if (this.bluetoothLeDiscovery == null) {
                this.bluetoothLeDiscovery = new BluetoothLeDiscovery(context);
            } else {
                Log.w(TAG, "startBluetoothLeDiscovery: already exists");
            }
            if (!this.bluetoothLeDiscovery.isDiscoveryRunning()) {
                this.bluetoothLeDiscovery.startDiscovery(context);
            } else {
                Log.e(TAG, "startBluetoothLeDiscovery: discovery already running");
            }
        }
    }

    public void stopDiscovery(Context context) {

        if (this.bluetoothLeDiscovery != null ){
            this.bluetoothLeDiscovery.stopDiscovery(context);
        }

    }
}
