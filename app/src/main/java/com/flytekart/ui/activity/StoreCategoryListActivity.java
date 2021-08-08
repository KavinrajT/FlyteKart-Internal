package com.flytekart.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
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
import com.flytekart.models.Store;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.ui.adapters.CategoriesAdapter;
import com.flytekart.ui.adapters.CategoryStoreCategoriesAdapter;
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

public class StoreCategoryListActivity extends AppCompatActivity implements CategoryStoreCategoriesAdapter.CategoryClickListener {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvStoreCategoryList;
    private CategoryStoreCategoriesAdapter adapter;
    private List<CategoryStoreCategoryDTO> categories;
    private LinearLayoutManager storeCategoriesLayoutManager;
    private LayoutInflater layoutInflater;
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

        layoutInflater = LayoutInflater.from(this);

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
                Intent createCategoryIntent = new Intent(this, CreateCategoryActivity.class);
                startActivityForResult(createCategoryIntent, Constants.ADD_CATEGORY_ACTIVITY_REQUEST_CODE);
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
        Call<BaseResponse<List<CategoryStoreCategoryDTO>>> getCategoriesCall = Flytekart.getApiService().getAllCategoriesWithStoreCategories(accessToken, store.getId(), clientId);
        getCategoriesCall.enqueue(new Callback<BaseResponse<List<CategoryStoreCategoryDTO>>>() {
            @Override
            public void onResponse(@NotNull Call<BaseResponse<List<CategoryStoreCategoryDTO>>> call, @NotNull Response<BaseResponse<List<CategoryStoreCategoryDTO>>> response) {
                Logger.i("Store categories list response received.");
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body().getBody();
                    setCategoriesData();
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
                Logger.e("Store categories List API call response status code : " + response.code());
                //populateFragment();
            }

            @Override
            public void onFailure(@NotNull Call<BaseResponse<List<CategoryStoreCategoryDTO>>> call, @NotNull Throwable t) {
                Logger.i("Store categories List API call failure.");
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
     * @param position
     */
    @Override
    public void onCategoryClicked(int position) {
        CategoryStoreCategoryDTO category = categories.get(position);
        Intent itemIntent = new Intent(this, ProductListActivity.class);
        itemIntent.putExtra(Constants.CATEGORY, category);
        startActivity(itemIntent);
    }

    @Override
    public void onEdit(int position) {
        /*Intent editCategoryIntent = new Intent(this, CreateCategoryActivity.class);
        editCategoryIntent.putExtra(Constants.CATEGORY, categories.get(position));
        editCategoryIntent.putExtra(Constants.POSITION, position);
        startActivityForResult(editCategoryIntent, Constants.EDIT_CATEGORY_ACTIVITY_REQUEST_CODE);*/
        View dialogView = layoutInflater.inflate(R.layout.dialog_store_category, null);
        /*etAttributeName = dialogView.findViewById(R.id.et_attribute_name);
        EditText etAttributeValueName = dialogView.findViewById(R.id.et_attribute_value_name);
        etAttributeName.addTextChangedListener(new CreateVariantActivity.AttributeNamePrefixListener());
        etAttributeName.setAdapter(attributeNameAdapter);*/

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCategory(position);
            }
        });
        builder.show();
    }

    private void deleteCategory(int position) {
        /*Category category = categories.get(position);
        Call<BaseResponse<Category>> deleteCategoryCall = Flytekart.getApiService().deleteCategory(accessToken, category.getId(), clientId);
        deleteCategoryCall.enqueue(new Callback<BaseResponse<Category>>() {
            @Override
            public void onResponse(@NotNull Call<BaseResponse<Category>> call, @NotNull Response<BaseResponse<Category>> response) {
                Logger.i("Delete category response received.");
                if (response.isSuccessful() && response.body() != null) {
                    Category deletedCategory = response.body().getBody();
                    categories.remove(position);
                    adapter.notifyItemRemoved(position);
                    if (position <= categories.size() - 1) {
                        adapter.notifyItemRangeChanged(position, categories.size() - position);
                    }
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
                Logger.e("Delete categoryAPI call response status code : " + response.code());
                //populateFragment();
            }

            @Override
            public void onFailure(@NotNull Call<BaseResponse<Category>> call, @NotNull Throwable t) {
                Logger.i("Delete category API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });*/
    }
}