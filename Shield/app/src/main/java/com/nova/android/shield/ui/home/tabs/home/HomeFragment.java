package com.nova.android.shield.ui.home.tabs.home;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nova.android.shield.R;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.ui.settings.PermissionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

public class HomeFragment extends Fragment {

    private static final String TAG = "[Nova][Shield][HomeFragment]";

    @BindView(R.id.shieldLoaderAnimation)
    ImageView shieldLoaderAnimation;

    @BindView(R.id.shieldingSwitch)
    FloatingActionButton shieldingSwitch;

    private HomeViewModel homeViewModel;

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

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, root);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        return root;
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
        boolean shieldingEnabled = ShieldPreferencesHelper.isBluetoothEnabled(getActivity());
        if (shieldingEnabled) {
            startAnimation();
        } else {
            stopAnimation();
        }
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

    @OnClick(R.id.shieldingSwitch)
    public void onShield() {
        boolean isSwitched = false;
        boolean shieldingEnabled = ShieldPreferencesHelper.isBluetoothEnabled(getActivity());
        if (shieldingEnabled) { // check whether already enabled
            isSwitched = true;
        }
        this.shieldingSwitchLogic(isSwitched);
    }

    public void shieldingSwitchLogic(Boolean isSwitched) {
        if (isSwitched) {
            ShieldPreferencesHelper.setBluetoothEnabled(getActivity(), false);
            updateHomeUI(false);
        } else {
            PermissionUtils.bluetoothSwitchLogic(getActivity());
            updateHomeUI(true);
        }
    }

    public void updateHomeUI(Boolean play) {
        if (play) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    public void startAnimation() {
        this.shieldLoaderAnimation.setImageDrawable(null);
        this.shieldingSwitch.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_baseline_pause_24));
        this.shieldLoaderAnimation.setBackground(getDrawable(getContext(), R.drawable.shield_animation_avatar_1));
        ((AnimationDrawable) this.shieldLoaderAnimation.getBackground()).start();
    }

    public void stopAnimation() {
        this.shieldingSwitch.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_baseline_play_arrow_24));
        this.shieldLoaderAnimation.setImageDrawable(getDrawable(getContext(), R.drawable.il_shieldanim_avatar_1));
        this.shieldLoaderAnimation.setBackground(null);
    }

}