package com.nova.android.shield.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nova.android.shield.R;
import com.nova.android.shield.logs.Log;
import com.nova.android.shield.main.ShieldConstants;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;
import com.nova.android.shield.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends PreferenceFragmentCompat{

    private static final String TAG = "[Nova][Shield][SettingsFragment]";
    private static final String KEY_DELETE_ACCOUNT_PREFERENCE = "deleteAccount";
    private static final String KEY_MARK_INFECTED_PREFERENCE = "markInfected";
    private static final String KEY_RINGTONE_PREFERENCE = "notificationSound";
    private static final String KEY_NOTIFICATION_SETTINGS_PREFERENCE = "notificationSettings";
    private static final String KEY_FEEDBACK = "feedback";

    private static final int REQUEST_CODE_ALERT_RINGTONE = 83216;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(Constants.PREFS_NAME);
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(KEY_RINGTONE_PREFERENCE)) {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);

            String existingValue = getRingtonePreferenceValue();
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
        }
        else if (preference.getKey().equals(KEY_NOTIFICATION_SETTINGS_PREFERENCE)) {

            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, Constants.PACKAGE_NAME);

            startActivity(intent);
            return true;
        }
        else if (preference.getKey().equals(KEY_DELETE_ACCOUNT_PREFERENCE)) {
            Toast.makeText(getContext(), "You can't delete the account at this moment", Toast.LENGTH_LONG).show();
            return true;
        }
        else if (preference.getKey().equals(KEY_MARK_INFECTED_PREFERENCE)) {
            this.showConfirmationInfectedDialog();
            return true;
        }
        else if (preference.getKey().equals(KEY_FEEDBACK)){
            Intent Email = new Intent(Intent.ACTION_SENDTO);
            Email.setType("text/email");
            Email.setData(Uri.parse("mailto:")); // only email apps should handle this
            Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "admin@novalabs.lk" });
            Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            startActivity(Intent.createChooser(Email, "Send Feedback"));
            return true;
        }
        else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    private void markAsInfected() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();

        data.put("uuid", ShieldPreferencesHelper.getUserUuid(getContext()).toString());
        data.put("date", FieldValue.serverTimestamp());

        db.collection("infected-users")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    Toast.makeText(getActivity(), "Marked yourself as Covid-19 infected", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                            Log.e(TAG, "Error adding document " + e);
                            Toast.makeText(getContext(), "Error whole marking infected", Toast.LENGTH_SHORT).show();
                        }
                );
    }

    private void showConfirmationInfectedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(ShieldConstants.string.confirm_infected_dialog_text))
                .setCancelable(false)
                .setPositiveButton("Confirm", (dialog, which) -> this.markAsInfected())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private String getRingtonePreferenceValue() {
        return ShieldPreferencesHelper.getNotificationSound(getContext());
    }

    private void setRingtonePreferenceValue(Uri ringtonePreferenceValue) {
        if (ringtonePreferenceValue!=null){
            ShieldPreferencesHelper.setNotificationSound(getContext(), ringtonePreferenceValue.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ALERT_RINGTONE && data != null) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtone != null) {
                setRingtonePreferenceValue(ringtone);
                Toast.makeText(getContext(), "Notification Sound Changed", Toast.LENGTH_SHORT).show();
            } else {
                // "Silent" was selected
                setRingtonePreferenceValue(null);
                Toast.makeText(getContext(), "Notification Sound Silenced", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}