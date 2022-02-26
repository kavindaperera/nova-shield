package com.nova.android.shield.ui.home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nova.android.ble.api.BleManager;
import com.nova.android.ble.api.Device;
import com.nova.android.ble.api.callback.StateListener;
import com.nova.android.ble.logs.BleLogger;
import com.nova.android.shield.BuildConfig;
import com.nova.android.shield.R;
import com.nova.android.shield.auth.ShieldSession;
import com.nova.android.shield.ble.BluetoothUtils;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.main.ShieldApp;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.service.ShieldService;
import com.nova.android.shield.ui.settings.PermissionUtils;
import com.nova.android.shield.ui.settings.SettingsActivity;
import com.nova.android.shield.ui.splash.SplashActivity;
import com.nova.android.shield.utils.Constants;
import com.nova.android.shield.utils.Utils;

import java.security.Permission;
import java.util.List;

import butterknife.ButterKnife;

import static com.nova.android.shield.utils.Constants.DEEP_LINK_QR;

public class TabbedMainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "[Nova][Shield][TabbedMainActivity]";

    Toolbar toolbar;

    private final BroadcastReceiver permissionBroadcastReceiver = new PermissionBroadcastReceiver();

    class PermissionBroadcastReceiver extends BroadcastReceiver {
        PermissionBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.LOCATION_PERMISSION)) {
                ActivityCompat.requestPermissions(TabbedMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate(): ");

        if (!ShieldSession.isLoggedIn()) {
            showSplashActivity();
            return;
        }

        registerReceiver(BluetoothUtils.bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        initView(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart(): ");
        ShieldPreferencesHelper.getSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.permissionBroadcastReceiver);
        super.onPause();
        Log.i(TAG, "onPause(): ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume(): ");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.LOCATION_PERMISSION);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.permissionBroadcastReceiver, intentFilter);
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
        try {
            unregisterReceiver(BluetoothUtils.bluetoothReceiver);
        } catch (Exception e) {
            Log.e(NotificationCompat.CATEGORY_ERROR, e.getMessage());
        }
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


        // listen to deep links
        Uri uri = getIntent().getData();
        if (uri != null){
            List<String> params = uri.getPathSegments();
            String intent = params.get(0);
            switch (intent) {
                case DEEP_LINK_QR: {
                    String encryptedUuid = params.get(params.size() - 1); // extract uuid

                    //TODO - decrypt UUID @JudeRanidu

                    navController.navigate(R.id.navigation_friends); //move to friends fragment

                    if (ShieldPreferencesHelper.addToWhitelist(getApplicationContext(), encryptedUuid)) {
                        Toast.makeText(this, "Already Whitelisted this Friend!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Added Friend to Whitelist!", Toast.LENGTH_LONG).show();
                    }

                    break;
                }
            }
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {
        getMenuInflater().inflate(R.menu.menu_main, menu2);
        setShielding();
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

        if (id == R.id.action_clear_logs) {
            if (ShieldPreferencesHelper.getLogPermission(this)) {
                try {
                    BleLogger.getInstance();
                } catch (Exception e) {
                    Toast.makeText(this, "Error occurred while clearing logs, try restarting the app", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return true;
                }
                Toast.makeText(this, "Logs Cleared", Toast.LENGTH_SHORT).show();
                BleLogger.clearLogs();
            } else {
                Toast.makeText(this, "Logs are not enabled", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setShielding(){
        if (ShieldPreferencesHelper.isBluetoothEnabled(getApplicationContext()) == true) {
            toolbar.getMenu().findItem(R.id.action_shield).setIcon(R.drawable.ic_baseline_pause_circle_filled_24).setTitle(R.string.action_stop_shield);
        } else {
            toolbar.getMenu().findItem(R.id.action_shield).setIcon(R.drawable.ic_baseline_play_circle_filled_24).setTitle(R.string.action_start_shield);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Constants.PREFS_BLUETOOTH_ENABLED)) {
            if (toolbar.getMenu().findItem(R.id.action_shield) != null){
                setShielding();
            }
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
                if (!Constants.ShieldingServiceRunning) {
                    Utils.startShieldingService(this);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult(): ");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please restart the app", Toast.LENGTH_LONG).show(); // implement this
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Location permissions is needed to start shielding!", Toast.LENGTH_SHORT).show(); //close app on deny permissions
            finish();
        }
    }
}