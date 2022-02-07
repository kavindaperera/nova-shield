package com.nova.android.shield.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.nova.android.shield.BuildConfig;
import com.nova.android.shield.R;
import com.nova.android.shield.auth.ShieldSession;
import com.nova.android.shield.main.ShieldApp;
import com.nova.android.shield.service.ShieldService;
import com.nova.android.shield.ui.settings.SettingsActivity;
import com.nova.android.shield.ui.splash.SplashActivity;
import com.nova.android.shield.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @BindView(R.id.shieldLoaderAnimation)
    ImageView shieldLoaderAnimation;
    @BindView(R.id.shieldLoaderTextViewRetry)
    Button shieldLoaderTextViewRetry;
    /* ViewModel */
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ShieldApp.debug = BuildConfig.DEBUG; // remove in production

        if (ShieldSession.isLoggedIn()) {
            init(savedInstanceState);
            return;
        }

        showSplashActivity();

    }

    private void showSplashActivity() {
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    private void init(Bundle bundle) {

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.sharedPreferences = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        /*Configure the Toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        /*ViewModel*/
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);


        if (this.mainViewModel.isShielding()) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    @OnClick(R.id.shieldLoaderTextViewRetry)
    public void onShield() {
        if (!this.mainViewModel.isShielding()) {
            this.mainViewModel.setShielding(true);
            startAnimation();
            return;
        } else {
            this.mainViewModel.setShielding(false);
            stopAnimation();
        }
    }


    public void startAnimation() {
        this.shieldLoaderAnimation.setImageDrawable(null);
        this.shieldLoaderTextViewRetry.setText(R.string.stop_shield_loader_text_retry);
        this.shieldLoaderAnimation.setBackground(getDrawable(R.drawable.shield_animation_avatar_1));
        ((AnimationDrawable) this.shieldLoaderAnimation.getBackground()).start();
    }


    public void stopAnimation() {
        if (true) {
            this.shieldLoaderTextViewRetry.setText(R.string.shield_loader_text_retry);
            this.shieldLoaderAnimation.setImageDrawable(getDrawable(R.drawable.il_shieldanim_avatar_1));
            this.shieldLoaderAnimation.setBackground(null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        if (id == R.id.action_logout) {
            sharedPreferences.edit().putString(Constants.PREFS_USER_UUID, (String) null).apply();
            sharedPreferences.edit().putBoolean(Constants.PREFS_SHIELDING_STATE, (Boolean) false).apply();
            sharedPreferences.edit().putString(Constants.PREFS_USERNAME, (String) null).apply();
            showSplashActivity();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(new Intent(this, ShieldService.class).setAction(Constants.SHIELD_APP_BACKGROUND));
        } else {
            startService(new Intent(this, ShieldService.class).setAction(Constants.SHIELD_APP_BACKGROUND));
        }
    }

}