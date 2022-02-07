package com.nova.android.shield.ui.signup;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.nova.android.shield.R;
import com.nova.android.shield.auth.ShieldSession;
import com.nova.android.shield.ui.home.MainActivity;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText mUserName;
    @BindView(R.id.register_button)
    Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        username = (username != null || username.isEmpty()) ? username : Build.MANUFACTURER + " " + Build.MODEL;

        this.mUserName.setText(username);
        this.mUserName.setEnabled(true);
        this.mRegisterButton.setEnabled(true);
    }

    @OnClick({R.id.register_button})
    public void attemptRegister(View v) {
        SignupActivity signupActivity = SignupActivity.this;
        ShieldSession.setSession(signupActivity, mUserName.getText().toString(), UUID.randomUUID().toString());
        sendUserToMainActivity();
    }

    private void sendUserToMainActivity() {
        startActivity(new Intent(SignupActivity.this, MainActivity.class));
        finish();
    }
}