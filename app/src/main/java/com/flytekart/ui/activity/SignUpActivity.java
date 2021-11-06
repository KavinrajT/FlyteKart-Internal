package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.R;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputEditText etUserName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhoneNumber;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextView tvSignUp;
    private TextView tvLogin;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.et_name);
        etUserName = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_phone_number);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_cnf_password);
        tvSignUp = findViewById(R.id.tv_sign_up);
        tvLogin = findViewById(R.id.tv_login);

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
                //signUp(name, username, email, phoneNumber, password);
            }
        });
    }

    /*private void signUp(String name, String username, String email, String phoneNumber, String password) {
        User user = new User(name, username, email, phoneNumber, password);
        Call<ApiCallResponse> signUpCall = Flytekart.getApiService().signUp(user);
        tvSignUp.setEnabled(false);
        showProgress(true);
        signUpCall.enqueue(new Callback<ApiCallResponse>() {
            @Override
            public void onResponse(@NotNull Call<ApiCallResponse> call, @NotNull Response<ApiCallResponse> response) {
                ApiCallResponse apiCallResponse = null;
                Logger.i("SignUp API call response received.");
                showProgress(false);
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
            public void onFlytekartGenericErrorResponse(@NotNull Call<ApiCallResponse> call) {
                Logger.i("SignUp API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
*/
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