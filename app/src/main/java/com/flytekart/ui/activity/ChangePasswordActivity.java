package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.R;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.APIError;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chage_password);
    }

    private void changePassword(String oldPassword, String newPassword) {
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, "");
        JsonObject changePasswordJson = new JsonObject();
        changePasswordJson.addProperty("oldPassword", oldPassword);
        changePasswordJson.addProperty("newPassword", newPassword);

        showProgress(true);
        Call<ApiCallResponse> loginCall = com.flytekart.Flytekart.getApiService().changePasswordMainUser(accessToken, changePasswordJson);
        loginCall.enqueue(new CustomCallback<ApiCallResponse>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<ApiCallResponse> call, @NotNull Response<ApiCallResponse> response) {
                Logger.i("Change Password API call response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiCallResponse apiCallResponse = response.body();
                    // Get dropdown data and go to next screen.
                    if (apiCallResponse.isSuccess())
                        Toast.makeText(getApplicationContext(), "Password changes successfully.", Toast.LENGTH_SHORT).show();
                    Logger.i("Change Password API call success.");
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
                Logger.e("Change Password API call response status code : " + response.code());
                // populateFragment();
            }

            @Override
            public void onFlytekartErrorResponse(Call<ApiCallResponse> call, APIError responseBody) {
                Logger.e("Organisation List API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<ApiCallResponse> call) {
                Logger.i("Organisation List API call failure.");
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