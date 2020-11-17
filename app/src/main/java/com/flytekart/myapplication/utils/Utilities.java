package com.flytekart.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.flytekart.myapplication.MyApplication;

public class Utilities {
    public static SharedPreferences getSharedPreferences() {
        return MyApplication.getInstance().getApplicationContext().getSharedPreferences(
                MyApplication.class.getSimpleName(), Context.MODE_PRIVATE);
    }
}
