package com.nova.android.ble.core;

import android.content.Context;

import com.nova.android.ble.api.Device;
import com.nova.android.ble.logs.Log;

import io.reactivex.Flowable;
import io.reactivex.subscribers.DisposableSubscriber;

public abstract class Discovery {

    protected String TAG = "[Nova][Ble][Discovery]";

    public DisposableSubscriber<Device> disposableSubscriber;

    public Flowable<Device> deviceFlowable; //emits device objects

    private boolean discoveryRunning = false;

    public Discovery() {
    }

    public void startDiscovery(Context context) {
        Log.d(TAG, "startDiscovery:");
        this.disposableSubscriber = new ConnectionSubscriber();
        this.deviceFlowable.subscribe(this.disposableSubscriber); //subscribe to the connection
    }

    public void stopDiscovery(Context context) {
        Log.d(TAG, "stopDiscovery:");
        if (this.disposableSubscriber != null) {
            this.disposableSubscriber.dispose();
        }
    }

    public boolean isDiscoveryRunning() {
        return discoveryRunning;
    }

    public void setDiscoveryRunning(boolean discoveryRunning) {
        this.discoveryRunning = discoveryRunning;
    }

}
