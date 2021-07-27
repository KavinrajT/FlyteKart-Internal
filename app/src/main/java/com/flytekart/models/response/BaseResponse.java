package com.flytekart.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseResponse<T> {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("body")
    private T body;

    @SerializedName("apiError")
    private APIError apiError;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public APIError getApiError() {
        return apiError;
    }

    public void setApiError(APIError apiError) {
        this.apiError = apiError;
    }
}
