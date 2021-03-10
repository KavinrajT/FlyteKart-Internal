package com.flytekart.myapplication.network;

import com.flytekart.myapplication.models.ApiCallResponse;
import com.flytekart.myapplication.models.BaseResponse;
import com.flytekart.myapplication.models.Category;
import com.flytekart.myapplication.models.LoginResponse;
import com.flytekart.myapplication.models.Organisation;
import com.flytekart.myapplication.models.Store;
import com.flytekart.myapplication.models.User;
import com.flytekart.myapplication.utils.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/api/auth/signup/")
    Call<ApiCallResponse> signUp(
            @Body User user);

    @POST("/api/auth/signin/")
    Call<LoginResponse> mainLogin(
            @Body JsonObject body);

    @POST("/api/auth/employeesignin")
    Call<LoginResponse> clientLogin(
            @Query("clientId") String clientId,
            @Body JsonObject body);

    @POST("/api/auth/changePassword")
    Call<ApiCallResponse> changePasswordMainUser(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Body JsonObject body);

    @POST("/api/auth/changePassword")
    Call<ApiCallResponse> changePasswordClientUser(
            @Query("clientId") String clientId,
            @Body JsonObject body);

    @GET("/api/organisation/organisationMap/")
    Call<BaseResponse<Organisation>> getAllOrganisations(
            @Header(Constants.API_TOKEN_TAG) String apiToken);

    @GET("/api/organisation/")
    Call<BaseResponse<Organisation>> getOrganisation(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId
    );

    @POST("/api/organisation/")
    Call<BaseResponse<Organisation>> createOrganisation(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Body Organisation organisation);

    @POST("/api/organisation/edit/")
    Call<BaseResponse<Organisation>> editOrganisation(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body Organisation organisation);

    @GET("/api/stores/")
    Call<BaseResponse<Store>> getStoresByOrg(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Query("organisationId") String organisationId);

    @GET("/api/stores/")
    Call<BaseResponse<Store>> getStore(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Query("storeId") String storeId);

    @POST("/api/stores/")
    Call<BaseResponse<Store>> createStore(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body Store store);

    @GET("/api/categories/")
    Call<BaseResponse<Category>> getAllCategories(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId);

    @GET("/api/categories/{id}")
    Call<BaseResponse<Category>> getCategory(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("id") String categoryId,
            @Query("clientId") String clientId);

    @POST("/api/categories/")
    Call<BaseResponse<Category>> createCategory(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body Category category);
}

