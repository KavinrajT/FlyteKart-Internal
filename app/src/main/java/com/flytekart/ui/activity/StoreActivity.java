package com.flytekart.ui.activity;

import android.app.ProgressDialog;
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
import com.flytekart.ui.views.TitleBarLayout;
import com.flytekart.utils.Constants;

public class StoreActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvStoreDetails;
    private TextView tvSummary;
    private TextView tvCategoriesProducts;
    private TextView tvOrders;
    private Store store;

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

        store = getIntent().getParcelableExtra(Constants.STORE);
        getSupportActionBar().setTitle(store.getName());
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
                startActivityForResult(storeIntent, Constants.EDIT_STORE_ACTIVITY_REQUEST_CODE);
                break;
            }
            case R.id.tv_summary:
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