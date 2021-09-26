package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.request.LoginRequest;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.LoginResponse;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etClientCode;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Spinner spLoginType;
    private TextView tvSignUp;
    private TextView tvLogin;
    private TextInputLayout tilClientCode;
    private ProgressDialog progressDialog;

    String loginType;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spLoginType = findViewById(R.id.sp_login_type);
        etClientCode = findViewById(R.id.et_client_code);
        tilClientCode = findViewById(R.id.til_client_code);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tvSignUp = findViewById(R.id.tv_sign_up);
        tvLogin = findViewById(R.id.tv_login);

        ArrayAdapter<CharSequence> loginSpAdapter = ArrayAdapter.createFromResource(this,
                R.array.login_type_array, android.R.layout.simple_spinner_item);
        loginSpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLoginType.setAdapter(loginSpAdapter);
        spLoginType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loginType = getResources().getStringArray(R.array.login_type_array)[position];
                if (TextUtils.equals(loginType, Constants.LOGIN_TYPE_MAIN_ACCOUNT)) {
                    tilClientCode.setVisibility(View.GONE);
                } else if (TextUtils.equals(loginType, Constants.LOGIN_TYPE_CLIENT_ACCOUNT)) {
                    tilClientCode.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spLoginType.setSelection(0);
        loginType = Constants.LOGIN_TYPE_MAIN_ACCOUNT;
        tilClientCode.setVisibility(View.GONE);

        tvSignUp.setOnClickListener(view -> {
            Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);
            finish();
        });

        tvLogin.setOnClickListener(view -> {
            String usernameOrEmail = etEmail.getText().toString().trim();
            String clientId = etClientCode.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (loginType == null) {
                Toast.makeText(getApplicationContext(), R.string.enter_all_details, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.equals(loginType, Constants.LOGIN_TYPE_MAIN_ACCOUNT)) {
                if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.enter_all_details, Toast.LENGTH_SHORT).show();
                } else {
                    mainLogin(usernameOrEmail, password);
                }
            } else if (TextUtils.equals(loginType, Constants.LOGIN_TYPE_CLIENT_ACCOUNT)) {
                if (usernameOrEmail.isEmpty() || clientId.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.enter_all_details, Toast.LENGTH_SHORT).show();
                } else {
                    clientLogin(clientId, usernameOrEmail, password);
                }
            }
        });
    }

    private void mainLogin(String usernameOrEmail, String password) {
        /*JsonObject loginJson = new JsonObject();
        loginJson.addProperty("usernameOrEmail", usernameOrEmail);
        loginJson.addProperty("password", password);*/
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(usernameOrEmail);
        loginRequest.setPassword(password);
        Call<LoginResponse> loginCall = Flytekart.getApiService().mainLogin(loginRequest);
        tvSignUp.setEnabled(false);
        showProgress(true);
        loginCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                Logger.i("Main Login API call response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    // Get dropdown data and go to next screen.
                    Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constants.SHARED_PREF_KEY_IS_MAIN_ACCOUNT_LOGGED_IN, true);
                    editor.putString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, loginResponse.getTokenType() + " " + loginResponse.getAccessToken());
                    editor.apply();
                    Logger.i("Main Login API call success.");
                    Intent mainIntent = new Intent(LoginActivity.this, MainHomeActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else if (response.errorBody() != null) {
                    try {
                        ApiCallResponse apiCallResponse = new Gson().fromJson(
                                response.errorBody().string(), ApiCallResponse.class);
                        Toast.makeText(getApplicationContext(), apiCallResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Logger.e("Main Login API response status code : " + response.code());
            }

            @Override
            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                Logger.i("Main Login API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clientLogin(String clientId, String usernameOrEmail, String password) {
        /*JsonObject loginJson = new JsonObject();
        loginJson.addProperty("usernameOrEmail", usernameOrEmail);
        loginJson.addProperty("password", password);*/
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(usernameOrEmail);
        loginRequest.setPassword(password);
        showProgress(true);
        Call<LoginResponse> loginCall = com.flytekart.Flytekart.getApiService().clientLogin(clientId, loginRequest);
        loginCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                Logger.i("Client Login API call response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    // Get dropdown data and go to next screen.
                    Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constants.SHARED_PREF_KEY_IS_MAIN_ACCOUNT_LOGGED_IN, false);
                    editor.putString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, loginResponse.getTokenType() + " " + loginResponse.getAccessToken());
                    editor.putString(Constants.SHARED_PREF_KEY_CLIENT_ID, clientId);
                    editor.apply();
                    Logger.i("Client Login API call success.");
                    Intent mainIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else if (response.errorBody() != null) {
                    try {
                        ApiCallResponse apiCallResponse = new Gson().fromJson(
                                response.errorBody().string(), ApiCallResponse.class);
                        Toast.makeText(getApplicationContext(), apiCallResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Logger.e("Client Login API response status code : " + response.code());
            }

            @Override
            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                Logger.i("Client Login API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showProgress(boolean show) {
        if (show) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this);
            }
            progressDialog.setMessage(getResources().getString(R.string.progress_please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}