package com.flytekart.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.ProductStoreProductDTO;
import com.flytekart.models.Store;
import com.flytekart.models.VariantStoreVariantDTO;
import com.flytekart.models.request.CreateStoreProductRequest;
import com.flytekart.models.request.CreateStoreVariantRequest;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.ui.adapters.VariantStoreVariantsAdapter;
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

public class StoreVariantListActivity extends AppCompatActivity {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvStoreVariantList;
    private VariantStoreVariantsAdapter adapter;
    private List<VariantStoreVariantDTO> variants;
    private LinearLayoutManager storeVariantsLayoutManager;
    private String clientId;
    private String accessToken;
    private Store store;
    private ProductStoreProductDTO product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_variant_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.variants);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvStoreVariantList = findViewById(R.id.rv_store_product_list);
        rvStoreVariantList.setHasFixedSize(true);
        storeVariantsLayoutManager = new LinearLayoutManager(this);
        rvStoreVariantList.setLayoutManager(storeVariantsLayoutManager);
        rvStoreVariantList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        store = getIntent().getParcelableExtra(Constants.STORE);
        product = getIntent().getParcelableExtra(Constants.PRODUCT);
        getSupportActionBar().setSubtitle(product.getName());

        getData();
        setListeners();
        //setData();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == Constants.ADD_CATEGORY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Category addedCategory = data.getParcelableExtra(Constants.CATEGORY);
            if (addedCategory != null) {
                categories.add(addedCategory);
                adapter.notifyItemInserted(categories.size() - 1);
            }
        } else if (requestCode == Constants.EDIT_CATEGORY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            int position = data.getIntExtra(Constants.POSITION, -1);
            Category editedCategory = data.getParcelableExtra(Constants.CATEGORY);
            if (position != -1 && editedCategory != null) {
                categories.remove(position);
                categories.add(position, editedCategory);
                adapter.notifyItemChanged(position);
            }
        }*/
    }

    private void getData() {
        Call<BaseResponse<List<VariantStoreVariantDTO>>> getVariantsCall = Flytekart.getApiService().getAllVariantsWithStoreVariants(accessToken, store.getId(), clientId, product.getId());
        getVariantsCall.enqueue(new Callback<BaseResponse<List<VariantStoreVariantDTO>>>() {
            @Override
            public void onResponse(@NotNull Call<BaseResponse<List<VariantStoreVariantDTO>>> call, @NotNull Response<BaseResponse<List<VariantStoreVariantDTO>>> response) {
                Logger.i("Store variants list response received.");
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
                Logger.e("Store variants List API call response status code : " + response.code());
                //populateFragment();
            }

            @Override
            public void onFailure(@NotNull Call<BaseResponse<List<VariantStoreVariantDTO>>> call, @NotNull Throwable t) {
                Logger.i("Store variants List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVariantsData() {
        if (variants == null || variants.isEmpty()) {
            rvStoreVariantList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);

        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvStoreVariantList.setVisibility(View.VISIBLE);

            adapter = new VariantStoreVariantsAdapter(variants);
            rvStoreVariantList.setAdapter(adapter);
        }
    }

    private void setListeners() {
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        rvStoreVariantList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    onVariantClicked(pos);
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
     * @param position
     */
    public void onVariantClicked(int position) {
        // Do nothing for now. User should be able to use custom price and original price here
        VariantStoreVariantDTO variantStoreVariantDTO = variants.get(position);
        Intent itemIntent = new Intent(this, StoreVariantActivity.class);
        itemIntent.putExtra(Constants.STORE, store);
        itemIntent.putExtra(Constants.PRODUCT, product);
        itemIntent.putExtra(Constants.VARIANT, variantStoreVariantDTO);
        startActivity(itemIntent);
    }
}