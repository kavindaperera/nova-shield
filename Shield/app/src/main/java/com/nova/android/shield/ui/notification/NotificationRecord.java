package com.nova.android.shield.ui.notification;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

@Entity(tableName = "notification_record_table")
public class NotificationRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "is_new")
    public boolean is_new;

    @NonNull
    @ColumnInfo(name = "msg")
    public String msg;

    @NonNull
    @ColumnInfo(name = "notif_type")
    public int notif_type;

    @NonNull
    @ColumnInfo(name = "timestamp_start")
    public long timestamp_start;

    @NonNull
    @ColumnInfo(name = "timestamp_end")
    public long timestamp_end;

    public NotificationRecord(long timestamp_start, long timestamp_end, String msg, int notif_type, boolean is_new) {
        this.timestamp_start = timestamp_start;
        this.timestamp_end = timestamp_end;
        this.msg = msg;
        this.notif_type = notif_type;
        this.is_new = is_new;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNew() {
        return is_new;
    }

    public String getMsg() {
        return msg;
    }

    public int getNotifType() {
        return notif_type;
    }

    public long getTimestampStart() {
        return timestamp_start;
    }


    public long getTimestampEnd() {
        return timestamp_end;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
