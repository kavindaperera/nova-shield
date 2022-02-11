package com.nova.android.shield.ui.home.tabs.friends;

import android.content.Context;
import android.content.Intent;
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

import com.github.clans.fab.FloatingActionButton;
import com.nova.android.shield.R;
import com.nova.android.shield.logs.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendsFragment extends Fragment {

    private static final String TAG = "[Nova][Shield][FriendsFragment]";

    private FriendsViewModel friendsViewModel;

    @BindView(R.id.fabShowBarcode)
    FloatingActionButton fabShowBarcode;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach(): ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate(): ");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView(): ");
        friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_friends, container, false);

        ButterKnife.bind(this, root);

        final TextView textView = root.findViewById(R.id.addFriendsDescription);

        friendsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }

    @OnClick(R.id.fabShowBarcode)
    public void showBarcode(View v) {
        startActivity(new Intent(v.getContext(), ShowBarcodeActivity.class));
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart(): ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume(): ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause(): ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop(): ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView(): ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy(): ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onCreate(): ");
    }


}