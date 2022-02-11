package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.Category;
import com.flytekart.models.Product;
import com.flytekart.models.Store;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.CategoriesSelectionAdapter;
import com.flytekart.ui.adapters.ProductsSelectionAdapter;
import com.flytekart.ui.adapters.StoresSelectionAdapter;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ProductOrderReportFiltersActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText etCategoryName;
    private SwitchCompat swIsActive;
    private Button btnApplyFilters;
    private ProgressDialog progressDialog;
    private StoresSelectionAdapter storesSelectionAdapter;
    private CategoriesSelectionAdapter categoriesSelectionAdapter;
    private ProductsSelectionAdapter productsSelectionAdapter;
    private List<Store> stores;
    private List<Category> categories;
    private List<Product> products;

    private Category selectedCategory;
    private int position;
    private RecyclerView rvStoresList;
    private RecyclerView rvCategoriesList;
    private RecyclerView rvProductList;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_order_report_filters);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.filters);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        rvStoresList = findViewById(R.id.rv_stores_list);
        rvStoresList.setHasFixedSize(true);
        rvStoresList.setLayoutManager(new LinearLayoutManager(this));
        rvStoresList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        rvCategoriesList = findViewById(R.id.rv_categories_list);
        rvCategoriesList.setHasFixedSize(true);
        rvCategoriesList.setLayoutManager(new LinearLayoutManager(this));
        rvCategoriesList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        rvProductList = findViewById(R.id.rv_products_list);
        rvProductList.setHasFixedSize(true);
        rvProductList.setLayoutManager(new LinearLayoutManager(this));
        rvProductList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        btnApplyFilters = findViewById(R.id.btn_apply_filters);
        btnApplyFilters.setOnClickListener(this);

        sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        getStoresData();
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

    private void showErrorToast(int messageStr) {
        Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_SHORT).show();
    }

    private void getStoresData() {
        showProgress(true);
        Call<BaseResponse<List<Store>>> getStoresCall = Flytekart.getApiService().getStoresByOrg(accessToken, clientId);
        getStoresCall.enqueue(new CustomCallback<BaseResponse<List<Store>>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<List<Store>>> call) {
                Logger.i("Store List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<Store>>> call, Response<BaseResponse<List<Store>>> response) {
                showProgress(false);
                stores = response.body().getBody();
                setStoresData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<Store>>> call, APIError responseBody) {
                Logger.e("Store List API call  response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStoresData() {
        if (stores == null || stores.isEmpty()) {
            rvStoresList.setVisibility(View.GONE);
        } else {
            rvStoresList.setVisibility(View.VISIBLE);

            storesSelectionAdapter = new StoresSelectionAdapter(stores);
            rvStoresList.setAdapter(storesSelectionAdapter);
            rvStoresList.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void getCategoriesData() {
        showProgress(true);
        Call<BaseResponse<List<Category>>> getCategoriesCall = Flytekart.getApiService().getAllCategories(accessToken, clientId);
        getCategoriesCall.enqueue(new CustomCallback<BaseResponse<List<Category>>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<Category>>> call, Response<BaseResponse<List<Category>>> response) {
                Logger.i("Categories list response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body().getBody();
                    setCategoriesData();
                }
                Logger.e("Categories List API call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<Category>>> call, APIError responseBody) {
                Logger.e("Categories List API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<List<Category>>> call) {
                Logger.i("Categories List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCategoriesData() {
        if (categories == null || categories.isEmpty()) {
            rvCategoriesList.setVisibility(View.GONE);

        } else {
            rvCategoriesList.setVisibility(View.VISIBLE);

            categoriesSelectionAdapter = new CategoriesSelectionAdapter(categories);
            rvCategoriesList.setAdapter(categoriesSelectionAdapter);
        }
    }

    private void getProductsData() {
        showProgress(true);
        Call<BaseResponse<List<Product>>> getProductsCall = Flytekart.getApiService().getProductsByCategoryId(accessToken, clientId, selectedCategory.getId());
        getProductsCall.enqueue(new CustomCallback<BaseResponse<List<Product>>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<Product>>> call, Response<BaseResponse<List<Product>>> response) {
                Logger.i("Products list response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    products = response.body().getBody();
                    setProductsData();
                }
                Logger.e("Products List API call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<Product>>> call, APIError responseBody) {
                Logger.e("Products List API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<List<Product>>> call) {
                Logger.i("Products List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setProductsData() {
        if (products == null || products.isEmpty()) {
            rvProductList.setVisibility(View.GONE);
        } else {
            rvProductList.setVisibility(View.VISIBLE);

            productsSelectionAdapter = new ProductsSelectionAdapter(products, selectedCategory);
            rvProductList.setAdapter(productsSelectionAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply_filters: {
                saveCategory();
                break;
            }
        }
    }

    private void saveCategory() {
        if (selectedCategory == null) {
            selectedCategory = new Category();
        }
        if (etCategoryName.getText() != null && !etCategoryName.getText().toString().isEmpty()) {
            selectedCategory.setName(etCategoryName.getText().toString());
        } else {
            showErrorToast(R.string.err_enter_category_name);
            return;
        }
        selectedCategory.setIsActive(swIsActive.isChecked());

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        String clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        showProgress(true);
        Call<BaseResponse<Category>> saveCategoryCall = Flytekart.getApiService().saveCategory(accessToken, clientId, selectedCategory);
        saveCategoryCall.enqueue(new CustomCallback<BaseResponse<Category>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<Category>> call) {
                Logger.i("Category API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Category>> call, Response<BaseResponse<Category>> response) {
                showProgress(false);
                selectedCategory = response.body().getBody();
                Toast.makeText(getApplicationContext(), "Category saved successfully.", Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra(Constants.POSITION, position);
                data.putExtra(Constants.CATEGORY, selectedCategory);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Category>> call, APIError responseBody) {
                Logger.e("Category save API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
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