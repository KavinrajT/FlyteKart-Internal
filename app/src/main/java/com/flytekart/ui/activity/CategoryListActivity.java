package com.flytekart.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.Category;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.ui.adapters.CategoriesAdapter;
import com.flytekart.ui.views.RecyclerItemClickListener;
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

public class CategoryListActivity extends AppCompatActivity implements TitleBarLayout.TitleBarIconClickListener {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvCategoryList;
    private CategoriesAdapter adapter;
    private List<Category> categories;
    private LinearLayoutManager categoriesLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        TitleBarLayout titleBarLayout = findViewById(R.id.titleBar);
        titleBarLayout.setTitleText("CATEGORIES");
        titleBarLayout.setOnIconClickListener(this);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvCategoryList = findViewById(R.id.rv_category_list);
        rvCategoryList.setHasFixedSize(true);
        categoriesLayoutManager = new LinearLayoutManager(this);
        rvCategoryList.setLayoutManager(categoriesLayoutManager);
        rvCategoryList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        getData();
        setListeners();
        //setData();
    }

    private void getData() {
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        String clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        Call<BaseResponse<List<Category>>> getCategoriesCall = Flytekart.getApiService().getAllCategories(accessToken, clientId);
        getCategoriesCall.enqueue(new Callback<BaseResponse<List<Category>>>() {
            @Override
            public void onResponse(@NotNull Call<BaseResponse<List<Category>>> call, @NotNull Response<BaseResponse<List<Category>>> response) {
                Logger.i("Categories list response received.");
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
                Logger.e("Categories List API call response status code : " + response.code());
                //populateFragment();
            }

            @Override
            public void onFailure(@NotNull Call<BaseResponse<List<Category>>> call, @NotNull Throwable t) {
                Logger.i("Categories List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCategoriesData() {
        if (categories == null || categories.isEmpty()) {
            rvCategoryList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(CategoryListActivity.this, CreateCategoryActivity.class);
                startActivityForResult(intent, Constants.ADD_CATEGORY_ACTIVITY_REQUEST_CODE);
            });

        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvCategoryList.setVisibility(View.VISIBLE);

            adapter = new CategoriesAdapter(categories);
            rvCategoryList.setAdapter(adapter);
        }
    }

    private void setListeners() {
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        rvCategoryList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    onCategoryClicked(categories.get(pos));
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
     * @param category
     */
    private void onCategoryClicked(Category category) {
        Intent itemIntent = new Intent(this, ProductListActivity.class);
        itemIntent.putExtra(Constants.CATEGORY, category);
        startActivity(itemIntent);
    }

    @Override
    public void onTitleBarRightIconClicked(View view) {
        Intent intent = new Intent(CategoryListActivity.this, CreateCategoryActivity.class);
        startActivityForResult(intent, Constants.ADD_CATEGORY_ACTIVITY_REQUEST_CODE);
    }
}