package com.nova.android.shield.ui.notification;

import android.content.Context;
import android.os.AsyncTask;

import com.nova.android.shield.utils.Constants;

public class NotifAsyncOperation extends AsyncTask<Void, Void, Void> {

    private Constants.DatabaseOps operation;
    private NotificationRepository mRepository;
    private NotificationRecord notifRecord;

    public NotifAsyncOperation(Context context, NotificationRecord record) {
        this.mRepository = new NotificationRepository(context);
        this.notifRecord = new NotificationRecord(record.getTimestampStart(), record.getTimestampEnd(), record.getMsg(), record.getNotifType(), record.isNew());
        this.operation = Constants.DatabaseOps.INSERT;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        switch (operation) {
            case INSERT:{
                this.mRepository.insert(notifRecord);
                return null;
            }
            default:{
                return null;
            }
        }
    }
}
