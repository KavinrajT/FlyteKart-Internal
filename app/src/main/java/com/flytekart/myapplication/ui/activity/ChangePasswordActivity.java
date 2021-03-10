package com.flytekart.myapplication.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.myapplication.MyApplication;
import com.flytekart.myapplication.R;
import com.flytekart.myapplication.models.ApiCallResponse;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Logger;
import com.flytekart.myapplication.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {


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

        Call<ApiCallResponse> loginCall = MyApplication.getApiService().changePasswordMainUser(accessToken, changePasswordJson);
        loginCall.enqueue(new Callback<ApiCallResponse>() {
            @Override
            public void onResponse(@NotNull Call<ApiCallResponse> call, @NotNull Response<ApiCallResponse> response) {
                Logger.i("Change Password API call response received.");
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
            public void onFailure(@NotNull Call<ApiCallResponse> call, @NotNull Throwable t) {
                Logger.i("Organisation List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}