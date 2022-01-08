package com.flytekart.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileUploadResponse {
    @SerializedName("statusCode")
    @Expose
    private long statusCode;
    @SerializedName("body")
    @Expose
    private String body;

    public long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(long statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
