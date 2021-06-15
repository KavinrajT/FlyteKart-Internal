package com.flytekart;

import android.app.Application;

import com.flytekart.network.ApiService;
import com.flytekart.network.RetrofitClient;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class Flytekart extends Application {

    private static Flytekart instance;
    public static RetrofitClient retrofitClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // retrofitClient = new RetrofitClient(getApplicationContext());

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));

        Picasso picasso = builder.build();
        picasso.setIndicatorsEnabled(true);
        picasso.setLoggingEnabled(true);
        Picasso.setSingletonInstance(picasso);
    }

    public static Flytekart getInstance() {
        return instance;
    }

    public static ApiService getApiService() {
        if (retrofitClient == null) {
            retrofitClient = new RetrofitClient(instance.getApplicationContext());
        }
        return retrofitClient.getApiService();
    }
}
