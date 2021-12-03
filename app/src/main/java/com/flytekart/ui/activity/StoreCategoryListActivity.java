package com.flytekart.ui.activity;

import android.app.ProgressDialog;
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
import com.flytekart.models.CategoryStoreCategoryDTO;
import com.flytekart.models.Store;
import com.flytekart.models.request.CreateStoreCategoryRequest;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.CategoryStoreCategoriesAdapter;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class StoreCategoryListActivity extends AppCompatActivity implements CategoryStoreCategoriesAdapter.CategoryClickListener {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvStoreCategoryList;
    private CategoryStoreCategoriesAdapter adapter;
    private List<CategoryStoreCategoryDTO> categories;
    private LinearLayoutManager storeCategoriesLayoutManager;
    private ProgressDialog progressDialog;
    private String clientId;
    private String accessToken;
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_category_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.categories);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvStoreCategoryList = findViewById(R.id.rv_store_category_list);
        rvStoreCategoryList.setHasFixedSize(true);
        storeCategoriesLayoutManager = new LinearLayoutManager(this);
        rvStoreCategoryList.setLayoutManager(storeCategoriesLayoutManager);
        rvStoreCategoryList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        store = getIntent().getParcelableExtra(Constants.STORE);
        getSupportActionBar().setSubtitle(store.getName());

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
        Call<BaseResponse<List<CategoryStoreCategoryDTO>>> getCategoriesCall = Flytekart.getApiService().getAllCategoriesWithStoreCategories(accessToken, store.getId(), clientId);
        getCategoriesCall.enqueue(new CustomCallback<BaseResponse<List<CategoryStoreCategoryDTO>>>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<BaseResponse<List<CategoryStoreCategoryDTO>>> call, @NotNull Response<BaseResponse<List<CategoryStoreCategoryDTO>>> response) {
                Logger.i("Store categories list response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body().getBody();
                    setCategoriesData();
                }
                Logger.e("Store categories List API call response status code : " + response.code());
                //populateFragment();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<CategoryStoreCategoryDTO>>> call, APIError responseBody) {
                Logger.e("Store categories List API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<List<CategoryStoreCategoryDTO>>> call) {
                Logger.i("Store categories List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCategoriesData() {
        if (categories == null || categories.isEmpty()) {
            rvStoreCategoryList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(StoreCategoryListActivity.this, CreateCategoryActivity.class);
                startActivityForResult(intent, Constants.ADD_CATEGORY_ACTIVITY_REQUEST_CODE);
            });

        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvStoreCategoryList.setVisibility(View.VISIBLE);

            adapter = new CategoryStoreCategoriesAdapter(this, categories);
            rvStoreCategoryList.setAdapter(adapter);
        }
    }

    private void setListeners() {
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        rvStoreCategoryList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    onCategoryClicked(pos);
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
     *
     * @param position
     */
    @Override
    public void onCategoryClicked(int position) {
        CategoryStoreCategoryDTO category = categories.get(position);
        Intent itemIntent = new Intent(this, StoreProductListActivity.class);
        itemIntent.putExtra(Constants.STORE, store);
        itemIntent.putExtra(Constants.CATEGORY, category);
        startActivity(itemIntent);
    }

    @Override
    public void onEdit(int position) {
        /*Intent editCategoryIntent = new Intent(this, CreateCategoryActivity.class);
        editCategoryIntent.putExtra(Constants.CATEGORY, categories.get(position));
        editCategoryIntent.putExtra(Constants.POSITION, position);
        startActivityForResult(editCategoryIntent, Constants.EDIT_CATEGORY_ACTIVITY_REQUEST_CODE);*/

        String message;
        if (categories.get(position).getStoreCategoryId() != null && categories.get(position).getStoreCategoryDeletedAt() == null) {
            message = "Do you want to mark the category " + categories.get(position).getName() + " as unavailable at this store? This will mark all products under this category as unavailable.";
        } else {
            message = "Do you want to mark the category " + categories.get(position).getName() + " as available at this store?";
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
                enableCategory(position);
            }
        });
        builder.show();
    }

    /**
     * Enable a disabled category or disable an enabled category.
     * Make API call and update UI.
     *
     * @param position
     */
    private void enableCategory(int position) {
        CategoryStoreCategoryDTO category = categories.get(position);
        CreateStoreCategoryRequest request = new CreateStoreCategoryRequest();
        request.setStoreCategoryId(category.getStoreCategoryId());
        request.setStoreId(store.getId());
        request.setCategoryId(category.getId());
        boolean isActive = false;
        if (category.getStoreCategoryId() != null && category.getStoreCategoryDeletedAt() == null) {
            isActive = true;
        }
        request.setActive(!isActive);
        showProgress(true);
        Call<BaseResponse<CategoryStoreCategoryDTO>> deleteCategoryCall = Flytekart.getApiService().saveStoreCategory(accessToken, clientId, request);
        deleteCategoryCall.enqueue(new CustomCallback<BaseResponse<CategoryStoreCategoryDTO>>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<BaseResponse<CategoryStoreCategoryDTO>> call, @NotNull Response<BaseResponse<CategoryStoreCategoryDTO>> response) {
                Logger.i("Save store category response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    CategoryStoreCategoryDTO savedCategory = response.body().getBody();
                    categories.remove(position);
                    categories.add(position, savedCategory);
                    adapter.notifyItemChanged(position);
                }
                Logger.e("Save store category API call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<CategoryStoreCategoryDTO>> call, APIError responseBody) {
                Logger.e("Save store category API call failure.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<CategoryStoreCategoryDTO>> call) {
                Logger.i("Save store category API call failure.");
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