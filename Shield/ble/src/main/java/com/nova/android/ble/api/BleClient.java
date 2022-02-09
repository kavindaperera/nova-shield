package com.nova.android.ble.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.nova.android.ble.core.BleCore;

public class BleClient {

    private static final String TAG = "[Ble][BleClient]";

    private String userUuid;

    private String appKey;

    private DeviceProfile deviceProfile;

    private BleClient(Builder builder) {
        this.userUuid = builder.userUuid;
        this.appKey = builder.appKey;
        this.deviceProfile = builder.deviceProfile;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public DeviceProfile getDeviceProfile() {
        return deviceProfile;
    }

    public String getUserUuid() {
        return this.userUuid;
    }

    static class Builder {

        private SharedPreferences sharedPreferences;

        private SharedPreferences.Editor editor;

        private String userUuid;

        private String appKey;

        private DeviceProfile deviceProfile;

        Builder(Context context) {
            if (context != null) {
                Context applicationContext = context.getApplicationContext();
                this.sharedPreferences = applicationContext.getSharedPreferences(BleCore.PREFS_NAME, 0);
                this.editor = sharedPreferences.edit();
                this.deviceProfile = new DeviceProfile(applicationContext);
                return;
            }
            throw new IllegalArgumentException("Context can not be null.");
        }

        public Builder setUserUuid(String userUuid) {
            this.userUuid = userUuid;
            return this;
        }


        public Builder setAppKey(String apiKey) {
            this.appKey = apiKey;
            return this;
        }

        public BleClient build() {
            this.editor.putString(BleCore.PREFS_USER_UUID, this.userUuid);
            this.editor.apply();
            return new BleClient(this);
        }
    }

}
