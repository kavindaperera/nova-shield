package com.nova.android.shield.ui.main.tabs.home;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nova.android.shield.R;
import com.nova.android.shield.ui.settings.SettingsActivity;
import com.nova.android.shield.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

public class HomeFragment extends Fragment {

    @BindView(R.id.shieldLoaderAnimation)
    ImageView shieldLoaderAnimation;
    @BindView(R.id.shieldLoaderTextViewRetry)
    Button shieldLoaderTextViewRetry;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setContext(getContext());

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, root);

        if (this.homeViewModel.isShielding()) {
            startAnimation();
        } else {
            stopAnimation();
        }

        return root;
    }

    @OnClick(R.id.shieldLoaderTextViewRetry)
    public void onShield() {
        if (!this.homeViewModel.isShielding()) {
            this.homeViewModel.setShielding(true);
            startAnimation();
            return;
        } else {
            this.homeViewModel.setShielding(false);
            stopAnimation();
        }
    }

    public void startAnimation() {
        this.shieldLoaderAnimation.setImageDrawable(null);
        this.shieldLoaderTextViewRetry.setText(R.string.stop_shield_loader_text_retry);
        this.shieldLoaderAnimation.setBackground(getDrawable(getContext(), R.drawable.shield_animation_avatar_1));
        ((AnimationDrawable) this.shieldLoaderAnimation.getBackground()).start();
    }


    public void stopAnimation() {
        if (true) {
            this.shieldLoaderTextViewRetry.setText(R.string.shield_loader_text_retry);
            this.shieldLoaderAnimation.setImageDrawable(getDrawable(getContext(), R.drawable.il_shieldanim_avatar_1));
            this.shieldLoaderAnimation.setBackground(null);
        }
    }

}