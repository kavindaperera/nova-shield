package com.nova.android.shield.ui.main.tabs.home;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nova.android.shield.utils.Constants;

import static android.content.Context.MODE_PRIVATE;

public class HomeViewModel extends ViewModel {

    private static String TAG = "[Nova][Shield][HomeViewModel]";

    Context context;

    SharedPreferences sharedPreferences;

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public boolean isShielding() {
        return this.sharedPreferences.getBoolean(Constants.PREFS_SHIELDING_STATE, false);
    }

    public void setShielding(boolean shielding) {
        sharedPreferences.edit().putBoolean(Constants.PREFS_SHIELDING_STATE, (Boolean) shielding).apply();
    }

    public void setContext(Context context) {
        this.context = context;
        this.sharedPreferences =  context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    }
}