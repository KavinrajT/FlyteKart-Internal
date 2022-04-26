package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.request.LoginRequest;
import com.flytekart.models.request.SendOTPRequest;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.models.response.LoginResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etClientCode;
    private TextInputEditText etPhoneNumber;
    private TextInputEditText etPassword;
    private Spinner spLoginType;
    private TextView tvSignUp;
    private TextView tvLogin;
    private TextInputLayout tilClientCode;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;

    private String loginType;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spLoginType = findViewById(R.id.sp_login_type);
        etClientCode = findViewById(R.id.et_client_code);
        tilClientCode = findViewById(R.id.til_client_code);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        tvSignUp = findViewById(R.id.tv_sign_up);
        tvLogin = findViewById(R.id.tv_login);

        sharedPreferences = Utilities.getSharedPreferences();
        String clientIdSP = sharedPreferences.getString(Constants.CLIENT_ID, Constants.EMPTY);
        String usernameSP = sharedPreferences.getString(Constants.USERNAME, Constants.EMPTY);
        etClientCode.setText(clientIdSP);
        etPhoneNumber.setText(usernameSP);

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
        loginType = Constants.LOGIN_TYPE_CLIENT_ACCOUNT;
        // tilClientCode.setVisibility(View.GONE);

        tvSignUp.setOnClickListener(view -> {
            Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);
            finish();
        });

        etPhoneNumber.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etPhoneNumber.getWindowToken(), 0);
                checkInputAndRequestOTP();
            }
            return true;
        });
        tvLogin.setOnClickListener(view -> {
            checkInputAndRequestOTP();
        });
    }

    private void checkInputAndRequestOTP() {
        String usernameOrEmail = etPhoneNumber.getText().toString().trim();
        String clientId = etClientCode.getText().toString().trim();

        if (loginType == null) {
            Toast.makeText(getApplicationContext(), R.string.enter_all_details, Toast.LENGTH_SHORT).show();
        } /*else if (TextUtils.equals(loginType, Constants.LOGIN_TYPE_MAIN_ACCOUNT)) {
                if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.enter_all_details, Toast.LENGTH_SHORT).show();
                } else {
                    mainLogin(usernameOrEmail, password);
                }
            }*/ else if (TextUtils.equals(loginType, Constants.LOGIN_TYPE_CLIENT_ACCOUNT)) {
            if (usernameOrEmail.isEmpty() || clientId.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.enter_all_details, Toast.LENGTH_SHORT).show();
            } else {
                employeeLogin(clientId, usernameOrEmail);
            }
        }
    }

    private void mainLogin(String usernameOrEmail, String password) {
        /*JsonObject loginJson = new JsonObject();
        loginJson.addProperty("usernameOrEmail", usernameOrEmail);
        loginJson.addProperty("password", password);*/
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(usernameOrEmail);
        loginRequest.setPassword(password);
        Call<BaseResponse<LoginResponse>> loginCall = Flytekart.getApiService().mainLogin(loginRequest);
        tvSignUp.setEnabled(false);
        showProgress(true);
        loginCall.enqueue(new CustomCallback<BaseResponse<LoginResponse>>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<BaseResponse<LoginResponse>> call, @NotNull Response<BaseResponse<LoginResponse>> response) {
                Logger.i("Main Login API call response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body().getBody();
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
                }
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<LoginResponse>> call, APIError responseBody) {
                /*if (response.errorBody() != null) {
                    try {
                        ApiCallResponse apiCallResponse = new Gson().fromJson(
                                response.errorBody().string(), ApiCallResponse.class);
                        Toast.makeText(getApplicationContext(), apiCallResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
                Logger.e("Main Login API response failed");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<LoginResponse>> call) {
                Logger.i("Main Login API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void employeeLogin(String clientId, String usernameOrEmail) {
        SendOTPRequest request = new SendOTPRequest();
        request.setPhoneNumber(usernameOrEmail);
        showProgress(true);
        Call<BaseResponse<String>> loginCall = Flytekart.getApiService().sendClientOTP(clientId, request);
        loginCall.enqueue(new CustomCallback<BaseResponse<String>>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<BaseResponse<String>> call, @NotNull Response<BaseResponse<String>> response) {
                Logger.i("Employee Login API call response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().getBody();
                    // Get dropdown data and go to next screen.
                    Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putString(Constants.CLIENT_ID, clientId).apply();
                    sharedPreferences.edit().putString(Constants.USERNAME, usernameOrEmail).apply();
                    Logger.i("Employee send OTP API call success.");
                    Intent otpVerificationIntent = new Intent(LoginActivity.this,
                            OTPVerificationActivity.class);
                    otpVerificationIntent.putExtra(Constants.CLIENT_ID, clientId);
                    otpVerificationIntent.putExtra(Constants.USERNAME, usernameOrEmail);
                    startActivity(otpVerificationIntent);
                }
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<String>> call, APIError responseBody) {
                /*if (response.errorBody() != null) {
                    try {
                        ApiCallResponse apiCallResponse = new Gson().fromJson(
                                response.errorBody().string(), ApiCallResponse.class);
                        Toast.makeText(getApplicationContext(), apiCallResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
                Logger.e("Employee Login API response failed");
                showProgress(false);
                if (responseBody != null && responseBody.getMessage() != null) {
                    Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<String>> call) {
                Logger.i("Employee Login API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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