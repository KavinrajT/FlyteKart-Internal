package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.OrdersOverTimeReportItem;
import com.flytekart.models.Store;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.OrdersOverTimeReportAdapter;
import com.flytekart.ui.views.EndlessRecyclerOnScrollListener;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class OrdersOverTimeReportActivity extends AppCompatActivity {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvOrdersOverTimeReport;
    private OrdersOverTimeReportAdapter adapter;
    private List<OrdersOverTimeReportItem> ordersOverTimeReportItems;
    private LinearLayoutManager reportItemsLayoutManager;
    private String clientId;
    private String accessToken;
    private ProgressDialog progressDialog;
    private int nextPageNumber = 0;
    private boolean isLoadingOrders = false;
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_over_time_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.orders_over_time_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvOrdersOverTimeReport = findViewById(R.id.rv_orders_over_time_report);
        rvOrdersOverTimeReport.setHasFixedSize(true);
        reportItemsLayoutManager = new LinearLayoutManager(this);
        rvOrdersOverTimeReport.setLayoutManager(reportItemsLayoutManager);
        rvOrdersOverTimeReport.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvOrdersOverTimeReport.addOnScrollListener(new EndlessRecyclerOnScrollListener(reportItemsLayoutManager) {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        store = getIntent().getParcelableExtra(Constants.STORE);
        if (store != null) {
            getSupportActionBar().setSubtitle(store.getName());
        }

        getData();
        //setListeners();
        //setData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_refresh: {
                getData();
                return true;
            }
            case R.id.menu_filters: {
                Intent filtersIntent = new Intent(this, ProductOrderReportFiltersActivity.class);
                startActivityForResult(filtersIntent, Constants.PRODUCT_ORDER_REPORT_FILTERS_ACTIVITY_REQUEST_CODE);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMoreData() {
        if (nextPageNumber > 0
                && (nextPageNumber * Constants.DEFAULT_PAGE_SIZE) == ordersOverTimeReportItems.size()) {
            if (!isLoadingOrders) {
                isLoadingOrders = true;
                getData();
            }
        }
    }

    private void getData() {
        showProgress(true);
        String storeId;
        if (store == null) {
            storeId = null;
        } else {
            storeId = store.getId();
        }
        Call<BaseResponse<List<OrdersOverTimeReportItem>>> getProductOrderReportCall = Flytekart.getApiService()
                .getOrdersOverTimeReport(accessToken, clientId, storeId, nextPageNumber, Constants.DEFAULT_PAGE_SIZE);
        getProductOrderReportCall.enqueue(new CustomCallback<BaseResponse<List<OrdersOverTimeReportItem>>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<OrdersOverTimeReportItem>>> call, Response<BaseResponse<List<OrdersOverTimeReportItem>>> response) {
                Logger.i("ProductOrderReportItem response received.");
                showProgress(false);
                isLoadingOrders = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<OrdersOverTimeReportItem> ordersOverTimeReportItems = response.body().getBody();
                    setProductOrderReportData(ordersOverTimeReportItems);
                }
                Logger.e("ProductOrderReportItem API call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<OrdersOverTimeReportItem>>> call, APIError responseBody) {
                Logger.e("ProductOrderReportItem API call failed.");
                showProgress(false);
                isLoadingOrders = false;
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<List<OrdersOverTimeReportItem>>> call) {
                Logger.i("ProductOrderReportItem call failure.");
                showProgress(false);
                isLoadingOrders = false;
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setProductOrderReportData(List<OrdersOverTimeReportItem> ordersOverTimeReportItems) {
        if ((this.ordersOverTimeReportItems == null || this.ordersOverTimeReportItems.isEmpty()) &&
                (ordersOverTimeReportItems == null || ordersOverTimeReportItems.isEmpty())) {
            rvOrdersOverTimeReport.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            /*llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(ProductOrderReportActivity.this, CreateStoreActivity.class);
                startActivityForResult(intent, Constants.ADD_STORE_ACTIVITY_REQUEST_CODE);
            });*/
        } else {
            if (adapter == null) {
                llNoRecordsFound.setVisibility(View.GONE);
                rvOrdersOverTimeReport.setVisibility(View.VISIBLE);

                this.ordersOverTimeReportItems = ordersOverTimeReportItems;
                adapter = new OrdersOverTimeReportAdapter(this.ordersOverTimeReportItems);
                rvOrdersOverTimeReport.setAdapter(adapter);
            } else {
                int initialSize = this.ordersOverTimeReportItems.size();
                this.ordersOverTimeReportItems.addAll(ordersOverTimeReportItems);
                adapter.notifyItemRangeInserted(initialSize, this.ordersOverTimeReportItems.size() - 1);
            }
            nextPageNumber++;
        }
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