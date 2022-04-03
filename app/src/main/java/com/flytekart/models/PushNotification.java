package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushNotification implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("body")
    @Expose
    private String body;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.body);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.title = source.readString();
        this.body = source.readString();
    }

    public PushNotification() {
    }

    protected PushNotification(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.body = in.readString();
    }

    public static final Creator<PushNotification> CREATOR = new Creator<PushNotification>() {
        @Override
        public PushNotification createFromParcel(Parcel source) {
            return new PushNotification(source);
        }

        @Override
        public PushNotification[] newArray(int size) {
            return new PushNotification[size];
        }
    };
}
