package com.flytekart.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class StoreActivity extends AppCompatActivity implements TitleBarLayout.TitleBarIconClickListener, View.OnClickListener {

    private TextView tvStoreDetails;
    private TextView tvSummary;
    private TextView tvOrders;
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        TitleBarLayout titleBarLayout = findViewById(R.id.titleBar);
        // TODO Change the title to store name
        titleBarLayout.setTitleText("Store");
        titleBarLayout.removeRightImg();
        titleBarLayout.setOnIconClickListener(this);

        tvStoreDetails = findViewById(R.id.tv_store_details);
        tvSummary = findViewById(R.id.tv_summary);
        tvOrders = findViewById(R.id.tv_orders);

        tvStoreDetails.setOnClickListener(this);
        tvSummary.setOnClickListener(this);
        tvOrders.setOnClickListener(this);

        store = getIntent().getParcelableExtra(Constants.STORE);
    }

    @Override
    public void onTitleBarRightIconClicked(View view) {
        Intent intent = new Intent(StoreActivity.this, CreateStoreActivity.class);
        startActivityForResult(intent, Constants.ADD_STORE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_store_details:
                break;
            case R.id.tv_summary:
                break;
            case R.id.tv_orders:
                Intent ordersIntent = new Intent(this, OrdersListActivity.class);
                ordersIntent.putExtra(Constants.STORE, store);
                startActivity(ordersIntent);
                break;
        }
    }
}