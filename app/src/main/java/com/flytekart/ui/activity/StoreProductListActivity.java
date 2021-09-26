package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.Category;
import com.flytekart.models.CategoryStoreCategoryDTO;
import com.flytekart.models.ProductStoreProductDTO;
import com.flytekart.models.Store;
import com.flytekart.models.request.CreateStoreProductRequest;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.BaseErrorResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.CategoryStoreCategoriesAdapter;
import com.flytekart.ui.adapters.ProductStoreProductsAdapter;
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

public class StoreProductListActivity extends AppCompatActivity implements ProductStoreProductsAdapter.ProductClickListener {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvStoreProductList;
    private ProductStoreProductsAdapter adapter;
    private List<ProductStoreProductDTO> products;
    private LinearLayoutManager storeProductsLayoutManager;
    private String clientId;
    private String accessToken;
    private Store store;
    private CategoryStoreCategoryDTO category;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_product_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.products);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvStoreProductList = findViewById(R.id.rv_store_product_list);
        rvStoreProductList.setHasFixedSize(true);
        storeProductsLayoutManager = new LinearLayoutManager(this);
        rvStoreProductList.setLayoutManager(storeProductsLayoutManager);
        rvStoreProductList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        store = getIntent().getParcelableExtra(Constants.STORE);
        category = getIntent().getParcelableExtra(Constants.CATEGORY);
        getSupportActionBar().setSubtitle(category.getName());

        getData();
        //setListeners();
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
        showProgress(true);
        Call<BaseResponse<List<ProductStoreProductDTO>>> getProductsCall = Flytekart.getApiService().getAllProductsWithStoreProducts(accessToken, store.getId(), clientId, category.getId());
        getProductsCall.enqueue(new CustomCallback<BaseResponse<List<ProductStoreProductDTO>>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<ProductStoreProductDTO>>> call, Response<BaseResponse<List<ProductStoreProductDTO>>> response) {
                Logger.i("Store products list response received.");
                showProgress(true);
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
                Logger.e("Store products List API call response status code : " + response.code());
                //populateFragment();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<ProductStoreProductDTO>>> call, BaseErrorResponse responseBody) {
                Logger.i("Store products list call failed.");
            }

            @Override
            public void onFailure(@NotNull Call<BaseResponse<List<ProductStoreProductDTO>>> call, @NotNull Throwable t) {
                Logger.i("Store products List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setProductsData() {
        if (products == null || products.isEmpty()) {
            rvStoreProductList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(StoreProductListActivity.this, CreateCategoryActivity.class);
                startActivityForResult(intent, Constants.ADD_CATEGORY_ACTIVITY_REQUEST_CODE);
            });

        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvStoreProductList.setVisibility(View.VISIBLE);

            adapter = new ProductStoreProductsAdapter(this, products);
            rvStoreProductList.setAdapter(adapter);
        }
    }

    private void setListeners() {
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        rvStoreProductList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    onProductClicked(pos);
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
     * Open products list of this category
     * @param position
     */
    @Override
    public void onProductClicked(int position) {
        ProductStoreProductDTO product = products.get(position);
        Intent itemIntent = new Intent(this, StoreVariantListActivity.class);
        itemIntent.putExtra(Constants.STORE, store);
        itemIntent.putExtra(Constants.PRODUCT, product);
        startActivity(itemIntent);
    }

    @Override
    public void onEdit(int position) {
        /*Intent editCategoryIntent = new Intent(this, CreateCategoryActivity.class);
        editCategoryIntent.putExtra(Constants.CATEGORY, categories.get(position));
        editCategoryIntent.putExtra(Constants.POSITION, position);
        startActivityForResult(editCategoryIntent, Constants.EDIT_CATEGORY_ACTIVITY_REQUEST_CODE);*/

        String message;
        if (products.get(position).getStoreProductId() != null && products.get(position).getStoreProductDeletedAt() == null)  {
            message = "Do you want to mark the product " + products.get(position).getName() + " as unavailable at this store?";
        } else {
            message = "Do you want to mark the product " + products.get(position).getName() + " as available at this store?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enableProduct(position);
            }
        });
        builder.show();
    }

    /**
     * Enable a disabled category or disable an enabled category.
     * Make API call and update UI.
     * @param position
     */
    private void enableProduct(int position) {
        ProductStoreProductDTO product = products.get(position);
        CreateStoreProductRequest request = new CreateStoreProductRequest();
        request.setStoreProductId(product.getStoreProductId());
        request.setStoreId(store.getId());
        request.setProductId(product.getId());
        boolean isActive = false;
        if (product.getStoreProductId() != null && product.getStoreProductDeletedAt() == null) {
            isActive = true;
        }
        request.setActive(!isActive);
        showProgress(true);
        Call<BaseResponse<ProductStoreProductDTO>> enableProductCall = Flytekart.getApiService().saveStoreProduct(accessToken, clientId, request);
        enableProductCall.enqueue(new CustomCallback<BaseResponse<ProductStoreProductDTO>>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<BaseResponse<ProductStoreProductDTO>> call, @NotNull Response<BaseResponse<ProductStoreProductDTO>> response) {
                Logger.i("Save store product response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ProductStoreProductDTO savedCategory = response.body().getBody();
                    products.remove(position);
                    products.add(position, savedCategory);
                    adapter.notifyItemChanged(position);
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
                Logger.e("Save store product API call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<ProductStoreProductDTO>> call, BaseErrorResponse responseBody) {
                Logger.e("Save store product API call failed.");
                showProgress(false);
            }

            @Override
            public void onFailure(@NotNull Call<BaseResponse<ProductStoreProductDTO>> call, @NotNull Throwable t) {
                Logger.i("Save store product API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
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