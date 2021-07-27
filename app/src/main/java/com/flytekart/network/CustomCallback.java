package com.flytekart.network;

import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.BaseErrorResponse;
import com.flytekart.models.response.BaseResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class CustomCallback<T> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.errorBody() == null) {
            onFlytekartSuccessResponse(call, response);
        } else {
            try {
                BaseErrorResponse errorResponse = new Gson().fromJson(
                        response.errorBody().string(), BaseErrorResponse.class);
                onFlytekartErrorResponse(call, errorResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public abstract void onFlytekartSuccessResponse(Call<T> call, Response<T> response);
    public abstract void onFlytekartErrorResponse(Call<T> call, BaseErrorResponse responseBody);

}
