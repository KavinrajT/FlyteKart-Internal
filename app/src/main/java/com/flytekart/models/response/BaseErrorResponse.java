package com.flytekart.models.response;

import com.google.gson.annotations.SerializedName;

public class BaseErrorResponse {

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("apiError")
    private APIError apiError;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public APIError getApiError() {
        return apiError;
    }

    public void setApiError(APIError apiError) {
        this.apiError = apiError;
    }
}
