package com.nova.android.ble.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;

import com.nova.android.ble.BuildConfig;
import com.nova.android.ble.api.callback.StateListener;
import com.nova.android.ble.core.BleCore;

import java.util.UUID;

public class BleManager {

    public static boolean debug = true;

    private static final String TAG = "[Nova][Ble][BleManager]";

    static BleManager INSTANCE;

    private BleCore bleCore;

    private BleClient bleClient;

    private Context context;

    public static BleManager getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        throw new IllegalStateException("BleManager must be initialized before trying to reference it.");
    }


    private static void createInstance(BleManager bleManager) {
        INSTANCE = bleManager;
    }

    private BleManager (Builder builder) {
        this.context = builder.context;
        this.bleClient = builder.bleClient;
    }

    public static void initialize (Context context) {
        BleManager.initialize(context, (String) null,  (UUID) null);
    }

    private static void initialize(Context context, @Nullable String providedApiKey, UUID uuid)  {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BleCore.PREFS_NAME, 0);
        String loadApiKey = BleManager.loadAppKey(context, providedApiKey);
        String string = sharedPreferences.getString(BleCore.PREFS_USER_UUID, (String) null);

        if ((uuid == null || uuid.toString().equals(string)) && string != null) {
            BleManager.create(context, new BleClient.Builder(context).setAppKey(loadApiKey).setUserUuid(string).build());
            return;
        }

        try {
            String uuid2 = uuid == null ? UUID.randomUUID().toString() : uuid.toString();
            BleClient meshifyClient = new BleClient.Builder(context).setAppKey(loadApiKey).setUserUuid(uuid2).build();
            BleManager.create(context, meshifyClient);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static void create(Context context, BleClient meshifyClient) {
        synchronized (BleManager.class) {
            createInstance(new Builder(context, meshifyClient).build());
        }
    }

    private static String loadAppKey(Context context,@Nullable String providedApiKey) throws IllegalArgumentException {
        block4: {
            SharedPreferences sharedPreferences = context.getSharedPreferences(BuildConfig.LIBRARY_PACKAGE_NAME, 0);
            if (providedApiKey == null) {
                try {
                    providedApiKey = (String) context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.get(BleCore.PREFS_APP_KEY);

                    if (providedApiKey == null && (providedApiKey = sharedPreferences.getString(BleCore.PREFS_APP_KEY, (String) null)) == null) {
                        throw new IllegalArgumentException("Missing UUID KEY ");
                    }
                    break block4;
                }
                catch (Exception exception) {
                    throw new IllegalArgumentException("Missing or incorrect UUID KEY");
                }
            }
            sharedPreferences.edit().putString(BleCore.PREFS_APP_KEY, providedApiKey).apply();
        }
        return providedApiKey;
    }

    public BleClient getBleClient() {
        return this.bleClient;
    }

    public BleCore getBleCore() {
        return this.bleCore;
    }

    public void setBleCore(BleCore bleCore) {
        this.bleCore = bleCore;
    }

    public static void start(@Nullable StateListener stateListener) {
        if (getInstance().getBleCore() == null) {
            BleUtils.initialize(getInstance().getContext());
            getInstance().setBleCore(new BleCore(getInstance().getContext()));
            getInstance().getBleCore().setStateListener(stateListener);
            getInstance().getBleCore().initializeServices();
            if (stateListener != null) {
                stateListener.onStarted();
            }
        }
    }

    public Context getContext() {
        return this.context;
    }

    private static class Builder {

        private final Context context;

        private final BleClient bleClient;

        Builder(Context context, BleClient bleClient1) {

            if (context == null | bleClient1 == null) {
                throw new IllegalArgumentException("Context or Client is null.");
            }

            this.context = context.getApplicationContext();
            this.bleClient = bleClient1;

        }

        public BleManager build() {
            return new BleManager(this);
        }
    }
}
