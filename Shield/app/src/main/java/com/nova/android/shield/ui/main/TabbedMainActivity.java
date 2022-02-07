package com.nova.android.shield.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nova.android.shield.BuildConfig;
import com.nova.android.shield.R;
import com.nova.android.shield.auth.ShieldSession;
import com.nova.android.shield.main.ShieldApp;
import com.nova.android.shield.service.ShieldService;
import com.nova.android.shield.ui.settings.SettingsActivity;
import com.nova.android.shield.ui.splash.SplashActivity;
import com.nova.android.shield.utils.Constants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabbedMainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ShieldApp.debug = BuildConfig.DEBUG; // remove in production

        if (!ShieldSession.isLoggedIn()) {
            showSplashActivity();
            return;
        }

        init(savedInstanceState);

    }

    private void showSplashActivity() {
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    private void init(Bundle bundle) {

        setContentView(R.layout.activity_tabbed_main);

        ButterKnife.bind(this);
        this.sharedPreferences = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        BottomNavigationView navView = findViewById(R.id.nav_view);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_friends, R.id.navigation_home, R.id.navigation_notifications)
                .build();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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

}