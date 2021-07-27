package com.flytekart.network;

import com.flytekart.models.OrderResponse;
import com.flytekart.models.Product;
import com.flytekart.models.request.LoginRequest;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.models.Category;
import com.flytekart.models.response.LoginResponse;
import com.flytekart.models.Organisation;
import com.flytekart.models.Store;
import com.flytekart.models.User;
import com.flytekart.utils.Constants;
import com.google.gson.JsonObject;

import java.util.List;

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
            @Body LoginRequest loginRequest);

    @POST("/api/auth/employeesignin")
    Call<LoginResponse> clientLogin(
            @Query("clientId") String clientId,
            @Body LoginRequest loginRequest);

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
    Call<BaseResponse<List<Store>>> getStoresByOrg(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId);

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
    Call<BaseResponse<List<Category>>> getAllCategories(
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

    @GET("/api/products/")
    Call<BaseResponse<List<Product>>> getProductsByCategoryId(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Query("categoryId") String categoryId);

    @GET("/api/orders/getByStoreId/{storeId}")
    Call<BaseResponse<List<OrderResponse>>> getOrdersByStoreId(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("storeId") String storeId,
            @Query("clientId") String clientId,
            @Query("pageNumber") int pageNumber,
            @Query("pageSize") int pageSize);

    @GET("/api/orders/getByUserId/{userId}")
    Call<BaseResponse<List<OrderResponse>>> getOrdersByUserId(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("userId") String userId,
            @Query("clientId") String clientId,
            @Query("pageNumber") int pageNumber,
            @Query("pageSize") int pageSize);

    @GET("/api/orders/get/{orderId}")
    Call<BaseResponse<OrderResponse>> getOrderById(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("orderId") String orderId,
            @Query("clientId") String clientId);
}

