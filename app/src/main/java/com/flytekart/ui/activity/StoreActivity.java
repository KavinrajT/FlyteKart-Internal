package com.flytekart.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.flytekart.R;
import com.flytekart.models.Store;
import com.flytekart.ui.views.TitleBarLayout;
import com.flytekart.utils.Constants;

public class StoreActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvStoreDetails;
    private TextView tvSummary;
    private TextView tvCategoriesProducts;
    private TextView tvOrders;
    private Store store;
    private ActivityResultLauncher<Intent> createStoreActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tvStoreDetails = findViewById(R.id.tv_store_details);
        tvSummary = findViewById(R.id.tv_summary);
        tvCategoriesProducts = findViewById(R.id.tv_categories_products);
        tvOrders = findViewById(R.id.tv_orders);

        tvStoreDetails.setOnClickListener(this);
        tvSummary.setOnClickListener(this);
        tvCategoriesProducts.setOnClickListener(this);
        tvOrders.setOnClickListener(this);

        registerForActivityResults();
        store = getIntent().getParcelableExtra(Constants.STORE);
        getSupportActionBar().setTitle(store.getName());
    }

    private void registerForActivityResults() {
        createStoreActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Store editedStore = result.getData().getParcelableExtra(Constants.STORE);
                            if (editedStore != null) {
                                store = editedStore;
                                getSupportActionBar().setTitle(store.getName());
                            }
                        }
                    }
                });
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
            case R.id.tv_store_details: {
                Intent storeIntent = new Intent(this, CreateStoreActivity.class);
                storeIntent.putExtra(Constants.STORE, store);
                createStoreActivityResultLauncher.launch(storeIntent);
                break;
            }
            case R.id.tv_summary:
                Intent reportsIntent = new Intent(StoreActivity.this, ReportsActivity.class);
                reportsIntent.putExtra(Constants.STORE, store);
                startActivity(reportsIntent);
                break;
            case R.id.tv_categories_products: {
                Intent categoriesIntent = new Intent(this, StoreCategoryListActivity.class);
                categoriesIntent.putExtra(Constants.STORE, store);
                startActivity(categoriesIntent);
                break;
            }
            case R.id.tv_orders:
                Intent ordersIntent = new Intent(this, OrdersListActivity.class);
                ordersIntent.putExtra(Constants.STORE, store);
                startActivity(ordersIntent);
                break;
        }
    }
}