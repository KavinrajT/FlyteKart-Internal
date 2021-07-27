package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.User;
import com.flytekart.utils.Logger;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputEditText etUserName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhoneNumber;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextView tvSignUp;
    private TextView tvLogin;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.et_name);
        etUserName = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_cnf_password);
        tvSignUp = findViewById(R.id.tv_sign_up);
        tvLogin = findViewById(R.id.tv_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        tvLogin.setOnClickListener(view -> {
            Intent signUpIntent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(signUpIntent);
            finish();
        });

        tvSignUp.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String username = etUserName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phoneNumber = "+91-" + etPhoneNumber.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String cnfPassword = etConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || username.isEmpty() || email.isEmpty()
                    || phoneNumber.isEmpty() || password.isEmpty() || cnfPassword.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.enter_all_details, Toast.LENGTH_SHORT).show();
            } else if (!TextUtils.equals(password, cnfPassword)) {
                Toast.makeText(getApplicationContext(), R.string.confirm_password_correctly, Toast.LENGTH_SHORT).show();
            } else {
                loadingStarted();
                signUp(name, username, email, phoneNumber, password);
            }
        });
    }

    public void loadingStarted() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public void loadingFinished() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.loading));
        }
    }

    private void signUp(String name, String username, String email, String phoneNumber, String password) {
        User user = new User(name, username, email, phoneNumber, password);
        Call<ApiCallResponse> signUpCall = Flytekart.getApiService().signUp(user);
        tvSignUp.setEnabled(false);
        signUpCall.enqueue(new Callback<ApiCallResponse>() {
            @Override
            public void onResponse(@NotNull Call<ApiCallResponse> call, @NotNull Response<ApiCallResponse> response) {
                ApiCallResponse apiCallResponse = null;
                Logger.i("SignUp API call response received.");
                loadingFinished();
                if (response.isSuccessful() && response.body() != null) {
                    apiCallResponse = response.body();
                    // Get dropdown data and go to next screen.
                    Toast.makeText(getApplicationContext(), apiCallResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    if (apiCallResponse.isSuccess()) {
                        Logger.i("SignUp API call success.");
                        Intent mainIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                } else if (response.errorBody() != null) {
                    try {
                        apiCallResponse = new Gson().fromJson(
                                response.errorBody().string(), ApiCallResponse.class);
                        Toast.makeText(getApplicationContext(), apiCallResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Logger.e("SingUp API response status code : " + response.code());
            }

            @Override
            public void onFailure(@NotNull Call<ApiCallResponse> call, @NotNull Throwable t) {
                Logger.i("SignUp API call failure.");
                loadingFinished();
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}