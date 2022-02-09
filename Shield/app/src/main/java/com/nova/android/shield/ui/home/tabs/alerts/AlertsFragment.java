package com.nova.android.shield.ui.home.tabs.alerts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nova.android.shield.R;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.ui.home.tabs.friends.FriendsFragment;

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
        this.swipeLayout.setRefreshing(false);
    }
}