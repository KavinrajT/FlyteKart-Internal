package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.UserDetails;
import com.flytekart.models.request.LoginRequest;
import com.flytekart.models.request.VerifyOTPRequest;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.models.response.LoginResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.SmsBroadcastReceiver;
import com.flytekart.utils.Utilities;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

public class OTPVerificationActivity extends AppCompatActivity {

    private TextInputEditText etClientCode;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Spinner spLoginType;
    private TextView tvSignUp;
    private TextView tvLogin;
    private TextInputLayout tilClientCode;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private final int REQ_USER_CONSENT = 1000;

    String loginType;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        spLoginType = findViewById(R.id.sp_login_type);
        etClientCode = findViewById(R.id.et_client_code);
        tilClientCode = findViewById(R.id.til_client_code);
        etEmail = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        tvSignUp = findViewById(R.id.tv_sign_up);
        tvLogin = findViewById(R.id.tv_login);
        String clientIdExtra = getIntent().getStringExtra(Constants.CLIENT_ID);
        String usernameExtra = getIntent().getStringExtra(Constants.USERNAME);
        etClientCode.setText(clientIdExtra);
        etEmail.setText(usernameExtra);
        sharedPreferences = Utilities.getSharedPreferences();

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
            Intent signUpIntent = new Intent(OTPVerificationActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);
            finish();
        });

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
                checkInputAndVerifyOTP();
            }
            return true;
        });

        tvLogin.setOnClickListener(view -> {
            checkInputAndVerifyOTP();
        });
        startSmsUserConsent();
    }

    private void checkInputAndVerifyOTP() {
        String usernameOrEmail = etEmail.getText().toString().trim();
        String clientId = etClientCode.getText().toString().trim();
        String otp = etPassword.getText().toString().trim();

        if (loginType == null || otp.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.enter_all_details, Toast.LENGTH_SHORT).show();
        } /*else if (TextUtils.equals(loginType, Constants.LOGIN_TYPE_MAIN_ACCOUNT)) {
                if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.enter_all_details, Toast.LENGTH_SHORT).show();
                } else {
                    mainLogin(usernameOrEmail, password);
                }
            }*/ else if (TextUtils.equals(loginType, Constants.LOGIN_TYPE_CLIENT_ACCOUNT)) {
            if (usernameOrEmail.isEmpty() || clientId.isEmpty() || otp.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.enter_all_details, Toast.LENGTH_SHORT).show();
            } else {
                employeeLogin(clientId, usernameOrEmail, otp);
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
                    Intent mainIntent = new Intent(OTPVerificationActivity.this, MainHomeActivity.class);
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
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void employeeLogin(String clientId, String usernameOrEmail, String otp) {
        VerifyOTPRequest request = new VerifyOTPRequest();
        request.setPhoneNumber(usernameOrEmail);
        request.setOtp(otp);
        showProgress(true);
        Call<BaseResponse<LoginResponse>> loginCall = Flytekart.getApiService().verifyClientOTP(clientId, request);
        loginCall.enqueue(new CustomCallback<BaseResponse<LoginResponse>>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<BaseResponse<LoginResponse>> call, @NotNull Response<BaseResponse<LoginResponse>> response) {
                Logger.i("Employee Login API call response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body().getBody();
                    // Get dropdown data and go to next screen.
                    Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constants.SHARED_PREF_KEY_IS_MAIN_ACCOUNT_LOGGED_IN, false);
                    editor.putString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, loginResponse.getTokenType() + " " + loginResponse.getAccessToken());
                    editor.putString(Constants.SHARED_PREF_KEY_CLIENT_ID, clientId);
                    editor.apply();
                    saveUserDetails(loginResponse.getUserDetails());
                    Logger.i("Employee Login API call success.");
                    Intent mainIntent = new Intent(OTPVerificationActivity.this, HomeActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
                Logger.e("Employee Login API response failed");
                showProgress(false);
                if (responseBody != null && responseBody.getMessage() != null) {
                    Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<LoginResponse>> call) {
                Logger.i("Employee Login API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDetails(UserDetails userDetails) {
        Gson gson = new Gson();
        String json = gson.toJson(userDetails);
        sharedPreferences.edit().putString(Constants.SHARED_PREF_KEY_USER_DETAILS, json).apply();
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

    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Logger.d("SmsRetrieverClient On Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Logger.d("SmsRetrieverClient On Failure");
                e.printStackTrace();
            }
        });
    }

    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, REQ_USER_CONSENT);
                    }

                    @Override
                    public void onFailure() {
                        Logger.d("SmsBroadcastReceiver on Failure");
                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }
    }

    private void getOtpFromMessage(String message) {
        // This will match any 6 digit number in the message
        Pattern pattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            etPassword.setText(matcher.group(0));
            checkInputAndVerifyOTP();
        }
    }
}