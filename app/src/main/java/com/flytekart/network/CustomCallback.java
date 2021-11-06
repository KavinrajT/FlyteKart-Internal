package com.flytekart.network;

import com.flytekart.models.response.APIError;
import com.flytekart.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

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
                String errorBodyString = response.errorBody().string();
                // 1. Content type can be different. 2. Server might send a generic error in json itself
                /*BaseErrorResponse errorResponse = new Gson().fromJson(
                        s, BaseErrorResponse.class);*/
                APIError apiError = new Gson().fromJson(
                        errorBodyString, APIError.class);
                if (apiError != null) {
                    onFlytekartErrorResponse(call, apiError);
                } else {
                    onFlytekartGenericErrorResponse(call);
                }
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                onFlytekartGenericErrorResponse(call);
            }

        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Logger.e(t.getLocalizedMessage());
        onFlytekartGenericErrorResponse(call);
    }

    public abstract void onFlytekartSuccessResponse(Call<T> call, Response<T> response);
    public abstract void onFlytekartErrorResponse(Call<T> call, APIError responseBody);
    public abstract void onFlytekartGenericErrorResponse(Call<T> call);

}
