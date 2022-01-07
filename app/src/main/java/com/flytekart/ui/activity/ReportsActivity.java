package com.flytekart.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.flytekart.R;
import com.flytekart.models.Store;
import com.flytekart.utils.Constants;

public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvProductOrderReport;
    private TextView tvOrdersOverTimeReport;
    private TextView tvCustomerAcquisitionReport;
    private TextView tvCustomerOrderReport;
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.reports);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tvProductOrderReport = findViewById(R.id.tv_product_order_report);
        tvOrdersOverTimeReport = findViewById(R.id.tv_orders_over_time_report);
        tvCustomerAcquisitionReport = findViewById(R.id.tv_customer_acquisition_report);
        tvCustomerOrderReport = findViewById(R.id.tv_customer_order_report);

        tvProductOrderReport.setOnClickListener(this);
        tvOrdersOverTimeReport.setOnClickListener(this);
        tvCustomerAcquisitionReport.setOnClickListener(this);
        tvCustomerOrderReport.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_product_order_report: {
                Intent intent = new Intent(this, ProductOrderReportActivity.class);
                //storeIntent.putExtra(Constants.STORE, store);
                startActivity(intent);
                break;
            }
            case R.id.tv_orders_over_time_report: {
                Intent intent = new Intent(this, OrdersOverTimeReportActivity.class);
                //storeIntent.putExtra(Constants.STORE, store);
                startActivity(intent);
                break;
            }
            case R.id.tv_customer_acquisition_report: {
                Intent intent = new Intent(this, CustomerAcquisitionReportActivity.class);
                //storeIntent.putExtra(Constants.STORE, store);
                startActivity(intent);
                break;
            }
            case R.id.tv_customer_order_report: {
                Intent intent = new Intent(this, CustomerOrderReportActivity.class);
                //storeIntent.putExtra(Constants.STORE, store);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.EDIT_STORE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Store editedStore = data.getParcelableExtra(Constants.STORE);
            if (editedStore != null) {
                store = editedStore;
                getSupportActionBar().setTitle(store.getName());
            }
        }
    }
}