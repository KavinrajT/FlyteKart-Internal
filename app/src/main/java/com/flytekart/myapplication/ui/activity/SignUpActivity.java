package com.flytekart.myapplication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.myapplication.R;

public class SignUpActivity extends AppCompatActivity {

    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tvLogin = findViewById(R.id.tv_login);

        tvLogin.setOnClickListener(view -> {
            Intent signUpIntent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(signUpIntent);
            finish();
        });
    }
}