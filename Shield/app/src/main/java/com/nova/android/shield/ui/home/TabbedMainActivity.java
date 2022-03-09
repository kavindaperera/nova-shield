package com.nova.android.shield.ui.home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nova.android.ble.logs.BleLogger;
import com.nova.android.shield.R;
import com.nova.android.shield.auth.ShieldSession;
import com.nova.android.shield.ble.BleRecordRepository;
import com.nova.android.shield.ble.BluetoothUtils;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.ui.about.AboutActivity;
import com.nova.android.shield.ui.notification.NotificationRecyclerViewAdapter;
import com.nova.android.shield.ui.notification.NotificationViewModel;
import com.nova.android.shield.ui.settings.SettingsActivity;
import com.nova.android.shield.ui.splash.SplashActivity;
import com.nova.android.shield.utils.Constants;
import com.nova.android.shield.utils.PermissionLogic;
import com.nova.android.shield.utils.Utils;

import java.util.List;

import butterknife.ButterKnife;

import static com.nova.android.shield.utils.Constants.DEEP_LINK_QR;

public class TabbedMainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "[Nova][Shield][TabbedMainActivity]";

    Toolbar toolbar;

    private NotificationViewModel notifViewModel;

    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate(): ");

        if (!ShieldSession.isLoggedIn()) {
            showSplashActivity();
            return;
        }

        Constants.init(getApplicationContext());

        registerReceiver(BluetoothUtils.bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        notifViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

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

        navView = findViewById(R.id.nav_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Constants.NotificationAdapter = new NotificationRecyclerViewAdapter(this, this);

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

        // update notification badge
        notifViewModel.getNewNotifCount().observe(this, count -> setNotificationBadge(count));

    }

    private void setNotificationBadge(Integer count) {
        BadgeDrawable badgeDrawable = navView.getOrCreateBadge(R.id.navigation_alerts);

        if (count==0) {
            badgeDrawable.setVisible(false);
            return;
        }
        badgeDrawable.setBadgeTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.verify_error));
        badgeDrawable.setHorizontalOffsetWithText(10);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(count);

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

        if (id == R.id.action_clear_database) {
            BleRecordRepository recordRepository = new BleRecordRepository(getApplicationContext());
            recordRepository.deleteAll();
            Toast.makeText(this, "Database Records Cleared", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id. action_about){
            startActivity(new Intent(getBaseContext(), AboutActivity.class));
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
        PermissionLogic.permissionLogic(requestCode, permissions, grantResults, this);
    }
}