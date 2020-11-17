package com.flytekart.myapplication.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;

import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.myapplication.R;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Utilities;

public class SplashActivity extends AppCompatActivity {

    private boolean isAppLoadCancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if (!isAppLoadCancelled) {
                SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
                boolean isUserLoggedIn = sharedPreferences.getBoolean(Constants.IS_USER_LOGGED_IN, false);
                if (isUserLoggedIn) {
                    Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                } else {
                    Intent introIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(introIntent);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        }, 2000);

        setupWindowAnimations();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isAppLoadCancelled = true;
    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fadeOut = new Fade(Fade.OUT);
            fadeOut.setDuration(1000);
            getWindow().setEnterTransition(fadeOut);

            Fade fadeIn = new Fade(Fade.IN);
            fadeIn.setDuration(1000);
            getWindow().setExitTransition(fadeIn);
        }
    }
}