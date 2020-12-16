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
import com.flytekart.myapplication.models.Store;
import com.flytekart.myapplication.ui.adapters.StoreRecyclerListAdapter;
import com.flytekart.myapplication.ui.views.TitleBarLayout;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class StoreListActivity extends AppCompatActivity implements TitleBarLayout.TitleBarIconClickListener {

    private TitleBarLayout titleBarLayout;
    private LinearLayout llNoRecordsFound;
    private RecyclerView rvStoresList;
    private StoreRecyclerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        titleBarLayout = findViewById(R.id.titleBar);
        titleBarLayout.setTitleText("STORES");
        titleBarLayout.setOnIconClickListener(this);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvStoresList = findViewById(R.id.rv_stores_list);

        setData();
    }

    private void setData() {
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String storesJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_STORES, null);
        List<Store> stores = new Gson().fromJson(storesJsonStr, new TypeToken<List<Store>>() {
        }.getType());
        if (stores == null || stores.isEmpty()) {
            rvStoresList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(StoreListActivity.this, CreateStoreActivity.class);
                startActivityForResult(intent, Constants.ADD_STORE_ACTIVITY_REQUEST_CODE);
            });

        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvStoresList.setVisibility(View.VISIBLE);

            adapter = new StoreRecyclerListAdapter(stores);
            rvStoresList.setAdapter(adapter);
            rvStoresList.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    public void onTitleBarRightIconClicked(View view) {
        Intent intent = new Intent(StoreListActivity.this, CreateStoreActivity.class);
        startActivityForResult(intent, Constants.ADD_STORE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setData();
    }
}