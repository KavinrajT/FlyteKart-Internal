package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetails implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("userType")
    @Expose
    private String userType;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("enabled")
    @Expose
    private boolean enabled;
    @SerializedName("accountNonExpired")
    @Expose
    private boolean accountNonExpired;
    @SerializedName("accountNonLocked")
    @Expose
    private boolean accountNonLocked;
    @SerializedName("credentialsNonExpired")
    @Expose
    private boolean credentialsNonExpired;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeString(this.userType);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.email);
        dest.writeByte(this.enabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.accountNonExpired ? (byte) 1 : (byte) 0);
        dest.writeByte(this.accountNonLocked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.credentialsNonExpired ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
        this.username = source.readString();
        this.userType = source.readString();
        this.phoneNumber = source.readString();
        this.email = source.readString();
        this.enabled = source.readByte() != 0;
        this.accountNonExpired = source.readByte() != 0;
        this.accountNonLocked = source.readByte() != 0;
        this.credentialsNonExpired = source.readByte() != 0;
    }

    public UserDetails() {
    }

    protected UserDetails(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.username = in.readString();
        this.userType = in.readString();
        this.phoneNumber = in.readString();
        this.email = in.readString();
        this.enabled = in.readByte() != 0;
        this.accountNonExpired = in.readByte() != 0;
        this.accountNonLocked = in.readByte() != 0;
        this.credentialsNonExpired = in.readByte() != 0;
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel source) {
            return new UserDetails(source);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };
}