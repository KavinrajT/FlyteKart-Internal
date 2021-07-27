package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderSource implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
    }

    public OrderSource() {
    }

    protected OrderSource(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Creator<OrderSource> CREATOR = new Creator<OrderSource>() {
        @Override
        public OrderSource createFromParcel(Parcel source) {
            return new OrderSource(source);
        }

        @Override
        public OrderSource[] newArray(int size) {
            return new OrderSource[size];
        }
    };
}
