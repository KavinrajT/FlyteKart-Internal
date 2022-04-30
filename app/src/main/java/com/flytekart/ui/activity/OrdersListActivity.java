package com.flytekart.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.OrderResponse;
import com.flytekart.models.Store;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.OrdersAdapter;
import com.flytekart.ui.views.EndlessRecyclerOnScrollListener;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.android.gms.auth.api.phone.SmsRetriever;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class OrdersListActivity extends AppCompatActivity {

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
    private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> storeActivityResultLauncher;
    private ActivityResultLauncher<Intent> orderDetailsActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.orders);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
        if (store != null && store.getName() != null) {
            getSupportActionBar().setSubtitle(store.getName());
        }

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        registerForActivityResults();
        getData();
        setListeners();
        //setData();
    }

    private void registerForActivityResults() {
        storeActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // Do nothing for now
                    }
                });

        orderDetailsActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        int position = data.getIntExtra(Constants.POSITION, 0);
                        OrderResponse orderResponse = data.getParcelableExtra(Constants.ORDER);
                        orderResponses.remove(position);
                        orderResponses.add(position, orderResponse);
                        adapter.notifyItemChanged(position);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        showProgress(true);
        Call<BaseResponse<List<OrderResponse>>> getOrdersCall;
        if (store == null) {
            getOrdersCall = Flytekart.getApiService().getOrders(accessToken, clientId, nextPageNumber, Constants.DEFAULT_PAGE_SIZE);
        } else {
            getOrdersCall = Flytekart.getApiService().getOrdersByStoreId(accessToken, store.getId(), clientId, nextPageNumber, Constants.DEFAULT_PAGE_SIZE);
        }
        getOrdersCall.enqueue(new CustomCallback<BaseResponse<List<OrderResponse>>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<List<OrderResponse>>> call) {
                isLoadingOrders = false;
                Logger.e("Store List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<OrderResponse>>> call, Response<BaseResponse<List<OrderResponse>>> response) {
                isLoadingOrders = false;
                Logger.e("Order list API success");
                showProgress(false);
                List<OrderResponse> orderResponses = response.body().getBody();
                setOrdersData(orderResponses);
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<OrderResponse>>> call, APIError responseBody) {
                isLoadingOrders = false;
                Logger.e("Order List API call  response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOrdersData(List<OrderResponse> orderResponses) {
        if ((this.orderResponses == null || this.orderResponses.isEmpty()) &&
                (orderResponses == null || orderResponses.isEmpty())) {
            rvOrdersList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            /*llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(OrdersListActivity.this, CreateStoreActivity.class);
                storeActivityResultLauncher.launch(intent);
            });*/
        } else {
            if (adapter == null) {
                llNoRecordsFound.setVisibility(View.GONE);
                rvOrdersList.setVisibility(View.VISIBLE);

                this.orderResponses = orderResponses;
                if (store == null) {
                    adapter = new OrdersAdapter(orderResponses, true);
                } else {
                    adapter = new OrdersAdapter(orderResponses, false);
                }
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
                    onOrderClicked(orderResponses.get(pos), pos);
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
    private void onOrderClicked(OrderResponse orderResponse, int position) {
        Intent itemIntent = new Intent(this, OrderDetailsActivity.class);
        itemIntent.putExtra(Constants.ORDER, orderResponse);
        itemIntent.putExtra(Constants.POSITION, position);
        orderDetailsActivityResultLauncher.launch(itemIntent);
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