package com.nova.android.shield.ui.notification;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nova.android.shield.R;

import org.jetbrains.annotations.NotNull;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationViewHolder> {

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NotificationViewHolder notificationViewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView bodyMainText;
        TextView bodySubText;
        Button dismissButton;
        NotificationRecord notificationRecord;

        public NotificationViewHolder(View view) {
            super(view);
            bodyMainText = view.findViewById(R.id.textViewMessage);
            bodySubText = view.findViewById(R.id.textView13);
            dismissButton = view.findViewById(R.id.dismiss);

            dismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        void setNotificationRecord(NotificationRecord notificationRecord) {
            this.notificationRecord = notificationRecord;
            switch (notificationRecord.notif_type) {
                case 1: {
                    this.bodyMainText.setText(Resources.getSystem().getString(R.string.distance_text));
                    this.bodySubText.setText(Resources.getSystem().getString(R.string.distance_text2));
                    break;
                }
                case 2: {
                    this.bodyMainText.setText(Resources.getSystem().getString(R.string.text_notification_1));
                    this.bodySubText.setText(Resources.getSystem().getString(R.string.text_notification_2));
                    break;
                }
            }
        }

    }

}
