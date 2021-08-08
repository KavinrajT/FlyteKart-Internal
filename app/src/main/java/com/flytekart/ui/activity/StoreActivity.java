package com.flytekart.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_store_details:
                break;
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
}