package com.nova.android.shield.ui.home.tabs.alerts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.nova.android.shield.R;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.ui.notification.NotificationRecord;
import com.nova.android.shield.ui.notification.NotificationViewModel;
import com.nova.android.shield.utils.Constants;
import com.nova.android.shield.workmanager.demo.PullFromFirebaseTaskDemo;
import com.nova.android.shield.workmanager.workers.PullFromFirebaseWorker;

import java.util.LinkedList;
import java.util.List;

public class AlertsFragment extends Fragment {

    private static final String TAG = "[Nova][Shield][AlertsFragment]";

    private AlertsViewModel alertsViewModel;

    private NotificationViewModel notifViewModel;

    SwipeRefreshLayout swipeLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        alertsViewModel = new ViewModelProvider(this).get(AlertsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_alerts, container, false);

        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        this.swipeLayout = swipeRefreshLayout;

        swipeRefreshLayout.setOnRefreshListener(() -> AlertsFragment.this.refreshTask());

        RecyclerView notificationView = root.findViewById(R.id.recyclerViewNotifications);
        notificationView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationView.setAdapter(Constants.NotificationAdapter);

        notifViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        notifViewModel.getAllSorted().observe(getActivity(), records -> {
            List<NotificationRecord> currentNotifs = new LinkedList<>();
            for (NotificationRecord record : records) {
                currentNotifs.add(record);
            }
            Constants.NotificationAdapter.setRecords(currentNotifs, root.getRootView());
        });

        return root;
    }

    public void refreshTask() {
        Log.i(TAG, "refreshTask(): ");

        if (Constants.DEMO) {
            new PullFromFirebaseTaskDemo(getContext()).execute(new Void[0]);
        } else {
            PullFromFirebaseTask();
        }

        this.swipeLayout.setRefreshing(false);
    }

    public void PullFromFirebaseTask() {

        OneTimeWorkRequest oneTimePullRequest = (OneTimeWorkRequest) ((OneTimeWorkRequest.Builder) new OneTimeWorkRequest.Builder(PullFromFirebaseWorker.class).setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())).build();
        WorkManager.getInstance((Context) requireActivity()).enqueue((WorkRequest) oneTimePullRequest);
        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(oneTimePullRequest.getId()).observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {

                Log.i(TAG, "PullFromFirebaseTask(): state " + workInfo.getState());

                if (workInfo.getState() == WorkInfo.State.FAILED) {

                    Log.e(TAG, "PullFromFirebaseTask(): Failed");

                }

            }
        });

    }
}