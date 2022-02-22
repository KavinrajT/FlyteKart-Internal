package com.flytekart.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
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
import com.flytekart.models.Discount;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.DiscountsAdapter;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class DiscountListActivity extends AppCompatActivity implements DiscountsAdapter.DiscountClickListener {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvDiscountList;
    private DiscountsAdapter adapter;
    private List<Discount> discounts;
    private LinearLayoutManager discountsLayoutManager;
    private String clientId;
    private String accessToken;
    private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> createDiscountActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.discounts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvDiscountList = findViewById(R.id.rv_discount_list);
        rvDiscountList.setHasFixedSize(true);
        discountsLayoutManager = new LinearLayoutManager(this);
        rvDiscountList.setLayoutManager(discountsLayoutManager);
        rvDiscountList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        registerForActivityResults();
        getData();
        //setListeners();
        //setData();
    }

    private void registerForActivityResults() {
        createDiscountActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            updateDiscountOnCreate(result.getData());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_refresh: {
                getData();
                return true;
            }
            case R.id.menu_create: {
                Intent createDiscountIntent = new Intent(this, CreateDiscountActivity.class);
                createDiscountActivityResultLauncher.launch(createDiscountIntent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDiscountOnCreate(Intent data) {
        Discount addedDiscount = data.getParcelableExtra(Constants.DISCOUNT);
        if (addedDiscount != null) {
            if (discounts == null) {
                discounts = new ArrayList<>(10);
                discounts.add(addedDiscount);
                setDiscountsData();
            } else {
                discounts.add(addedDiscount);
                if (adapter == null) {
                    adapter = new DiscountsAdapter(this, discounts);
                }
                adapter.notifyItemInserted(discounts.size() - 1);
            }
        }
    }

    private void updateDiscountOnEdit(Intent data) {
        int position = data.getIntExtra(Constants.POSITION, -1);
        Discount editedDiscount = data.getParcelableExtra(Constants.DISCOUNT);
        if (position != -1 && editedDiscount != null) {
            discounts.remove(position);
            discounts.add(position, editedDiscount);
            adapter.notifyItemChanged(position);
        }
    }

    private void getData() {
        showProgress(true);
        Call<BaseResponse<List<Discount>>> getDiscountsCall = Flytekart.getApiService().getDiscounts(accessToken, clientId);
        getDiscountsCall.enqueue(new CustomCallback<BaseResponse<List<Discount>>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<Discount>>> call, Response<BaseResponse<List<Discount>>> response) {
                Logger.i("Categories list response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    discounts = response.body().getBody();
                    setDiscountsData();
                }
                Logger.e("Categories List API call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<Discount>>> call, APIError responseBody) {
                Logger.e("Categories List API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<List<Discount>>> call) {
                Logger.i("Categories List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDiscountsData() {
        if (discounts == null || discounts.isEmpty()) {
            rvDiscountList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(DiscountListActivity.this, CreateDiscountActivity.class);
                createDiscountActivityResultLauncher.launch(intent);
            });

        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvDiscountList.setVisibility(View.VISIBLE);

            adapter = new DiscountsAdapter(this, discounts);
            rvDiscountList.setAdapter(adapter);
        }
    }

    private void setListeners() {
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        rvDiscountList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    onDiscountClicked(pos);
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
     *
     * @param position
     */
    @Override
    public void onDiscountClicked(int position) {
        Discount discount = discounts.get(position);
        Intent itemIntent = new Intent(this, CreateDiscountActivity.class);
        itemIntent.putExtra(Constants.DISCOUNT, discount);
        startActivity(itemIntent);
    }

    /*public void onDelete(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete the category: " + discounts.get(position).getName());
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCategory(position);
            }
        });
        builder.show();
    }

    private void deleteCategory(int position) {
        Category category = discounts.get(position);
        showProgress(true);
        Call<BaseResponse<Category>> deleteCategoryCall = Flytekart.getApiService().deleteCategory(accessToken, category.getId(), clientId);
        deleteCategoryCall.enqueue(new CustomCallback<BaseResponse<Category>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Category>> call, Response<BaseResponse<Category>> response) {
                Logger.i("Delete category response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    Category deletedCategory = response.body().getBody();
                    discounts.remove(position);
                    adapter.notifyItemRemoved(position);
                    if (position <= discounts.size() - 1) {
                        adapter.notifyItemRangeChanged(position, discounts.size() - position);
                    }
                }
                Logger.e("Delete categoryAPI call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Category>> call, APIError responseBody) {
                Logger.e("Delete category API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<Category>> call) {
                Logger.i("Delete category API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }*/

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