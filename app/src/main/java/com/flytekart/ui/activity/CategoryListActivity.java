package com.flytekart.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.Category;
import com.flytekart.ui.adapters.CategoryRecyclerListAdapter;
import com.flytekart.ui.views.RecyclerItemClickListener;
import com.flytekart.ui.views.TitleBarLayout;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class CategoryListActivity extends AppCompatActivity implements TitleBarLayout.TitleBarIconClickListener {

    private TitleBarLayout titleBarLayout;
    private LinearLayout llNoRecordsFound;
    private RecyclerView rvCategoryList;
    private CategoryRecyclerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        titleBarLayout = findViewById(R.id.titleBar);
        titleBarLayout.setTitleText("CATEGORIES");
        titleBarLayout.setOnIconClickListener(this);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvCategoryList = findViewById(R.id.rv_category_list);

        setData();
    }

    private void setData() {
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String categoryJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CATEGORIES, null);
        List<Category> categories = new Gson().fromJson(categoryJsonStr, new TypeToken<List<Category>>() {
        }.getType());
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

            adapter = new CategoryRecyclerListAdapter(categories);
            rvCategoryList.setAdapter(adapter);
            rvCategoryList.setLayoutManager(new LinearLayoutManager(this));
            rvCategoryList.addOnItemTouchListener(
                    new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(CategoryListActivity.this, SubCategoryListActivity.class);
                            //TODO add category id in the intent
                            startActivity(intent);
                        }
                    })
            );
        }
    }

    @Override
    public void onTitleBarRightIconClicked(View view) {
        Intent intent = new Intent(CategoryListActivity.this, CreateCategoryActivity.class);
        startActivityForResult(intent, Constants.ADD_CATEGORY_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setData();
    }
}