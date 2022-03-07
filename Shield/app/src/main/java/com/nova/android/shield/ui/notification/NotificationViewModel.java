package com.nova.android.shield.ui.notification;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NotificationViewModel extends AndroidViewModel {

    private NotificationRepository mRepository;
    private LiveData<List<NotificationRecord>> mRecords;
    private LiveData<Integer> mNewRecordCount;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        if (mRecords != null){
            return;
        }
        this.mRepository = new NotificationRepository(application);
        this.mRecords = mRepository.getAllSortedRecords();
        this.mNewRecordCount = mRepository.getNewNotificationCount();
    }

    public LiveData<List<NotificationRecord>> getAllSorted() {
        return this.mRecords;
    }

    public LiveData<Integer> getNewNotifCount() {
        return this.mNewRecordCount;
    }

    public void deleteAll() {
        this.mRepository.deleteAll();
    }
}
