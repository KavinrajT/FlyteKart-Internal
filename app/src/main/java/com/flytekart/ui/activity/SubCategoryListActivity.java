package com.flytekart.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.SubCategory;
import com.flytekart.ui.adapters.SubCategoryRecyclerListAdapter;
import com.flytekart.ui.views.RecyclerItemClickListener;
import com.flytekart.ui.views.TitleBarLayout;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class SubCategoryListActivity extends AppCompatActivity implements TitleBarLayout.TitleBarIconClickListener {

    private TitleBarLayout titleBarLayout;
    private LinearLayout llNoRecordsFound;
    private RecyclerView rvSubCategoryList;
    private SubCategoryRecyclerListAdapter adapter;
    private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> createSubCategoryActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_list);

        titleBarLayout = findViewById(R.id.titleBar);
        titleBarLayout.setTitleText("SUB CATEGORIES");
        titleBarLayout.setOnIconClickListener(this);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvSubCategoryList = findViewById(R.id.rv_sub_category_list);

        registerForActivityResults();
        setData();
    }

    private void registerForActivityResults() {
        createSubCategoryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            setData();
                        }
                    }
                });
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
                createSubCategoryActivityResultLauncher.launch(intent);
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
        createSubCategoryActivityResultLauncher.launch(intent);
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