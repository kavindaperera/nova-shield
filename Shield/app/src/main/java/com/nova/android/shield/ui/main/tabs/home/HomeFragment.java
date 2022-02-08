package com.nova.android.shield.ui.main.tabs.home;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nova.android.shield.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

public class HomeFragment extends Fragment {

    @BindView(R.id.shieldLoaderAnimation)
    ImageView shieldLoaderAnimation;

    @BindView(R.id.fabStartShielding)
    FloatingActionButton fabStartShielding;

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

    @OnClick(R.id.fabStartShielding)
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
        this.fabStartShielding.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_baseline_pause_24));
        this.shieldLoaderAnimation.setBackground(getDrawable(getContext(), R.drawable.shield_animation_avatar_1));
        ((AnimationDrawable) this.shieldLoaderAnimation.getBackground()).start();
    }


    public void stopAnimation() {
        if (true) {
            this.fabStartShielding.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_baseline_play_arrow_24));
            this.shieldLoaderAnimation.setImageDrawable(getDrawable(getContext(), R.drawable.il_shieldanim_avatar_1));
            this.shieldLoaderAnimation.setBackground(null);
        }
    }

}