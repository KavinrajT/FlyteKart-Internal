package com.flytekart.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.flytekart.models.Product;
import com.flytekart.models.Variant;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.ui.adapters.VariantsAdapter;
import com.flytekart.ui.views.TitleBarLayout;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VariantListActivity extends AppCompatActivity {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvVariantList;
    private VariantsAdapter adapter;
    private LinearLayoutManager variantsLayoutManager;
    private Product product;
    private List<Variant> variants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variant_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Variants");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvVariantList = findViewById(R.id.rv_product_list);
        rvVariantList.setHasFixedSize(true);
        variantsLayoutManager = new LinearLayoutManager(this);
        rvVariantList.setLayoutManager(variantsLayoutManager);
        rvVariantList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        product = getIntent().getParcelableExtra(Constants.PRODUCT);

        getData();
        setListeners();
        //setData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getData() {
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        String clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        Call<BaseResponse<List<Variant>>> getVariantsCall = Flytekart.getApiService().getVariantsByProductId(accessToken, product.getId(), clientId);
        getVariantsCall.enqueue(new Callback<BaseResponse<List<Variant>>>() {
            @Override
            public void onResponse(@NotNull Call<BaseResponse<List<Variant>>> call, @NotNull Response<BaseResponse<List<Variant>>> response) {
                Logger.i("Variants list response received.");
                if (response.isSuccessful() && response.body() != null) {
                    variants = response.body().getBody();
                    setVariantsData();
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
                Logger.e("Variants List API call response status code : " + response.code());
                //populateFragment();
            }

            @Override
            public void onFailure(@NotNull Call<BaseResponse<List<Variant>>> call, @NotNull Throwable t) {
                Logger.i("Variants List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVariantsData() {
        if (variants == null || variants.isEmpty()) {
            rvVariantList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(VariantListActivity.this, CreateVariantActivity.class);
                intent.putExtra(Constants.PRODUCT, product);
                startActivityForResult(intent, Constants.ADD_VARIANT_ACTIVITY_REQUEST_CODE);
            });

        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvVariantList.setVisibility(View.VISIBLE);

            adapter = new VariantsAdapter(variants);
            rvVariantList.setAdapter(adapter);
        }
    }

    private void setListeners() {
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        rvVariantList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    onVariantClicked(variants.get(pos));
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
     * Open variant details
     * @param variant
     */
    private void onVariantClicked(Variant variant) {
        Intent itemIntent = new Intent(this, CreateVariantActivity.class);
        itemIntent.putExtra(Constants.VARIANT, variant);
        itemIntent.putExtra(Constants.PRODUCT, product);
        startActivityForResult(itemIntent, Constants.ADD_VARIANT_ACTIVITY_REQUEST_CODE);
    }
}