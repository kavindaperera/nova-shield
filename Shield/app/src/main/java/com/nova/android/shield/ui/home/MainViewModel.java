package com.nova.android.shield.ui.home;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.nova.android.shield.logs.Log;
import com.nova.android.shield.utils.Constants;

import static android.content.Context.MODE_PRIVATE;

public class MainViewModel extends AndroidViewModel {

    private static String TAG = "[Nova][Shield][MainViewModel]";

    SharedPreferences sharedPreferences;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.sharedPreferences = application.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.e(TAG, "onCleared():");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.e(TAG, "finalize():");
    }

    public boolean isShielding() {
        return this.sharedPreferences.getBoolean(Constants.PREFS_SHIELDING_STATE, false);
    }

    public void setShielding(boolean shielding) {
        sharedPreferences.edit().putBoolean(Constants.PREFS_SHIELDING_STATE, (Boolean) shielding).apply();
    }
}
