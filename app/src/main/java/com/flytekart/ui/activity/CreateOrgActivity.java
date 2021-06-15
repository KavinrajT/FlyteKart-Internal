package com.flytekart.ui.activity;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.R;
import com.flytekart.models.ApiCallResponse;
import com.flytekart.models.BaseResponse;
import com.flytekart.models.Organisation;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrgActivity extends AppCompatActivity {

    private TextInputEditText etOrgName;
    private Spinner spStoreType;
    private Button btnLocateMe;
    private TextView tvLocation;
    private Spinner spBusinessType;
    private TextInputEditText etGstNo;
    private Button btnCreateOrg;

    private String strStoreType;
    private String strBusinessType;
    private String strAddress;


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
            startActivityForResult(intent, Constants.MAPS_ACTIVITY_REQUEST_CODE);
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
        Call<BaseResponse<Organisation>> loginCall = com.flytekart.Flytekart.getApiService().createOrganisation(accessToken, organisation);
        loginCall.enqueue(new Callback<BaseResponse<Organisation>>() {
            @Override
            public void onResponse(@NotNull Call<BaseResponse<Organisation>> call, @NotNull Response<BaseResponse<Organisation>> response) {
                Logger.i("create Org API call response received.");
                Logger.i("create Org API call response received.");
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Organisation> orgResponse = response.body();
                    // Get dropdown data and go to next screen.
                    Toast.makeText(getApplicationContext(), orgResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    if (orgResponse.isSuccess()) {
                        Logger.i("create Org API call success.");
                        finish();
                    }
                } else if (response.errorBody() != null) {
                    try {
                        ApiCallResponse apiCallResponse = new Gson().fromJson(
                                response.errorBody().string(), ApiCallResponse.class);
                        Toast.makeText(getApplicationContext(), apiCallResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Logger.e("create Org API call  response status code : " + response.code());
                // populateFragment();
            }

            @Override
            public void onFailure(@NotNull Call<BaseResponse<Organisation>> call, @NotNull Throwable t) {
                Logger.i("Organisation List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}