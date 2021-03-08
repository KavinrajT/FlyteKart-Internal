package com.flytekart.myapplication.network;

import com.flytekart.myapplication.models.ApiCallResponse;
import com.flytekart.myapplication.models.LoginResponse;
import com.flytekart.myapplication.models.Organisation;
import com.flytekart.myapplication.models.OrganisationResponse;
import com.flytekart.myapplication.models.User;
import com.flytekart.myapplication.utils.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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

    @POST("/api/organisation/")
    Call<ApiCallResponse> createOrganisation(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Body Organisation organisation);

    @GET("/api/organisation/organisationMap/")
    Call<OrganisationResponse> getAllOrganisation(
            @Header(Constants.API_TOKEN_TAG) String apiToken);

}

