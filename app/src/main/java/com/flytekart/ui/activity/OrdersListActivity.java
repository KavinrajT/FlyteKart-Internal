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
import com.flytekart.models.OrderResponse;
import com.flytekart.models.Store;
import com.flytekart.models.response.BaseErrorResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.OrdersAdapter;
import com.flytekart.ui.views.EndlessRecyclerOnScrollListener;
import com.flytekart.ui.views.TitleBarLayout;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class OrdersListActivity extends AppCompatActivity implements TitleBarLayout.TitleBarIconClickListener {

    private LinearLayout llNoRecordsFound;
    private LinearLayoutManager ordersLayoutManager;
    private RecyclerView rvOrdersList;
    private OrdersAdapter adapter;
    private Store store;
    private List<OrderResponse> orderResponses;
    private boolean isLoadingOrders = false;
    private String accessToken;
    private String clientId;
    private int nextPageNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        TitleBarLayout titleBarLayout = findViewById(R.id.titleBar);
        titleBarLayout.setTitleText("Orders");
        titleBarLayout.setOnIconClickListener(this);
        titleBarLayout.removeRightImg();

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvOrdersList = findViewById(R.id.rv_orders_list);
        rvOrdersList.setHasFixedSize(true);
        ordersLayoutManager = new LinearLayoutManager(this);
        rvOrdersList.setLayoutManager(ordersLayoutManager);
        //rvOrdersList.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.divider_rv)));
        rvOrdersList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvOrdersList.addOnScrollListener(new EndlessRecyclerOnScrollListener(ordersLayoutManager) {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });

        store = getIntent().getParcelableExtra(Constants.STORE);

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        getData();
        setListeners();
        //setData();
    }

    private void loadMoreData() {
        if (nextPageNumber > 0
                && (nextPageNumber * Constants.DEFAULT_PAGE_SIZE) == orderResponses.size()) {
            if (!isLoadingOrders) {
                isLoadingOrders = true;
                getData();
            }
        }
    }

    private void getData() {
        Logger.e("Loading data - page = " + nextPageNumber);
        Call<BaseResponse<List<OrderResponse>>> getOrdersByStoreCall = Flytekart.getApiService().getOrdersByStoreId(accessToken, store.getId(), clientId, nextPageNumber, Constants.DEFAULT_PAGE_SIZE);
        getOrdersByStoreCall.enqueue(new CustomCallback<BaseResponse<List<OrderResponse>>>() {
            @Override
            public void onFailure(Call<BaseResponse<List<OrderResponse>>> call, Throwable t) {
                isLoadingOrders = false;
                Logger.e("Store List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<OrderResponse>>> call, Response<BaseResponse<List<OrderResponse>>> response) {
                isLoadingOrders = false;
                Logger.e("Order list API success");
                List<OrderResponse> orderResponses = response.body().getBody();
                setOrdersData(orderResponses);
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<OrderResponse>>> call, BaseErrorResponse responseBody) {
                isLoadingOrders = false;
                Logger.e("Order List API call  response status code : " + responseBody.getStatusCode());
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOrdersData(List<OrderResponse> orderResponses) {
        if ((this.orderResponses == null || this.orderResponses.isEmpty()) &&
                (orderResponses == null || orderResponses.isEmpty())) {
            rvOrdersList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(OrdersListActivity.this, CreateStoreActivity.class);
                startActivityForResult(intent, Constants.ADD_STORE_ACTIVITY_REQUEST_CODE);
            });
        } else {
            if (adapter == null) {
                llNoRecordsFound.setVisibility(View.GONE);
                rvOrdersList.setVisibility(View.VISIBLE);

                this.orderResponses = orderResponses;
                adapter = new OrdersAdapter(orderResponses);
                rvOrdersList.setAdapter(adapter);
            } else {
                int initialSize = this.orderResponses.size();
                this.orderResponses.addAll(orderResponses);
                adapter.notifyItemRangeInserted(initialSize, this.orderResponses.size() - 1);
            }
            nextPageNumber++;
        }
    }

    private void setListeners() {
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        rvOrdersList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    onOrderClicked(orderResponses.get(pos));
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
     * Take user to order details screen
     * @param orderResponse
     */
    private void onOrderClicked(OrderResponse orderResponse) {
        Intent itemIntent = new Intent(this, OrderDetailsActivity.class);
        itemIntent.putExtra(Constants.ORDER, orderResponse);
        startActivity(itemIntent);
    }

    @Override
    public void onTitleBarRightIconClicked(View view) {
        Intent intent = new Intent(OrdersListActivity.this, CreateStoreActivity.class);
        startActivityForResult(intent, Constants.ADD_STORE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //setData();
    }
}