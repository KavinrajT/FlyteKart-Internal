package com.flytekart.myapplication.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.myapplication.R;
import com.flytekart.myapplication.models.Product;
import com.flytekart.myapplication.ui.adapters.ProductRecyclerListAdapter;
import com.flytekart.myapplication.ui.views.TitleBarLayout;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ProductListActivity extends AppCompatActivity implements TitleBarLayout.TitleBarIconClickListener {

    private TitleBarLayout titleBarLayout;
    private LinearLayout llNoRecordsFound;
    private RecyclerView rvProductList;
    private ProductRecyclerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        titleBarLayout = findViewById(R.id.titleBar);
        titleBarLayout.setTitleText("PRODUCTS");
        titleBarLayout.setOnIconClickListener(this);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvProductList = findViewById(R.id.rv_product_list);

        setData();
    }

    private void setData() {
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String productJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_PRODUCTS, null);
        List<Product> products = new Gson().fromJson(productJsonStr, new TypeToken<List<Product>>() {
        }.getType());
        if (products == null || products.isEmpty()) {
            rvProductList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(ProductListActivity.this, CreateProductActivity.class);
                startActivityForResult(intent, Constants.ADD_PRODUCT_ACTIVITY_REQUEST_CODE);
            });

        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvProductList.setVisibility(View.VISIBLE);

            adapter = new ProductRecyclerListAdapter(products);
            rvProductList.setAdapter(adapter);
            rvProductList.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    public void onTitleBarRightIconClicked(View view) {
        Intent intent = new Intent(ProductListActivity.this, CreateProductActivity.class);
        startActivityForResult(intent, Constants.ADD_PRODUCT_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setData();
    }
}