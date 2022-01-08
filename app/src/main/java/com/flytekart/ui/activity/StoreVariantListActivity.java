package com.flytekart.ui.activity;

import android.app.ProgressDialog;
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
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.VariantStoreVariantsAdapter;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
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
    private ProgressDialog progressDialog;

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
            case R.id.menu_refresh: {
                getData();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.EDIT_VARIANT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            int position = data.getIntExtra(Constants.POSITION, -1);
            VariantStoreVariantDTO editedVariant = data.getParcelableExtra(Constants.VARIANT);
            if (position != -1 && editedVariant != null) {
                variants.remove(position);
                variants.add(position, editedVariant);
                adapter.notifyItemChanged(position);
            }
        }
    }

    private void getData() {
        showProgress(true);
        Call<BaseResponse<List<VariantStoreVariantDTO>>> getVariantsCall = Flytekart.getApiService().getAllVariantsWithStoreVariants(accessToken, store.getId(), clientId, product.getId());
        getVariantsCall.enqueue(new CustomCallback<BaseResponse<List<VariantStoreVariantDTO>>>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<BaseResponse<List<VariantStoreVariantDTO>>> call, @NotNull Response<BaseResponse<List<VariantStoreVariantDTO>>> response) {
                Logger.i("Store variants list response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    variants = response.body().getBody();
                    setVariantsData();
                }
                Logger.e("Store variants List API call response status code : " + response.code());
                //populateFragment();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<VariantStoreVariantDTO>>> call, APIError responseBody) {
                Logger.e("Store variants List API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<List<VariantStoreVariantDTO>>> call) {
                Logger.i("Store variants List API call failure.");
                showProgress(false);
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
        itemIntent.putExtra(Constants.POSITION, position);
        startActivityForResult(itemIntent, Constants.EDIT_VARIANT_ACTIVITY_REQUEST_CODE);
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