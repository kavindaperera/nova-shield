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
import com.nova.android.shield.utils.Constants;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "[Nova][Shield][SettingsFragment]";
    private static final String KEY_RINGTONE_PREFERENCE = "notificationSound";
    private static final int REQUEST_CODE_ALERT_RINGTONE = 83216;
    Preference verification;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(Constants.PREFS_NAME);
        setPreferencesFromResource(R.xml.preferences, rootKey);
        if (ShieldSession.isVerified()) {
            Preference preference = findPreference("verification");
            this.verification = preference;
            preference.setSummary("Verified");
            preference.setOnPreferenceClickListener(preference1 -> {
                Toast.makeText(getContext(), "Phone Number Already Verified", Toast.LENGTH_SHORT).show();
                return false;
            });
        }
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