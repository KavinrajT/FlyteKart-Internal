package com.flytekart.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.CustomerAcquisitionReportItem;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.CustomerAcquisitionReportAdapter;
import com.flytekart.ui.adapters.ProductOrderReportAdapter;
import com.flytekart.ui.views.EndlessRecyclerOnScrollListener;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CustomerAcquisitionReportActivity extends AppCompatActivity {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvCustomerAcquisitionReport;
    private CustomerAcquisitionReportAdapter adapter;
    private List<CustomerAcquisitionReportItem> customerAcquisitionReportItems;
    private LinearLayoutManager reportItemsLayoutManager;
    private String clientId;
    private String accessToken;
    private ProgressDialog progressDialog;
    private int nextPageNumber = 0;
    private boolean isLoadingOrders = false;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_acquisition_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.customer_acquisition_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvCustomerAcquisitionReport = findViewById(R.id.rv_customer_acquisition_report);
        rvCustomerAcquisitionReport.setHasFixedSize(true);
        reportItemsLayoutManager = new LinearLayoutManager(this);
        rvCustomerAcquisitionReport.setLayoutManager(reportItemsLayoutManager);
        rvCustomerAcquisitionReport.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvCustomerAcquisitionReport.addOnScrollListener(new EndlessRecyclerOnScrollListener(reportItemsLayoutManager) {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        registerForActivityResults();
        getData();
        //setListeners();
        //setData();
    }

    private void registerForActivityResults() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            // TODO Need to update data based on filters
                        }
                    }
                });
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
                activityResultLauncher.launch(filtersIntent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMoreData() {
        if (nextPageNumber > 0
                && (nextPageNumber * Constants.DEFAULT_PAGE_SIZE) == customerAcquisitionReportItems.size()) {
            if (!isLoadingOrders) {
                isLoadingOrders = true;
                getData();
            }
        }
    }

    private void getData() {
        showProgress(true);
        Call<BaseResponse<List<CustomerAcquisitionReportItem>>> getCustomerAcquisitionReportCall = Flytekart.getApiService()
                .getCustomerAcquisitionReport(accessToken, clientId, nextPageNumber, Constants.DEFAULT_PAGE_SIZE);
        getCustomerAcquisitionReportCall.enqueue(new CustomCallback<BaseResponse<List<CustomerAcquisitionReportItem>>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<CustomerAcquisitionReportItem>>> call, Response<BaseResponse<List<CustomerAcquisitionReportItem>>> response) {
                Logger.i("CustomerAcquisitionReport response received.");
                showProgress(false);
                isLoadingOrders = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<CustomerAcquisitionReportItem> customerAcquisitionReportItems = response.body().getBody();
                    setCustomerAcquisitionReportData(customerAcquisitionReportItems);
                }
                Logger.e("CustomerAcquisitionReport API call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<CustomerAcquisitionReportItem>>> call, APIError responseBody) {
                Logger.e("CustomerAcquisitionReport API call failed.");
                showProgress(false);
                isLoadingOrders = false;
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<List<CustomerAcquisitionReportItem>>> call) {
                Logger.i("CustomerAcquisitionReport call failure.");
                showProgress(false);
                isLoadingOrders = false;
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCustomerAcquisitionReportData(List<CustomerAcquisitionReportItem> customerAcquisitionReportItems) {
        if ((this.customerAcquisitionReportItems == null || this.customerAcquisitionReportItems.isEmpty()) &&
                (customerAcquisitionReportItems == null || customerAcquisitionReportItems.isEmpty())) {
            rvCustomerAcquisitionReport.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            /*llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(ProductOrderReportActivity.this, CreateStoreActivity.class);
                startActivityForResult(intent, Constants.ADD_STORE_ACTIVITY_REQUEST_CODE);
            });*/
        } else {
            if (adapter == null) {
                llNoRecordsFound.setVisibility(View.GONE);
                rvCustomerAcquisitionReport.setVisibility(View.VISIBLE);

                this.customerAcquisitionReportItems = customerAcquisitionReportItems;
                adapter = new CustomerAcquisitionReportAdapter(this.customerAcquisitionReportItems);
                rvCustomerAcquisitionReport.setAdapter(adapter);
            } else {
                int initialSize = this.customerAcquisitionReportItems.size();
                this.customerAcquisitionReportItems.addAll(customerAcquisitionReportItems);
                adapter.notifyItemRangeInserted(initialSize, this.customerAcquisitionReportItems.size() - 1);
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