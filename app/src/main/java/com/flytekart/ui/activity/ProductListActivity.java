package com.flytekart.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.Category;
import com.flytekart.models.Product;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.ui.adapters.ProductsAdapter;
import com.flytekart.ui.views.TitleBarLayout;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvProductList;
    private ProductsAdapter adapter;
    private Category category;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.products);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvProductList = findViewById(R.id.rv_product_list);
        rvProductList.setHasFixedSize(true);
        LinearLayoutManager productsLayoutManager = new LinearLayoutManager(this);
        rvProductList.setLayoutManager(productsLayoutManager);
        rvProductList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        category = getIntent().getParcelableExtra(Constants.CATEGORY);
        getSupportActionBar().setSubtitle(category.getName());

        getData();
        setListeners();
        //setData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_create: {
                Intent createProductIntent = new Intent(this, CreateProductActivity.class);
                createProductIntent.putExtra(Constants.CATEGORY, category);
                startActivityForResult(createProductIntent, Constants.ADD_PRODUCT_ACTIVITY_REQUEST_CODE);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getData() {
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        String clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        Call<BaseResponse<List<Product>>> getProductsCall = Flytekart.getApiService().getProductsByCategoryId(accessToken, clientId, category.getId());
        getProductsCall.enqueue(new Callback<BaseResponse<List<Product>>>() {
            @Override
            public void onResponse(@NotNull Call<BaseResponse<List<Product>>> call, @NotNull Response<BaseResponse<List<Product>>> response) {
                Logger.i("Categories list response received.");
                if (response.isSuccessful() && response.body() != null) {
                    products = response.body().getBody();
                    setProductsData();
                } else if (response.body().getApiError() != null || response.errorBody() != null) {
                    // TODO Need to write this properly
                    try {
                        ApiCallResponse apiCallResponse = new Gson().fromJson(
                                response.errorBody().string(), ApiCallResponse.class);
                        Toast.makeText(getApplicationContext(), apiCallResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Logger.e("Categories List API call response status code : " + response.code());
                //populateFragment();
            }

            @Override
            public void onFailure(@NotNull Call<BaseResponse<List<Product>>> call, @NotNull Throwable t) {
                Logger.i("Categories List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setProductsData() {
        if (products == null || products.isEmpty()) {
            rvProductList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(ProductListActivity.this, CreateProductActivity.class);
                intent.putExtra(Constants.CATEGORY, category);
                startActivityForResult(intent, Constants.ADD_PRODUCT_ACTIVITY_REQUEST_CODE);
            });

        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvProductList.setVisibility(View.VISIBLE);

            adapter = new ProductsAdapter(products);
            rvProductList.setAdapter(adapter);
        }
    }

    private void setListeners() {
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        rvProductList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    onProductClicked(products.get(pos));
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    /**
     * Open product details
     * @param product
     */
    private void onProductClicked(Product product) {
        Intent itemIntent = new Intent(this, CreateProductActivity.class);
        itemIntent.putExtra(Constants.CATEGORY, category);
        itemIntent.putExtra(Constants.PRODUCT, product);
        startActivity(itemIntent);
    }
}