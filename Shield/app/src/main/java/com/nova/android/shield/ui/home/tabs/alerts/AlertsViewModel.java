package com.nova.android.shield.ui.home.tabs.alerts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlertsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AlertsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Alerts fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}