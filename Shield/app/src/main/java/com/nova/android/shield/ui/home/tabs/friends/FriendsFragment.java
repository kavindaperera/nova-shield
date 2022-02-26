package com.nova.android.shield.ui.home.tabs.friends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.clans.fab.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.nova.android.shield.R;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.main.ShieldConstants;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendsFragment extends Fragment {

    private static final String TAG = "[Nova][Shield][FriendsFragment]";

    @BindView(R.id.fabShowBarcode)
    FloatingActionButton fabShowBarcode;

    @BindView(R.id.fabScanBarcode)
    FloatingActionButton scanBarcode;

    @BindView(R.id.fabRemove)
    FloatingActionButton resetWhitelist;

    @BindView(R.id.whiteListSize)
    TextView whiteListSizeText;
    private FriendsViewModel friendsViewModel;

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

    private final ActivityResultLauncher<ScanOptions> qrcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this.getActivity(), "Scanning cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    saveScannedResult(result.getContents());
                }
            });

    @OnClick(R.id.fabShowBarcode)
    public void showBarcode(View v) {
        startActivity(new Intent(v.getContext(), ShowBarcodeActivity.class));
    }

    private void saveScannedResult(String scannedUUID) {
        if (ShieldPreferencesHelper.addToWhitelist(getContext(), scannedUUID)) {
            Toast.makeText(this.getActivity(), "Already Whitelisted this Friend!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getActivity(), "Added Friend to Whitelist!", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.fabScanBarcode)
    public void scanBarcode(View v) {

        ScanOptions options = new ScanOptions();
        //options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
        options.setPrompt("Scan QR Code of Your Friend");
        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        qrcodeLauncher.launch(options);
    }

    @OnClick(R.id.fabRemove)
    public void ResetWhitelist() {
        ShieldPreferencesHelper.resetWhitelist(getContext());
        hideResetWhitelistFab();
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart(): ");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume(): ");
        int size = ShieldPreferencesHelper.getWhitelist(getActivity()).size();
        Log.e(TAG, "whitelist size: " + size);
        if (size > 0) {
            showResetWhitelistFab(size);
        } else {
            hideResetWhitelistFab();
        }
    }

    private void showResetWhitelistFab(int size) {
        this.resetWhitelist.show(true);
        if (size == 1) {
            this.whiteListSizeText.setText("You have 1 whitelisted friend.");
            return;
        }
        this.whiteListSizeText.setText("You have " + size + " whitelisted friends.");
    }

    private void hideResetWhitelistFab() {
        this.resetWhitelist.hide(true);
        this.whiteListSizeText.setText(ShieldConstants.string.whitelist_description);
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