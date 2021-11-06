package com.flytekart.utils;

import android.util.Log;

import com.flytekart.BuildConfig;


/**
 * Custom logging to print logs only in debug builds.
 * Created by narendranathp on 04/05/17.
 */
public class Logger {
    public static void v(String message) {
        if (BuildConfig.DEBUG) {
            Log.v(com.flytekart.Flytekart.class.getSimpleName(), message);
        }
    }

    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(com.flytekart.Flytekart.class.getSimpleName(), message);
        }
    }

    public static void i(String message) {
        if (BuildConfig.DEBUG) {
            Log.i(com.flytekart.Flytekart.class.getSimpleName(), message);
        }
    }

    public static void w(String message) {
        if (BuildConfig.DEBUG) {
            Log.w(com.flytekart.Flytekart.class.getSimpleName(), message);
        }
    }

    public static void e(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(com.flytekart.Flytekart.class.getSimpleName(), message);
        }
    }
}
