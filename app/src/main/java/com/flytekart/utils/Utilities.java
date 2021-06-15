package com.flytekart.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Utilities {
    public static SharedPreferences getSharedPreferences() {
        return com.flytekart.Flytekart.getInstance().getApplicationContext().getSharedPreferences(
                com.flytekart.Flytekart.class.getSimpleName(), Context.MODE_PRIVATE);
    }
}
