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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.nova.android.shield.R;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.utils.Constants;
import com.nova.android.shield.workmanager.demo.PullFromFirebaseTaskDemo;
import com.nova.android.shield.workmanager.workers.PullFromFirebaseWorker;

public class AlertsFragment extends Fragment {

    private static final String TAG = "[Nova][Shield][AlertsFragment]";

    private AlertsViewModel alertsViewModel;

    SwipeRefreshLayout swipeLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        alertsViewModel = new ViewModelProvider(this).get(AlertsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_alerts, container, false);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefresh);
        this.swipeLayout = swipeRefreshLayout;

        swipeRefreshLayout.setOnRefreshListener(() -> AlertsFragment.this.refreshTask());

        return root;
    }

    public void refreshTask() {
        Log.i(TAG, "refreshTask(): ");

        if (Constants.DEBUG) {
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