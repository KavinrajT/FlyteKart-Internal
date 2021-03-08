package com.flytekart.myapplication.utils;

import android.util.Log;

import com.flytekart.myapplication.BuildConfig;
import com.flytekart.myapplication.MyApplication;


/**
 * Custom logging to print logs only in debug builds.
 * Created by narendranathp on 04/05/17.
 */
public class Logger {
    public static void v(String message) {
        if (BuildConfig.DEBUG) {
            Log.v(MyApplication.class.getSimpleName(), message);
        }
    }

    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(MyApplication.class.getSimpleName(), message);
        }
    }

    public static void i(String message) {
        if (BuildConfig.DEBUG) {
            Log.i(MyApplication.class.getSimpleName(), message);
        }
    }

    public static void w(String message) {
        if (BuildConfig.DEBUG) {
            Log.w(MyApplication.class.getSimpleName(), message);
        }
    }

    public static void e(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(MyApplication.class.getSimpleName(), message);
        }
    }
}
