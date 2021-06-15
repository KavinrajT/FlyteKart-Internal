package com.flytekart.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;

import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.R;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Utilities;

public class SplashActivity extends AppCompatActivity {

    private boolean isAppLoadCancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if (!isAppLoadCancelled) {
                SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
                String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, "");
                if (!accessToken.isEmpty()) {
                    boolean isUserLoggedInMainAccount = sharedPreferences.getBoolean
                            (Constants.SHARED_PREF_KEY_IS_MAIN_ACCOUNT_LOGGED_IN, false);
                    if (isUserLoggedInMainAccount) {
                        Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                    } else {
                        Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                    }
                } else {
                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
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