package com.flytekart.network;

import com.flytekart.models.Attribute;
import com.flytekart.models.Category;
import com.flytekart.models.CategoryStoreCategoryDTO;
import com.flytekart.models.EmployeePushToken;
import com.flytekart.models.OrderResponse;
import com.flytekart.models.Organisation;
import com.flytekart.models.Product;
import com.flytekart.models.ProductStoreProductDTO;
import com.flytekart.models.Store;
import com.flytekart.models.StoreVariant;
import com.flytekart.models.User;
import com.flytekart.models.Variant;
import com.flytekart.models.VariantAttributeValue;
import com.flytekart.models.VariantStoreVariantDTO;
import com.flytekart.models.request.CODPaymentRequest;
import com.flytekart.models.request.CreateEmployeePushTokenRequest;
import com.flytekart.models.request.CreateProductRequest;
import com.flytekart.models.request.CreateStoreCategoryRequest;
import com.flytekart.models.request.CreateStoreProductRequest;
import com.flytekart.models.request.CreateStoreRequest;
import com.flytekart.models.request.CreateStoreVariantRequest;
import com.flytekart.models.request.CreateVariantRequest;
import com.flytekart.models.request.CreateVariantVavRequest;
import com.flytekart.models.request.DeleteEmployeePushTokenRequest;
import com.flytekart.models.request.DeleteVariantAttributeValueRequest;
import com.flytekart.models.request.LoginRequest;
import com.flytekart.models.request.SendOTPRequest;
import com.flytekart.models.request.UpdateOrderStatusRequest;
import com.flytekart.models.request.VerifyOTPRequest;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.AttributeResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.models.response.LoginResponse;
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

    /*@POST("/api/auth/signup/")
    Call<ApiCallResponse> signUp(
            @Body User user);*/

    @POST("/api/auth/signin/")
    Call<BaseResponse<LoginResponse>> mainLogin(
            @Body LoginRequest loginRequest);

    @POST("/api/auth/employeesignin")
    Call<BaseResponse<LoginResponse>> clientLogin(
            @Query("clientId") String clientId,
            @Body LoginRequest loginRequest);

    @POST("/api/auth/employeeotplogin")
    Call<BaseResponse<String>> sendClientOTP(
            @Query("clientId") String clientId,
            @Body SendOTPRequest request);

    @POST("/api/auth/employeeotpverify")
    Call<BaseResponse<LoginResponse>> verifyClientOTP(
            @Query("clientId") String clientId,
            @Body VerifyOTPRequest request);

    @POST("/api/notifications/saveEmployeePushToken")
    Call<BaseResponse<EmployeePushToken>> saveFCMToken(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body CreateEmployeePushTokenRequest request);

    @POST("/api/notifications/deleteEmployeePushToken")
    Call<BaseResponse<EmployeePushToken>> deleteFCMToken(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body DeleteEmployeePushTokenRequest request);

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
    Call<BaseResponse<Store>> saveStore(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body CreateStoreRequest request);

    @GET("/api/categories/")
    Call<BaseResponse<List<Category>>> getAllCategories(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId);

    @POST("/api/categories/delete/{categoryId}")
    Call<BaseResponse<Category>> deleteCategory(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("categoryId") String categoryId,
            @Query("clientId") String clientId);

    @GET("/api/categories/{id}")
    Call<BaseResponse<Category>> getCategory(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("id") String categoryId,
            @Query("clientId") String clientId);

    @POST("/api/categories/")
    Call<BaseResponse<Category>> saveCategory(
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

    @GET("/api/products/{id}")
    Call<BaseResponse<Product>> getProductById(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("id") String id,
            @Query("clientId") String clientId);

    @POST("/api/products/")
    Call<BaseResponse<Product>> saveProduct(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body CreateProductRequest request);

    @GET("/api/variants/getByProductId/{productId}")
    Call<BaseResponse<List<Variant>>> getVariantsByProductId(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("productId") String productId,
            @Query("clientId") String clientId);

    @GET("/api/variants/{id}")
    Call<BaseResponse<Variant>> getVariantById(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("id") String id,
            @Query("clientId") String clientId);

    @GET("/api/variantAttributeValues/getByVariantId/{variantId}")
    Call<BaseResponse<List<VariantAttributeValue>>> getAttributeValuesByVariantId(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("variantId") String variantId,
            @Query("clientId") String clientId);

    @GET("/api/attributes/")
    Call<BaseResponse<List<Attribute>>> getAttributesByPrefix(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("prefix") String prefix,
            @Query("clientId") String clientId);

    @GET("/api/attributes/")
    Call<BaseResponse<List<Attribute>>> getAllAttributes(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId);

    @GET("/api/attributeValues/")
    Call<BaseResponse<List<AttributeResponse>>> getAllAttributeValues(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId);

    @GET("/api/attributeValues/getByAttributeId/{attributeId}")
    Call<BaseResponse<List<Attribute>>> getAttributeValuesByPrefix(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("attributeId") String attributeId,
            @Query("prefix") String prefix,
            @Query("clientId") String clientId);

    @POST("/api/variants/savevav")
    Call<BaseResponse<Variant>> saveVariantVav(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body CreateVariantVavRequest createVariantVavRequest);

    @POST("/api/variants/savevav")
    Call<BaseResponse<Variant>> saveVariant(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body CreateVariantRequest createVariantRequest);

    @POST("/api/variantAttributeValues/deleteById")
    Call<BaseResponse<VariantAttributeValue>> deleteVariantAttributeValue(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body DeleteVariantAttributeValueRequest request);

    @GET("/api/storeCategories/withAllCategories/store/{storeId}")
    Call<BaseResponse<List<CategoryStoreCategoryDTO>>> getAllCategoriesWithStoreCategories(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("storeId") String storeId,
            @Query("clientId") String clientId);

    @POST("/api/storeCategories/")
    Call<BaseResponse<CategoryStoreCategoryDTO>> saveStoreCategory(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body CreateStoreCategoryRequest request);

    @GET("/api/storeProducts/withAllProducts/store/{storeId}")
    Call<BaseResponse<List<ProductStoreProductDTO>>> getAllProductsWithStoreProducts(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("storeId") String storeId,
            @Query("clientId") String clientId,
            @Query("categoryId") String categoryId);

    @POST("/api/storeProducts/")
    Call<BaseResponse<ProductStoreProductDTO>> saveStoreProduct(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body CreateStoreProductRequest request);

    @GET("/api/storeVariants/withAllVariants/store/{storeId}")
    Call<BaseResponse<List<VariantStoreVariantDTO>>> getAllVariantsWithStoreVariants(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("storeId") String storeId,
            @Query("clientId") String clientId,
            @Query("productId") String productId);

    @GET("/api/storeVariants/getDTO/{id}")
    Call<BaseResponse<VariantStoreVariantDTO>> getStoreVariant(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Path("id") String id,
            @Query("clientId") String clientId);

    @POST("/api/storeVariants/")
    Call<BaseResponse<StoreVariant>> saveStoreVariant(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body CreateStoreVariantRequest request);

    @POST("/api/orders/updateOrderStatusToAccepted")
    Call<BaseResponse<OrderResponse>> acceptOrder(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body UpdateOrderStatusRequest request);

    @POST("/api/orders/updateOrderStatusToProcessing")
    Call<BaseResponse<OrderResponse>> processOrder(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body UpdateOrderStatusRequest request);

    @POST("/api/orders/updateOrderStatusToProcessed")
    Call<BaseResponse<OrderResponse>> processedOrder(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body UpdateOrderStatusRequest request);

    @POST("/api/orders/updateOrderStatusToOutForDelivery")
    Call<BaseResponse<OrderResponse>> outForDeliveryOrder(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body UpdateOrderStatusRequest request);

    @POST("/api/orders/updateOrderStatusToDelivered")
    Call<BaseResponse<OrderResponse>> deliverOrder(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body UpdateOrderStatusRequest request);

    @POST("/api/payments/collectCODPayment")
    Call<BaseResponse<OrderResponse>> collectCODPayment(
            @Header(Constants.API_TOKEN_TAG) String apiToken,
            @Query("clientId") String clientId,
            @Body CODPaymentRequest request);
}

