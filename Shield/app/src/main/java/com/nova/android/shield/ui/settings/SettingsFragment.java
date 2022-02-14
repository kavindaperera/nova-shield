package com.nova.android.shield.ui.settings;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.nova.android.shield.R;
import com.nova.android.shield.auth.ShieldSession;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.ui.splash.SplashActivity;
import com.nova.android.shield.utils.Constants;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "[Nova][Shield][SettingsFragment]";
    private static final String KEY_DELETE_ACCOUNT_PREFERENCE = "deleteAccount";
    private static final String KEY_RINGTONE_PREFERENCE = "notificationSound";
    private static final String KEY_NOTIFICATION_SETTINGS_PREFERENCE = "notificationSettings";

    private static final int REQUEST_CODE_ALERT_RINGTONE = 83216;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(Constants.PREFS_NAME);
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }


    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        Log.e(TAG, " " + preference.getKey().equals(KEY_RINGTONE_PREFERENCE));
        if (preference.getKey().equals(KEY_RINGTONE_PREFERENCE)) {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);

            String existingValue = getRingtonePreferenceValue(); // TODO
            if (existingValue != null) {
                if (existingValue.length() == 0) {
                    // Select "Silent"
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                } else {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));
                }
            } else {
                // No ringtone has been selected, set to the default
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
            }
            startActivityForResult(intent, REQUEST_CODE_ALERT_RINGTONE);
            return true;
        } else if (preference.getKey().equals(KEY_NOTIFICATION_SETTINGS_PREFERENCE)){

            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, Constants.PACKAGE_NAME);

            startActivity(intent);
            return true;
        } else if (preference.getKey().equals(KEY_DELETE_ACCOUNT_PREFERENCE)) {
            Toast.makeText(getContext(), "You can't delete the account at this moment", Toast.LENGTH_LONG).show();
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    private String getRingtonePreferenceValue() {
        return null;
    }

    private void setRingtonePreferenceValue(String ringtonePreferenceValue) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ALERT_RINGTONE && data != null) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtone != null) {
                setRingtonePreferenceValue(ringtone.toString()); // TODO
            } else {
                // "Silent" was selected
                setRingtonePreferenceValue(""); // TODO
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}