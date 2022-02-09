package com.nova.android.shield.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.main.ShieldApp;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.service.ShieldService;
import com.nova.android.shield.ui.settings.SettingsActivity;
import com.nova.android.shield.ui.splash.SplashActivity;
import com.nova.android.shield.utils.Constants;
import com.nova.android.shield.utils.Utils;

import butterknife.ButterKnife;

public class TabbedMainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "[Nova][Shield][TabbedMainActivity]";

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShieldApp.debug = BuildConfig.DEBUG; // remove in production

        Log.i(TAG, "onCreate(): ");

        if (!ShieldSession.isLoggedIn()) {
            showSplashActivity();
            return;
        }

        initView(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart(): ");
        ShieldPreferencesHelper.getSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause(): ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume(): ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop(): ");
        ShieldPreferencesHelper.getSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy(): ");
    }

    private void showSplashActivity() {
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    private void initView(Bundle bundle) {

        setContentView(R.layout.activity_tabbed_main);

        ButterKnife.bind(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_friends, R.id.navigation_home, R.id.navigation_alerts).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

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
            ShieldPreferencesHelper.setUserUuid(getApplicationContext(),(String) null);
            ShieldPreferencesHelper.setBluetoothEnabled(getApplicationContext(), false);
            ShieldPreferencesHelper.setUsername(getApplicationContext(),(String) null);
            showSplashActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setShieldMenuItem(){
        if (ShieldPreferencesHelper.isBluetoothEnabled(getApplicationContext()) == true) {
            toolbar.getMenu().findItem(R.id.action_shield).setIcon(R.drawable.ic_baseline_pause_circle_filled_24).setTitle(R.string.action_stop_shield);
        } else {
            toolbar.getMenu().findItem(R.id.action_shield).setIcon(R.drawable.ic_baseline_play_circle_filled_24).setTitle(R.string.action_start_shield);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Constants.PREFS_BLUETOOTH_ENABLED)) {
            setShieldMenuItem();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult | requestCode: " + requestCode + " | resultCode: " + resultCode);
        if (requestCode == 0 ){
            if (resultCode != -1){
                Utils.updateBluetoothSwitchState(this);
            } else {
                ShieldPreferencesHelper.setBluetoothEnabled(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult(): ");
    }
}