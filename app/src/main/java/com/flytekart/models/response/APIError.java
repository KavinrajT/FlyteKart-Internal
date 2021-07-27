package com.flytekart.models.response;

import com.google.gson.annotations.SerializedName;

public class APIError {

    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("message")
    private String message;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
