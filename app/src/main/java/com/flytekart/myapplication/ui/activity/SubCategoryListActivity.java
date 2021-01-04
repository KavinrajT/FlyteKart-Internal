package com.flytekart.myapplication.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.myapplication.R;
import com.flytekart.myapplication.models.SubCategory;
import com.flytekart.myapplication.ui.adapters.SubCategoryRecyclerListAdapter;
import com.flytekart.myapplication.ui.views.RecyclerItemClickListener;
import com.flytekart.myapplication.ui.views.TitleBarLayout;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class SubCategoryListActivity extends AppCompatActivity implements TitleBarLayout.TitleBarIconClickListener {

    private TitleBarLayout titleBarLayout;
    private LinearLayout llNoRecordsFound;
    private RecyclerView rvSubCategoryList;
    private SubCategoryRecyclerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_list);

        titleBarLayout = findViewById(R.id.titleBar);
        titleBarLayout.setTitleText("SUB CATEGORIES");
        titleBarLayout.setOnIconClickListener(this);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvSubCategoryList = findViewById(R.id.rv_sub_category_list);

        setData();
    }

    private void setData() {
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String subCategoryJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_SUB_CATEGORIES, null);
        List<SubCategory> categories = new Gson().fromJson(subCategoryJsonStr, new TypeToken<List<SubCategory>>() {
        }.getType());
        if (categories == null || categories.isEmpty()) {
            rvSubCategoryList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(SubCategoryListActivity.this, CreateSubCategoryActivity.class);
                startActivityForResult(intent, Constants.ADD_SUB_CATEGORY_ACTIVITY_REQUEST_CODE);
            });

        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvSubCategoryList.setVisibility(View.VISIBLE);

            adapter = new SubCategoryRecyclerListAdapter(categories);
            rvSubCategoryList.setAdapter(adapter);
            rvSubCategoryList.setLayoutManager(new LinearLayoutManager(this));
            rvSubCategoryList.addOnItemTouchListener(
                    new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(SubCategoryListActivity.this, ProductListActivity.class);
                            //TODO add sub category id in the intent
                            startActivity(intent);
                        }
                    })
            );
        }
    }

    @Override
    public void onTitleBarRightIconClicked(View view) {
        Intent intent = new Intent(SubCategoryListActivity.this, CreateSubCategoryActivity.class);
        startActivityForResult(intent, Constants.ADD_SUB_CATEGORY_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setData();
    }

}