package com.flytekart.myapplication.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.myapplication.R;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Utilities;

public class LoginActivity extends AppCompatActivity {

    private TextView tvSignUp;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvSignUp = findViewById(R.id.tv_sign_up);
        tvLogin = findViewById(R.id.tv_login);

        tvSignUp.setOnClickListener(view -> {
            Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);
            finish();
        });

        tvLogin.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
            sharedPreferences.edit().putBoolean(Constants.IS_USER_LOGGED_IN, true).apply();
            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            finish();
        });
    }
}