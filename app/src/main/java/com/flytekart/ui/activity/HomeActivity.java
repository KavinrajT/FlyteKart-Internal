package com.flytekart.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.CustomerAcquisitionReportItem;
import com.flytekart.models.EmployeePushToken;
import com.flytekart.models.MenuModel;
import com.flytekart.models.OrdersOverTimeReportItem;
import com.flytekart.models.Organisation;
import com.flytekart.models.ProductOrderReportItem;
import com.flytekart.models.UserDetails;
import com.flytekart.models.request.DeleteEmployeePushTokenRequest;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.MenuExpandableListAdapter;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    ActionBarDrawerToggle drawerToggle;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private String clientId;
    private UserDetails userDetails;

    private TextView tvTotalOrdersValue;
    private TextView tvTotalOrders;
    private TextView tvTodayAcquisition;
    private TextView tvProductName;
    private TextView tvVariantName;
    private TextView tvCategoryName;
    private ProgressBar pgTotalOrders;
    private ProgressBar pgTodayAcquisition;
    private ProgressBar pgMostSoldToday;
    private ImageView ivMostSoldTodayRefresh;
    private ImageView ivMostSoldTodayOpen;
    private ImageView ivTotalOrderRefresh;
    private ImageView ivTotalOrderOpen;
    private ImageView ivTodayAcquisitionRefresh;
    private ImageView ivTodayAcquisitionOpen;

    private ActivityResultLauncher<Intent> storesActivityResultLauncher;
    private ActivityResultLauncher<Intent> categoriesActivityResultLauncher;

    private Organisation organisation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTitle = mDrawerTitle = getTitle();
        // mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);

        setupToolbar();
        prepareMenuData();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        getUserDetails();
        setUsernameToUi();

        expandableListView = findViewById(R.id.left_drawer);
        expandableListAdapter = new MenuExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnItemClickListener(new DrawerItemClickListener());
        expandableListView.setOnGroupClickListener(new GroupClickListener());
        tvTotalOrdersValue = findViewById(R.id.tv_total_orders_value);
        tvTotalOrders = findViewById(R.id.tv_total_orders);
        tvTodayAcquisition = findViewById(R.id.tv_today_acquisition);
        tvProductName = findViewById(R.id.tv_product_name);
        tvVariantName = findViewById(R.id.tv_variant_name);
        tvCategoryName = findViewById(R.id.tv_category_name);
        pgTotalOrders = findViewById(R.id.pg_total_orders);
        pgTodayAcquisition = findViewById(R.id.pg_today_acquisition);
        pgMostSoldToday = findViewById(R.id.pg_most_sold_today);
        ivMostSoldTodayRefresh = findViewById(R.id.iv_most_sold_today_refresh);
        ivMostSoldTodayOpen = findViewById(R.id.iv_most_sold_today_open);
        ivMostSoldTodayRefresh.setOnClickListener(this);
        ivMostSoldTodayOpen.setOnClickListener(this);
        ivTotalOrderRefresh = findViewById(R.id.iv_total_order_refresh);
        ivTotalOrderOpen = findViewById(R.id.iv_total_order_open);
        ivTotalOrderRefresh.setOnClickListener(this);
        ivTotalOrderOpen.setOnClickListener(this);
        ivTodayAcquisitionRefresh = findViewById(R.id.iv_today_acquisition_refresh);
        ivTodayAcquisitionOpen = findViewById(R.id.iv_today_acquisition_open);
        ivTodayAcquisitionRefresh.setOnClickListener(this);
        ivTodayAcquisitionOpen.setOnClickListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(drawerToggle);
        setupDrawerToggle();
        registerForActivityResults();

        //getAllOrganisations();
        getReports();
    }

    private void getReports() {
        getOrdersOverTimeReportForToday();
        getCustomerAcquisitionReportData();
        getProductOrderReportData();
    }

    private void getOrdersOverTimeReportForToday() {
        pgTotalOrders.setVisibility(View.VISIBLE);
        tvTotalOrders.setVisibility(View.GONE);
        tvTotalOrdersValue.setVisibility(View.GONE);
        ivTotalOrderRefresh.setOnClickListener(null);
        ivTotalOrderOpen.setOnClickListener(null);
        String todayDate = Utilities.getFormattedTodayCalendarString(Constants.DATE_FORMAT_YYYY_MM_DD);
        Call<BaseResponse<List<OrdersOverTimeReportItem>>> getOrdersOverTimeReportCall = Flytekart.getApiService()
                .getOrdersOverTimeReport(accessToken, clientId, null, todayDate, todayDate,
                        0, 1);
        getOrdersOverTimeReportCall.enqueue(new CustomCallback<BaseResponse<List<OrdersOverTimeReportItem>>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<OrdersOverTimeReportItem>>> call, Response<BaseResponse<List<OrdersOverTimeReportItem>>> response) {
                Logger.i("ProductOrderReportItem response received.");
                pgTotalOrders.setVisibility(View.GONE);
                ivTotalOrderRefresh.setOnClickListener(HomeActivity.this);
                ivTotalOrderOpen.setOnClickListener(HomeActivity.this);
                if (response.isSuccessful() && response.body() != null) {
                    List<OrdersOverTimeReportItem> ordersOverTimeReportItems = response.body().getBody();
                    tvTotalOrders.setVisibility(View.VISIBLE);
                    tvTotalOrdersValue.setVisibility(View.VISIBLE);
                    setOrdersOverTimeReportData(ordersOverTimeReportItems);
                }
                Logger.e("ProductOrderReportItem API call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<OrdersOverTimeReportItem>>> call, APIError responseBody) {
                Logger.e("ProductOrderReportItem API call failed.");
                pgTotalOrders.setVisibility(View.GONE);
                ivTotalOrderRefresh.setOnClickListener(HomeActivity.this);
                ivTotalOrderOpen.setOnClickListener(HomeActivity.this);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<List<OrdersOverTimeReportItem>>> call) {
                Logger.i("ProductOrderReportItem call failure.");
                pgTotalOrders.setVisibility(View.GONE);
                ivTotalOrderRefresh.setOnClickListener(null);
                ivTotalOrderOpen.setOnClickListener(null);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOrdersOverTimeReportData(List<OrdersOverTimeReportItem> ordersOverTimeReportItems) {
        if (ordersOverTimeReportItems != null && ordersOverTimeReportItems.size() > 0) {
            // TODO Follow different formatting to support large money numbers
            tvTotalOrdersValue.setText(Utilities.getFormattedMoney(ordersOverTimeReportItems.get(0).getTotalOrderedValue()));
            tvTotalOrders.setText(String.valueOf(ordersOverTimeReportItems.get(0).getPlacedOrders()));
        } else {
            // Show 0 value
            tvTotalOrdersValue.setText(Utilities.getFormattedMoney(0));
            tvTotalOrders.setText(String.valueOf(0));
        }
    }

    private void getCustomerAcquisitionReportData() {
        pgTodayAcquisition.setVisibility(View.VISIBLE);
        tvTodayAcquisition.setVisibility(View.GONE);
        ivTodayAcquisitionRefresh.setOnClickListener(null);
        ivTodayAcquisitionOpen.setOnClickListener(null);
        String todayDate = Utilities.getFormattedTodayCalendarString(Constants.DATE_FORMAT_YYYY_MM_DD);
        Call<BaseResponse<List<CustomerAcquisitionReportItem>>> getCustomerAcquisitionReportCall = Flytekart.getApiService()
                .getCustomerAcquisitionReport(accessToken, clientId, todayDate, todayDate, 0, 1);
        getCustomerAcquisitionReportCall.enqueue(new CustomCallback<BaseResponse<List<CustomerAcquisitionReportItem>>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<CustomerAcquisitionReportItem>>> call, Response<BaseResponse<List<CustomerAcquisitionReportItem>>> response) {
                Logger.i("CustomerAcquisitionReport response received.");
                pgTodayAcquisition.setVisibility(View.GONE);
                ivTodayAcquisitionRefresh.setOnClickListener(HomeActivity.this);
                ivTodayAcquisitionOpen.setOnClickListener(HomeActivity.this);
                if (response.isSuccessful() && response.body() != null) {
                    List<CustomerAcquisitionReportItem> customerAcquisitionReportItems = response.body().getBody();
                    tvTodayAcquisition.setVisibility(View.VISIBLE);
                    setCustomerAcquisitionReportData(customerAcquisitionReportItems);
                }
                Logger.e("CustomerAcquisitionReport API call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<CustomerAcquisitionReportItem>>> call, APIError responseBody) {
                Logger.e("CustomerAcquisitionReport API call failed.");
                pgTodayAcquisition.setVisibility(View.GONE);
                ivTodayAcquisitionRefresh.setOnClickListener(HomeActivity.this);
                ivTodayAcquisitionOpen.setOnClickListener(HomeActivity.this);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<List<CustomerAcquisitionReportItem>>> call) {
                Logger.i("CustomerAcquisitionReport call failure.");
                pgTodayAcquisition.setVisibility(View.GONE);
                ivTodayAcquisitionRefresh.setOnClickListener(HomeActivity.this);
                ivTodayAcquisitionOpen.setOnClickListener(HomeActivity.this);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCustomerAcquisitionReportData(List<CustomerAcquisitionReportItem> customerAcquisitionReportItems) {
        if (customerAcquisitionReportItems != null && customerAcquisitionReportItems.size() > 0) {
            // TODO Follow different formatting to support large money numbers
            tvTodayAcquisition.setText(String.valueOf(customerAcquisitionReportItems.get(0).getTotal()));
        } else {
            // Show 0 value
            tvTodayAcquisition.setText(String.valueOf(0));
        }
    }

    private void getProductOrderReportData() {
        pgMostSoldToday.setVisibility(View.VISIBLE);
        tvProductName.setVisibility(View.GONE);
        tvVariantName.setVisibility(View.GONE);
        tvCategoryName.setVisibility(View.GONE);
        ivMostSoldTodayRefresh.setOnClickListener(null);
        ivMostSoldTodayOpen.setOnClickListener(null);
        String todayDate = Utilities.getFormattedTodayCalendarString(Constants.DATE_FORMAT_YYYY_MM_DD);
        Call<BaseResponse<List<ProductOrderReportItem>>> getProductOrderReportCall = Flytekart.getApiService()
                .getProductOrderReport(accessToken, clientId, null, todayDate, todayDate,
                        0, 1);
        getProductOrderReportCall.enqueue(new CustomCallback<BaseResponse<List<ProductOrderReportItem>>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<ProductOrderReportItem>>> call, Response<BaseResponse<List<ProductOrderReportItem>>> response) {
                Logger.i("ProductOrderReport response received.");
                pgMostSoldToday.setVisibility(View.GONE);
                ivMostSoldTodayRefresh.setOnClickListener(HomeActivity.this);
                ivMostSoldTodayOpen.setOnClickListener(HomeActivity.this);
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductOrderReportItem> productOrderReportItems = response.body().getBody();
                    tvProductName.setVisibility(View.VISIBLE);
                    tvVariantName.setVisibility(View.VISIBLE);
                    tvCategoryName.setVisibility(View.VISIBLE);
                    setProductOrderReportData(productOrderReportItems);
                }
                Logger.e("ProductOrderReport API call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<ProductOrderReportItem>>> call, APIError responseBody) {
                Logger.e("ProductOrderReport API call failed.");
                pgMostSoldToday.setVisibility(View.GONE);
                ivMostSoldTodayRefresh.setOnClickListener(HomeActivity.this);
                ivMostSoldTodayOpen.setOnClickListener(HomeActivity.this);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<List<ProductOrderReportItem>>> call) {
                Logger.i("ProductOrderReport call failure.");
                pgMostSoldToday.setVisibility(View.GONE);
                ivMostSoldTodayRefresh.setOnClickListener(HomeActivity.this);
                ivMostSoldTodayOpen.setOnClickListener(HomeActivity.this);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setProductOrderReportData(List<ProductOrderReportItem> productOrderReportItems) {
        if (productOrderReportItems != null && productOrderReportItems.size() > 0) {
            tvProductName.setText(productOrderReportItems.get(0).getProductName());
            tvVariantName.setText(productOrderReportItems.get(0).getVariantName());
            tvCategoryName.setText(productOrderReportItems.get(0).getCategoryName());
        } else {
            // Show 0 value
            tvProductName.setText(Constants.HYPHEN);
            tvVariantName.setText(Constants.HYPHEN);
            tvCategoryName.setText(Constants.HYPHEN);
        }
    }

    private void getUserDetails() {
        Gson gson = new Gson();
        String userDetailsString = sharedPreferences.getString(Constants.SHARED_PREF_KEY_USER_DETAILS, null);
        if (userDetailsString != null) {
            this.userDetails = gson.fromJson(userDetailsString, UserDetails.class);
        }
    }

    private void setUsernameToUi() {
        TextView tvUsername = findViewById(R.id.nav_header_textView);
        if (!TextUtils.isEmpty(userDetails.getName())) {
            tvUsername.setText(userDetails.getName());
        } else if (!TextUtils.isEmpty(userDetails.getPhoneNumber())) {
            tvUsername.setText(userDetails.getPhoneNumber());
        } else if (!TextUtils.isEmpty(userDetails.getEmail())) {
            tvUsername.setText(userDetails.getEmail());
        }
    }

    private void prepareMenuData() {
        headerList.add(new MenuModel(getString(R.string.stores), true, true, null));
        headerList.add(new MenuModel(getString(R.string.categories_products), true, true, null));
        headerList.add(new MenuModel(getString(R.string.orderResponses), true, true, null));
        headerList.add(new MenuModel(getString(R.string.reports), true, true, null));
        //headerList.add(new MenuModel(getString(R.string.change_password), true, true, null));
        headerList.add(new MenuModel(getString(R.string.sign_out), true, true, null));
    }

    private void registerForActivityResults() {
        storesActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            // TODO Do nothing for now
                        }
                    }
                });

        categoriesActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            // TODO Do nothing for now
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_total_order_refresh: {
                getOrdersOverTimeReportForToday();
                break;
            }
            case R.id.iv_total_order_open: {
                Intent intent = new Intent(HomeActivity.this, OrdersOverTimeReportActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.iv_today_acquisition_refresh: {
                getCustomerAcquisitionReportData();
                break;
            }
            case R.id.iv_today_acquisition_open: {
                Intent intent = new Intent(HomeActivity.this, CustomerAcquisitionReportActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.iv_most_sold_today_refresh: {
                getProductOrderReportData();
                break;
            }
            case R.id.iv_most_sold_today_open: {
                Intent intent = new Intent(HomeActivity.this, ProductOrderReportActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private class GroupClickListener implements ExpandableListView.OnGroupClickListener {

        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            MenuModel menuModel = headerList.get(groupPosition);
            if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.stores))) {
                Intent intent = new Intent(HomeActivity.this, StoreListActivity.class);
                storesActivityResultLauncher.launch(intent);
            } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.categories_products))) {
                Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
                categoriesActivityResultLauncher.launch(intent);
            } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.orderResponses))) {
                Intent ordersIntent = new Intent(HomeActivity.this, OrdersListActivity.class);
                startActivity(ordersIntent);
            } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.reports))) {
                Intent intent = new Intent(HomeActivity.this, ReportsActivity.class);
                startActivity(intent);
            } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.sign_out))) {
                SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
                String pushTokenId = sharedPreferences.getString(Constants.SHARED_PREF_EMPLOYEE_PUSH_TOKEN_ID, null);
                deleteFCMToken(clientId, pushTokenId);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(Constants.SHARED_PREF_KEY_IS_MAIN_ACCOUNT_LOGGED_IN);
                editor.remove(Constants.SHARED_PREF_KEY_ACCESS_TOKEN);
                editor.remove(Constants.SHARED_PREF_KEY_USER_DETAILS);
                editor.remove(Constants.SHARED_PREF_EMPLOYEE_PUSH_TOKEN_ID);
                editor.apply();
                Intent signOutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(signOutIntent);
                finish();
            }
            return false;
        }
    }

    private class DrawerItemClickListener implements ExpandableListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MenuModel menuModel = headerList.get(position);
            if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.stores))) {
                Intent intent = new Intent(HomeActivity.this, StoreListActivity.class);
                storesActivityResultLauncher.launch(intent);
            } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.categories_products))) {
                Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
                categoriesActivityResultLauncher.launch(intent);
            } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.change_password))) {

            } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.sign_out))) {
                SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
                String pushTokenId = sharedPreferences.getString(Constants.SHARED_PREF_EMPLOYEE_PUSH_TOKEN_ID, null);
                deleteFCMToken(clientId, pushTokenId);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(Constants.SHARED_PREF_KEY_IS_MAIN_ACCOUNT_LOGGED_IN);
                editor.remove(Constants.SHARED_PREF_KEY_ACCESS_TOKEN);
                editor.remove(Constants.SHARED_PREF_EMPLOYEE_PUSH_TOKEN_ID);
                editor.apply();
                finish();
            }
        }
    }

    private void deleteFCMToken(String clientId, String pushTokenId) {
        FirebaseMessaging.getInstance().deleteToken();
        DeleteEmployeePushTokenRequest request = new DeleteEmployeePushTokenRequest();
        request.setId(pushTokenId);
        Call<BaseResponse<EmployeePushToken>> deleteFCMTokenCall = Flytekart.getApiService().deleteFCMToken(
                accessToken, clientId, request);
        deleteFCMTokenCall.enqueue(new CustomCallback<BaseResponse<EmployeePushToken>>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<BaseResponse<EmployeePushToken>> call, @NotNull Response<BaseResponse<EmployeePushToken>> response) {
                Logger.i("Employee push token delete API call response received.");
                // No need to do anything
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<EmployeePushToken>> call, APIError responseBody) {
                Logger.e("Employee push token delete API response failed");
                // No need to do anything
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<EmployeePushToken>> call) {
                Logger.i("Employee push token save API call failure.");
                // No need to do anything
            }
        });
    }

    private void populateFragment() {
        /*Fragment fragment = OrganisationListFragment.newInstance(organisations);
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            //setTitle(mNavigationDrawerItemTitles[position]);
            //drawerLayout.closeDrawer(expandableListView);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }*/
        Log.e("MainActivity", "In populateFragment");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        drawerToggle.syncState();
    }

    private void getAllOrganisations() {
        showProgress(true);
        Call<BaseResponse<Organisation>> organisationCall = com.flytekart.Flytekart.getApiService().getOrganisation(accessToken, clientId);
        organisationCall.enqueue(new CustomCallback<BaseResponse<Organisation>>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<BaseResponse<Organisation>> call, @NotNull Response<BaseResponse<Organisation>> response) {
                Logger.i("Client Login API call response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    organisation = response.body().getBody();
                    Toast.makeText(getApplicationContext(), "Organisation API call successful.", Toast.LENGTH_SHORT).show();
                } else if (response.errorBody() != null) {
                    try {
                        ApiCallResponse apiCallResponse = new Gson().fromJson(
                                response.errorBody().string(), ApiCallResponse.class);
                        Toast.makeText(getApplicationContext(), apiCallResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Logger.e("Organisation List API call  response status code : " + response.code());
                populateFragment();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Organisation>> call, APIError responseBody) {
                Logger.e("Organisation List API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<Organisation>> call) {
                Logger.i("Organisation List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showProgress(boolean show) {
        if (show) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this);
            }
            progressDialog.setMessage(getResources().getString(R.string.progress_please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}