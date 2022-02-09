package com.nova.android.shield.ui.home.tabs.friends;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FriendsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FriendsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Mute alerts for friends.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}