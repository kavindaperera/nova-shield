package com.nova.android.ble.core;

import com.nova.android.ble.api.Device;
import com.nova.android.ble.logs.Log;

import io.reactivex.subscribers.DisposableSubscriber;

public class ConnectionSubscriber extends DisposableSubscriber<Device> {

    private static final String TAG = "[Nova][Ble][ConnectionSubscriber]";

    public ConnectionSubscriber() {
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        request(1L);
    }

    @Override
    public void onNext(Device device) {
        Log.d(TAG, "onNext: ");
        request(1L);
    }

    @Override
    public void onError(Throwable t) {
        Log.e(TAG, "onError: " + t.getMessage());
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
        if (!isDisposed()) {
            dispose();
        }
    }
}
