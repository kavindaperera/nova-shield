package com.nova.android.shield.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nova.android.shield.BuildConfig;
import com.nova.android.shield.R;
import com.nova.android.shield.auth.ShieldSession;
import com.nova.android.shield.main.ShieldApp;
import com.nova.android.shield.service.ShieldService;
import com.nova.android.shield.ui.settings.SettingsActivity;
import com.nova.android.shield.ui.splash.SplashActivity;
import com.nova.android.shield.utils.Constants;

import butterknife.ButterKnife;

public class TabbedMainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "[Nova][TabbedMainActivity]";

    SharedPreferences sharedPreferences;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ShieldApp.debug = BuildConfig.DEBUG; // remove in production

        if (!ShieldSession.isLoggedIn()) {
            showSplashActivity();
            return;
        }

        this.sharedPreferences = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        initUi(savedInstanceState);

    }

    private void showSplashActivity() {
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    private void initUi(Bundle bundle) {

        setContentView(R.layout.activity_tabbed_main);

        ButterKnife.bind(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_friends, R.id.navigation_home, R.id.navigation_notifications).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {
        getMenuInflater().inflate(R.menu.menu_main, menu2);
        setShieldMenuItem();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_shield) {
            // TODO
        }

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

    private void setShieldMenuItem(){
        if (sharedPreferences.getBoolean(Constants.PREFS_SHIELDING_STATE, false) == true) {
            toolbar.getMenu().findItem(R.id.action_shield).setIcon(R.drawable.ic_baseline_pause_circle_filled_24).setTitle(R.string.action_stop_shield);
        } else {
            toolbar.getMenu().findItem(R.id.action_shield).setIcon(R.drawable.ic_baseline_play_circle_filled_24).setTitle(R.string.action_start_shield);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Constants.PREFS_SHIELDING_STATE)) {
            setShieldMenuItem();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}