package com.nova.android.shield.ui.notification;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nova.android.shield.R;
import com.nova.android.shield.logs.Log;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationViewHolder> {

    public static String TAG = "[Nova][Shield][NotificationRecyclerViewAdapter]";

    private Activity activity;
    private Context context;
    private List<NotificationRecord> notificationRecords = new ArrayList();
    int notifId = -1;
    NotificationRepository notificationRepository;

    public NotificationRecyclerViewAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.notificationRepository = new NotificationRepository(context);
    }

    public void setRecords(List<NotificationRecord> records, View view) {
        if (records.size() > this.notificationRecords.size()) {
            notifyItemInserted(0);
        } else {
            notifyDataSetChanged();
        }
        this.notificationRecords = records;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NotificationViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final NotificationRecord record = this.notificationRecords.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d");
        switch (record.notif_type) {
            case 1:{
                notifId = record.getId();
                holder.headerText.setText(this.context.getResources().getString(R.string.exposed_text));
                holder.messageText.setText(Html.fromHtml(record.msg + " " + this.context.getString(R.string.exposed_text2) + " <b>" + dateFormat.format(Long.valueOf(record.getTimestampStart())) + "."));
            }
            case 2:{
                // TODO
            }
        }

        holder.dismissButton.setOnClickListener(view -> {
            notificationRepository.deleteById(notifId);
        });


    }

    @Override
    public int getItemCount() {
        return this.notificationRecords.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;
        TextView messageText;
        TextView secondMessageText;
        Button dismissButton;

        public NotificationViewHolder(View view) {
            super(view);
            headerText = view.findViewById(R.id.textViewHeader);
            messageText = view.findViewById(R.id.textViewMessage);
            secondMessageText = view.findViewById(R.id.textViewMessage2);
            dismissButton = view.findViewById(R.id.dismiss);
        }

    }

}
