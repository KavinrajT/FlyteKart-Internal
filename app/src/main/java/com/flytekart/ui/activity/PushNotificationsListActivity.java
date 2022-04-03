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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.PushNotification;
import com.flytekart.models.Store;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.PushNotificationsAdapter;
import com.flytekart.ui.views.EndlessRecyclerOnScrollListener;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class PushNotificationsListActivity extends AppCompatActivity {

    private LinearLayout llNoRecordsFound;
    private LinearLayoutManager pushNotificationsLayoutManager;
    private RecyclerView rvPushNotificationsList;
    private PushNotificationsAdapter adapter;
    private Store store;
    private List<PushNotification> pushNotifications;
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
        setContentView(R.layout.activity_push_notifications_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.push_notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvPushNotificationsList = findViewById(R.id.rv_push_notifications_list);
        rvPushNotificationsList.setHasFixedSize(true);
        pushNotificationsLayoutManager = new LinearLayoutManager(this);
        rvPushNotificationsList.setLayoutManager(pushNotificationsLayoutManager);
        //rvOrdersList.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.divider_rv)));
        rvPushNotificationsList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvPushNotificationsList.addOnScrollListener(new EndlessRecyclerOnScrollListener(pushNotificationsLayoutManager) {
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
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            // Do nothing for now
                        }
                    }
                });

        orderDetailsActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            int position = data.getIntExtra(Constants.POSITION, 0);
                            PushNotification pushNotification = data.getParcelableExtra(Constants.PUSH_NOTIFICATION);
                            pushNotifications.remove(position);
                            pushNotifications.add(position, pushNotification);
                            adapter.notifyItemChanged(position);
                        }
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
                && (nextPageNumber * Constants.DEFAULT_PAGE_SIZE) == pushNotifications.size()) {
            if (!isLoadingOrders) {
                isLoadingOrders = true;
                getData();
            }
        }
    }

    private void getData() {
        Logger.e("Loading data - page = " + nextPageNumber);
        showProgress(true);
        Call<BaseResponse<List<PushNotification>>> getPushNotificationsCall =
                Flytekart.getApiService().getPushNotifications(accessToken, clientId,
                        nextPageNumber, Constants.DEFAULT_PAGE_SIZE);
        getPushNotificationsCall.enqueue(new CustomCallback<BaseResponse<List<PushNotification>>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<List<PushNotification>>> call) {
                isLoadingOrders = false;
                Logger.e("Store List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<PushNotification>>> call, Response<BaseResponse<List<PushNotification>>> response) {
                isLoadingOrders = false;
                Logger.e("PushNotification list API success");
                showProgress(false);
                List<PushNotification> pushNotifications = response.body().getBody();
                setPushNotificationsData(pushNotifications);
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<PushNotification>>> call, APIError responseBody) {
                isLoadingOrders = false;
                Logger.e("Order List API call  response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPushNotificationsData(List<PushNotification> pushNotifications) {
        if ((this.pushNotifications == null || this.pushNotifications.isEmpty()) &&
                (pushNotifications == null || pushNotifications.isEmpty())) {
            rvPushNotificationsList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(PushNotificationsListActivity.this, CreatePushNotificationActivity.class);
                storeActivityResultLauncher.launch(intent);
            });
        } else {
            if (adapter == null) {
                llNoRecordsFound.setVisibility(View.GONE);
                rvPushNotificationsList.setVisibility(View.VISIBLE);

                this.pushNotifications = pushNotifications;
                adapter = new PushNotificationsAdapter(pushNotifications);
                rvPushNotificationsList.setAdapter(adapter);
            } else {
                int initialSize = this.pushNotifications.size();
                this.pushNotifications.addAll(pushNotifications);
                adapter.notifyItemRangeInserted(initialSize, this.pushNotifications.size() - 1);
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

        rvPushNotificationsList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    onPushNotificationClicked(pushNotifications.get(pos), pos);
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
     * Take user to push notification details screen
     * @param pushNotification
     */
    private void onPushNotificationClicked(PushNotification pushNotification, int position) {
        Intent itemIntent = new Intent(this, CreatePushNotificationActivity.class);
        itemIntent.putExtra(Constants.PUSH_NOTIFICATION, pushNotification);
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