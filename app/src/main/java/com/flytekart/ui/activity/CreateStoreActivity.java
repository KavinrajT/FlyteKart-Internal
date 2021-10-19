package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.Address;
import com.flytekart.models.Store;
import com.flytekart.models.request.CreateStoreRequest;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Response;

public class CreateStoreActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText etStoreName;
    private Button btnLocateMe;
    private TextView tvLocation;
    private TextInputEditText etGstNo;
    private Button btnSaveStore;
    private int position;
    private Store store;
    private Address storeAddress;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.add_store);

        etStoreName = findViewById(R.id.et_store_name);
        btnLocateMe = findViewById(R.id.btn_locate_me);
        tvLocation = findViewById(R.id.tv_location);
        etGstNo = findViewById(R.id.et_gst_no);
        btnSaveStore = findViewById(R.id.btn_save_store);

        etStoreName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        btnLocateMe.setOnClickListener(arg0 -> {
            openMaps();
        });

        btnSaveStore.setOnClickListener(v -> {
            saveStore();
        });

        position = getIntent().getIntExtra(Constants.POSITION, -1);
        store = getIntent().getParcelableExtra(Constants.STORE);
        if (store != null) {
            etStoreName.setText(store.getName());
            etGstNo.setText(store.getTaxNumber());
            tvLocation.setVisibility(View.VISIBLE);
            storeAddress = store.getAddress();
            StringBuilder builder = new StringBuilder();
            builder.append(storeAddress.getLine1()).append(Constants.COMMA_SPACE);
            builder.append(storeAddress.getLine2()).append(Constants.COMMA_SPACE);
            builder.append(storeAddress.getCity()).append(Constants.COMMA_SPACE);
            builder.append(storeAddress.getState()).append(Constants.COMMA_SPACE);
            builder.append(storeAddress.getCountry()).append(Constants.COMMA_SPACE);
            builder.append(storeAddress.getZip());
            tvLocation.setText(builder.toString());
            getSupportActionBar().setTitle(R.string.edit_store);
        } else {
            getSupportActionBar().setTitle(R.string.add_store);
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_locate_me: {
                openMaps();
                break;
            }
            case R.id.btn_save_store: {
                saveStore();
                break;
            }
        }
    }

    private void showErrorToast(int messageStr) {
        Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.MAPS_ACTIVITY_REQUEST_CODE && data != null) {
            storeAddress = data.getParcelableExtra(Constants.ADDRESS);
            tvLocation.setVisibility(View.VISIBLE);
            StringBuilder builder = new StringBuilder();
            builder.append(storeAddress.getLine1()).append(Constants.COMMA_SPACE);
            builder.append(storeAddress.getLine2()).append(Constants.COMMA_SPACE);
            builder.append(storeAddress.getCity()).append(Constants.COMMA_SPACE);
            builder.append(storeAddress.getState()).append(Constants.COMMA_SPACE);
            builder.append(storeAddress.getCountry()).append(Constants.COMMA_SPACE);
            builder.append(storeAddress.getZip());
            tvLocation.setText(builder.toString());
        }
    }

    private void openMaps() {
        Intent intent = new Intent(CreateStoreActivity.this, MapsActivity.class);
        intent.putExtra(Constants.ADDRESS, storeAddress);
        startActivityForResult(intent, Constants.MAPS_ACTIVITY_REQUEST_CODE);
    }

    private void saveStore() {
        if (store == null) {
            store = new Store();
        }
        if (etStoreName.getText() != null && !etStoreName.getText().toString().isEmpty()) {
            store.setName(etStoreName.getText().toString());
        } else {
            showErrorToast(R.string.err_enter_store_name);
            return;
        }
        if (storeAddress != null) {
            //store.setAddress(strAddress);
        } else {
            showErrorToast(R.string.err_add_address);
            return;
        }
        if (etGstNo.getText() != null && !etGstNo.getText().toString().isEmpty()) {
            store.setTaxNumber(etGstNo.getText().toString());
        } else {
            showErrorToast(R.string.err_enter_gst_no);
            return;
        }

        CreateStoreRequest request = new CreateStoreRequest();
        request.setId(store.getId());
        request.setName(store.getName());
        request.setTaxNumber(store.getTaxNumber());
        request.setAddress(storeAddress);

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        String clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        showProgress(true);
        Call<BaseResponse<Store>> getStoresCall = Flytekart.getApiService().saveStore(accessToken, clientId, request);
        getStoresCall.enqueue(new CustomCallback<BaseResponse<Store>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<Store>> call) {
                Logger.i("Save store API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Store>> call, Response<BaseResponse<Store>> response) {
                showProgress(false);
                store = response.body().getBody();
                storeAddress = store.getAddress();
                Toast.makeText(getApplicationContext(), "Store information saved successfully.", Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra(Constants.POSITION, position);
                data.putExtra(Constants.STORE, store);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Store>> call, APIError responseBody) {
                Logger.e("Save store API call  response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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