package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Organisation implements Parcelable {

    private String name;
    private String storeType;
    private String address;
    private String businessType;
    private String gstNo;

    public Organisation() {
    }

    protected Organisation(Parcel in) {
        name = in.readString();
        storeType = in.readString();
        address = in.readString();
        businessType = in.readString();
        gstNo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(storeType);
        dest.writeString(address);
        dest.writeString(businessType);
        dest.writeString(gstNo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Organisation> CREATOR = new Creator<Organisation>() {
        @Override
        public Organisation createFromParcel(Parcel in) {
            return new Organisation(in);
        }

        @Override
        public Organisation[] newArray(int size) {
            return new Organisation[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getGstNo() {
        return gstNo;
    }

    public void setGstNo(String gstNo) {
        this.gstNo = gstNo;
    }
}
