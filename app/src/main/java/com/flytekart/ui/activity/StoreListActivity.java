package com.flytekart.ui.activity;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.Store;
import com.flytekart.models.response.BaseErrorResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.StoresAdapter;
import com.flytekart.ui.views.DividerItemDecoration;
import com.flytekart.ui.views.TitleBarLayout;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class StoreListActivity extends AppCompatActivity implements TitleBarLayout.TitleBarIconClickListener {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvStoresList;
    private StoresAdapter adapter;
    private List<Store> stores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        /*TitleBarLayout titleBarLayout = findViewById(R.id.titleBar);
        titleBarLayout.setTitleText("Stores");
        titleBarLayout.setOnIconClickListener(this);*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Stores");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvStoresList = findViewById(R.id.rv_stores_list);
        rvStoresList.setHasFixedSize(true);
        rvStoresList.setLayoutManager(new LinearLayoutManager(this));
        rvStoresList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_rv)));

        getData();
        setListeners();
        //setData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getData() {
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        String clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        Call<BaseResponse<List<Store>>> getStoresCall = Flytekart.getApiService().getStoresByOrg(accessToken, clientId);
        getStoresCall.enqueue(new CustomCallback<BaseResponse<List<Store>>>() {
            @Override
            public void onFailure(Call<BaseResponse<List<Store>>> call, Throwable t) {
                Logger.i("Store List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<Store>>> call, Response<BaseResponse<List<Store>>> response) {
                stores = response.body().getBody();
                setStoresData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<Store>>> call, BaseErrorResponse responseBody) {
                Logger.e("Store List API call  response status code : " + responseBody.getStatusCode());
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStoresData() {
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

            adapter = new StoresAdapter(stores);
            rvStoresList.setAdapter(adapter);
            rvStoresList.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void setListeners() {
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        rvStoresList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    onStoreClicked(stores.get(pos));
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

    private void onStoreClicked(Store store) {
        Intent itemIntent = new Intent(this, StoreActivity.class);
        itemIntent.putExtra(Constants.STORE, store);
        startActivity(itemIntent);
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

            adapter = new StoresAdapter(stores);
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