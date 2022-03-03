package com.nova.android.shield.ui.notification;

import androidx.room.Entity;

import com.google.gson.Gson;

@Entity(tableName = "notification_record_table", primaryKeys = {"id", "timestamp_start"})
public class NotificationRecord {

    private int id; // auto-generated id

    public boolean is_new;

    public String msg;

    public int notif_type;

    public long timestamp_start;

    public long timestamp_end;

    public NotificationRecord(long timestamp_start2, long timestamp_end2, String msg2, int notif_type2, boolean is_new2) {
        this.timestamp_start = timestamp_start2;
        this.timestamp_end = timestamp_end2;
        this.msg = msg2;
        this.notif_type = notif_type2;
        this.is_new = is_new2;
    }

    public int getId() {
        return id;
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
