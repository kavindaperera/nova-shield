package com.nova.android.shield.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.nova.android.ble.api.BleManager;
import com.nova.android.ble.api.Device;
import com.nova.android.ble.api.callback.StateListener;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.main.ShieldApp;
import com.nova.android.shield.main.ShieldConstants;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.utils.Constants;
import com.nova.android.shield.utils.TimeUtils;
import com.nova.android.shield.utils.Utils;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class BluetoothUtils {

    private static final String TAG = "[Nova][Shield][BleUtils]";

    public static boolean isBluetoothOn() { // check whether bluetooth is ON
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    public static final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (!action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                return;
            }

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            int state =  mBluetoothAdapter.getState();
            switch (state){
                case BluetoothAdapter.STATE_ON: {
                    Log.d(TAG, "BluetoothAdapter.STATE_ON");
                    ShieldPreferencesHelper.setBluetoothEnabled(context);
                    break;
                }
                case BluetoothAdapter.STATE_OFF: {
                    Log.d(TAG, "BluetoothAdapter.STATE_OFF");
                    ShieldPreferencesHelper.setBluetoothEnabled(context, false);
                    break;
                }
            }
        }
    };

    public static final StateListener stateListener = new StateListener() {
        @Override
        public void onStartError(@NonNull String message, @NonNull int errorCode) {
            Log.e(TAG, "onStartError(): " + message);


        }

        @Override
        public void onStarted() {
            Log.e(TAG, "onStarted(): ");

        }

        @Override
        public void onRssiRead(@NonNull Device device, int rssi) {
            Log.e(TAG, "onRssiRead(): " + "| uuid: " + device.getUserId() + " | RSSI: " + rssi);

            String contactUuid = device.getUserId();
            int contactRssi = rssi;
            int notifLevel = 1;

            Context mContext = ShieldApp.getInstance();

            if (Constants.scanResultsUUIDs != null && !Constants.scanResultsUUIDs.contains(contactUuid) && BluetoothUtils.checkRssiThreshold(contactRssi) ){
                Log.e(TAG, "Found contact with UUID: " + contactUuid );

                Constants.scanResultsUUIDs.add(contactUuid);
                Constants.scanResultsUUIDsRSSIs.put(contactUuid, Integer.valueOf(contactRssi));
                Constants.scanResultsUUIDsTimes.put(contactUuid, Long.valueOf(TimeUtils.getTime()));


                if (!ShieldPreferencesHelper.getWhitelist(mContext).contains(contactUuid)){
                    Utils.sendNotification((Context) mContext, mContext.getString(ShieldConstants.string.distance_text), mContext.getString(ShieldConstants.string.distance_text2), notifLevel);
                }

            }
        }
    };

    public static void startBle(Context context) {
        Constants.scanResultsUUIDs = new ConcurrentSkipListSet<>();
        Constants.scanResultsUUIDsRSSIs = new ConcurrentHashMap<>();
        Constants.scanResultsUUIDsTimes = new ConcurrentHashMap<>();

        BleManager.initialize(context, ShieldPreferencesHelper.getUserUuid(context));
        BleManager.start(stateListener);
    }

    public static void stopBle(Context context) {
        BleManager.stop();
    }

    private static boolean checkRssiThreshold(int rssi) {

        //pass to machine learning model

        if (rssi >= -82) {
            return true;
        }
        return false;
    }

    public static void writeBleRecords(Context context) {
        if (Constants.scanResultsUUIDs != null && Constants.scanResultsUUIDsRSSIs != null && Constants.scanResultsUUIDsTimes != null) {
            Iterator<String> it = Constants.scanResultsUUIDs.iterator();
            while (it.hasNext()) {
                String uuid = it.next();
                if (Constants.scanResultsUUIDsRSSIs.containsKey(uuid) && Constants.scanResultsUUIDsTimes.containsKey(uuid)) {
                    Utils.bleRecordToDatabase(context, uuid, Constants.scanResultsUUIDsRSSIs.get(uuid).intValue(), Constants.scanResultsUUIDsTimes.get(uuid).longValue());
                }
            }
        }
    }

}
