package com.flytekart.network;

import com.flytekart.BuildConfig;
import com.flytekart.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private ApiService apiService;

    private final Gson gson;

    public RetrofitClient() {
        GsonBuilder builder = new GsonBuilder().setLenient();
        gson = builder.create();
        setService();
    }

    private void setService() {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.connectTimeout(Constants.TIMEOUT, TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(Constants.TIMEOUT, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(Constants.TIMEOUT, TimeUnit.SECONDS);

        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);
        OkHttpClient okHttpClient = okHttpBuilder.build();

        Retrofit apiRetrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .client(okHttpClient)
                .build();

        apiService = apiRetrofit.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }
}
