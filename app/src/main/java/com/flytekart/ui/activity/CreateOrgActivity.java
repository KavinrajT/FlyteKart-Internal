package com.flytekart.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.R;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.models.Organisation;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class CreateOrgActivity extends AppCompatActivity {

    private TextInputEditText etOrgName;
    private Spinner spStoreType;
    private Button btnLocateMe;
    private TextView tvLocation;
    private Spinner spBusinessType;
    private TextInputEditText etGstNo;
    private Button btnCreateOrg;
    private ProgressDialog progressDialog;

    private String strStoreType;
    private String strBusinessType;
    private String strAddress;
    private ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_org);

        etOrgName = findViewById(R.id.et_org_name);
        spStoreType = findViewById(R.id.sp_store_type);
        btnLocateMe = findViewById(R.id.btn_locate_me);
        tvLocation = findViewById(R.id.tv_location);
        spBusinessType = findViewById(R.id.sp_business_type);
        etGstNo = findViewById(R.id.et_gst_no);
        btnCreateOrg = findViewById(R.id.btn_create_org);

        etOrgName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        btnLocateMe.setOnClickListener(arg0 -> {
            Intent intent = new Intent(CreateOrgActivity.this, MapsActivity.class);
            activityResultLauncher.launch(intent);
        });

        btnCreateOrg.setOnClickListener(v -> {
            Organisation organisation = new Organisation();
            if (etOrgName.getText() != null && !etOrgName.getText().toString().isEmpty()) {
                organisation.setName(etOrgName.getText().toString());
            } else {
                showErrorToast(R.string.err_enter_org_name);
                return;
            }
            if (strStoreType != null) {
                organisation.setStoreType(strStoreType);
            } else {
                showErrorToast(R.string.err_enter_store_type);
                return;
            }
            if (strAddress != null) {
                organisation.setAddress(strAddress);
            } else {
                showErrorToast(R.string.err_add_address);
                return;
            }
            if (strBusinessType != null) {
                organisation.setBusinessType(strBusinessType);
            } else {
                showErrorToast(R.string.err_enter_business_type);
                return;
            }
            if (etGstNo.getText() != null && !etGstNo.getText().toString().isEmpty()) {
                organisation.setGstNo(etGstNo.getText().toString());
            } else {
                showErrorToast(R.string.err_enter_gst_no);
                return;
            }

            /*Gson gson = new Gson();
            String orgJsonStr = gson.toJson(organisation);
            SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.SHARED_PREF_KEY_ORGANISATION, orgJsonStr);
            editor.apply();
            finish();*/

            createOrganisation(organisation);
        });

        ArrayAdapter<CharSequence> storeSpAdapter = ArrayAdapter.createFromResource(this,
                R.array.store_type_array, android.R.layout.simple_spinner_item);
        storeSpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStoreType.setAdapter(storeSpAdapter);
        spStoreType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strStoreType = getResources().getStringArray(R.array.store_type_array)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> businessSpAdapter = ArrayAdapter.createFromResource(this,
                R.array.business_type_array, android.R.layout.simple_spinner_item);
        businessSpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBusinessType.setAdapter(businessSpAdapter);
        spBusinessType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strBusinessType = getResources().getStringArray(R.array.business_type_array)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        registerForActivityResults();
    }

    private void registerForActivityResults() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            strAddress = result.getData().getStringExtra("address");
                            tvLocation.setVisibility(View.VISIBLE);
                            tvLocation.setText(strAddress);
                        }
                    }
                });
    }

    private void showErrorToast(int messageStr) {
        Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.MAPS_ACTIVITY_REQUEST_CODE && data != null) {
            strAddress = data.getStringExtra("address");
            tvLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(strAddress);
        }
    }


    private void createOrganisation(Organisation organisation) {
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, "");
        showProgress(true);
        Call<BaseResponse<Organisation>> loginCall = com.flytekart.Flytekart.getApiService().createOrganisation(accessToken, organisation);
        loginCall.enqueue(new CustomCallback<BaseResponse<Organisation>>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<BaseResponse<Organisation>> call, @NotNull Response<BaseResponse<Organisation>> response) {
                Logger.i("create Org API call response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Organisation> orgResponse = response.body();
                    // Get dropdown data and go to next screen.
                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                }
                Logger.e("create Org API call  response status code : " + response.code());
                // populateFragment();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Organisation>> call, APIError responseBody) {
                Logger.e("Organisation List API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<Organisation>> call) {
                Logger.i("Organisation List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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