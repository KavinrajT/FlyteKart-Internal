package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribute implements Parcelable {
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("lastUpdatedAt")
    @Expose
    private String lastUpdatedAt;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("lastUpdatedBy")
    @Expose
    private String lastUpdatedBy;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.createdAt);
        dest.writeString(this.lastUpdatedAt);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.createdBy);
        dest.writeString(this.lastUpdatedBy);
    }

    public void readFromParcel(Parcel source) {
        this.createdAt = source.readString();
        this.lastUpdatedAt = source.readString();
        this.id = source.readString();
        this.name = source.readString();
        this.createdBy = source.readString();
        this.lastUpdatedBy = source.readString();
    }

    public Attribute() {
    }

    protected Attribute(Parcel in) {
        this.createdAt = in.readString();
        this.lastUpdatedAt = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.createdBy = in.readString();
        this.lastUpdatedBy = in.readString();
    }

    public static final Parcelable.Creator<Attribute> CREATOR = new Parcelable.Creator<Attribute>() {
        @Override
        public Attribute createFromParcel(Parcel source) {
            return new Attribute(source);
        }

        @Override
        public Attribute[] newArray(int size) {
            return new Attribute[size];
        }
    };
}