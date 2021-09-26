package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.Attribute;
import com.flytekart.models.AttributeValueDTO;
import com.flytekart.models.Product;
import com.flytekart.models.Variant;
import com.flytekart.models.VariantAttributeValue;
import com.flytekart.models.request.CreateVariantVavRequest;
import com.flytekart.models.request.DeleteVariantAttributeValueRequest;
import com.flytekart.models.response.BaseErrorResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CreateVariantActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etVariantName;
    private SwitchCompat swIsActive;
    private TextInputEditText etSku;
    private TextInputEditText etPrice;
    private TextInputEditText etTax;
    private TextInputEditText etOriginalPrice;
    private LinearLayout llAttributeValues;
    private LayoutInflater layoutInflater;
    private ArrayAdapter<String> attributeNameAdapter;
    MaterialAutoCompleteTextView etAttributeName;
    private ProgressDialog progressDialog;

    private String accessToken;
    private String clientId;
    private Variant variant;
    private Product product;
    private List<VariantAttributeValue> variantAttributeValues;
    private List<String> attributes;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_variant);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        etVariantName = findViewById(R.id.et_variant_name);
        swIsActive = findViewById(R.id.sw_is_active);
        etSku = findViewById(R.id.et_sku);
        etPrice = findViewById(R.id.et_price);
        etTax = findViewById(R.id.et_tax);
        etOriginalPrice = findViewById(R.id.et_original_price);
        Button btnSaveVariant = findViewById(R.id.btn_save_variant);
        View llAddAttributeValues = findViewById(R.id.ll_add_attribute_values);
        llAttributeValues = findViewById(R.id.ll_attribute_values);
        attributes = new ArrayList<>(5);
        attributeNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attributes);

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        layoutInflater = LayoutInflater.from(this);

        llAddAttributeValues.setOnClickListener(this);
        btnSaveVariant.setOnClickListener(this);
        variant = getIntent().getParcelableExtra(Constants.VARIANT);
        product = getIntent().getParcelableExtra(Constants.PRODUCT);
        if (variant != null) {
            getSupportActionBar().setTitle("Edit variant");
            setData();
            // TODO Show progress, get data from server and re-setData
            getData();
            // TODO Get data about variantAttributeValues and display
            getVariantAttributeValuesData();
        } else {
            getSupportActionBar().setTitle("Create variant");
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

    private void setData() {
        etVariantName.setText(variant.getName());
        swIsActive.setChecked(variant.isActive());
        etSku.setText(variant.getSku());
        etPrice.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variant.getPrice()));
        etOriginalPrice.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variant.getOriginalPrice()));
    }

    private void setVariantAttributeValuesData() {
        if (variantAttributeValues != null) {
            for (VariantAttributeValue value : variantAttributeValues) {
                View v = layoutInflater.inflate(R.layout.list_item_attribute_value, null);

                TextView tvAttributeName = v.findViewById(R.id.tv_attribute_name);
                TextView tvAttributeValue = v.findViewById(R.id.tv_attribute_value);
                View ivAttributeValueDelete = v.findViewById(R.id.iv_attribute_value_delete);

                tvAttributeName.setText(value.getAttributeValue().getAttribute().getName());
                tvAttributeValue.setText(value.getAttributeValue().getName());
                ivAttributeValueDelete.setOnClickListener(this);

                AttributeValueDTO attributeValueDTO = new AttributeValueDTO();
                attributeValueDTO.setVariantAttributeValueId(value.getId());
                attributeValueDTO.setAttributeValueId(value.getAttributeValue().getId());
                attributeValueDTO.setAttributeName(value.getAttributeValue().getAttribute().getName());
                attributeValueDTO.setAttributeValueName(value.getAttributeValue().getName());
                v.setTag(attributeValueDTO);
                ivAttributeValueDelete.setTag(attributeValueDTO);

                llAttributeValues.addView(v);
            }
        }
    }

    private void showErrorToast(int messageStr) {
        Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_SHORT).show();
    }

    private void getData() {
        showProgress(true);
        Call<BaseResponse<Variant>> getVariantCall = Flytekart.getApiService().getVariantById(accessToken, variant.getId(), clientId);
        getVariantCall.enqueue(new CustomCallback<BaseResponse<Variant>>() {
            @Override
            public void onFailure(Call<BaseResponse<Variant>> call, Throwable t) {
                Logger.i("Variant API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Variant>> call, Response<BaseResponse<Variant>> response) {
                showProgress(false);
                variant = response.body().getBody();
                setData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Variant>> call, BaseErrorResponse responseBody) {
                Logger.e("Variant API call  response status code : " + responseBody.getStatusCode());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getVariantAttributeValuesData() {
        showProgress(true);
        Call<BaseResponse<List<VariantAttributeValue>>> getStoresCall = Flytekart.getApiService().getAttributeValuesByVariantId(accessToken, variant.getId(), clientId);
        getStoresCall.enqueue(new CustomCallback<BaseResponse<List<VariantAttributeValue>>>() {
            @Override
            public void onFailure(Call<BaseResponse<List<VariantAttributeValue>>> call, Throwable t) {
                Logger.i("Variants API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<VariantAttributeValue>>> call, Response<BaseResponse<List<VariantAttributeValue>>> response) {
                showProgress(false);
                variantAttributeValues = response.body().getBody();
                setVariantAttributeValuesData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<VariantAttributeValue>>> call, BaseErrorResponse responseBody) {
                Logger.e("Variants API call  response status code : " + responseBody.getStatusCode());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_add_attribute_values: {
                showAttributeDialog(null);
                break;
            }
            case R.id.btn_save_variant: {
                saveVariantAlongWithAttributeValue();
                break;
            }
            case R.id.iv_attribute_value_delete: {
                AttributeValueDTO dto = (AttributeValueDTO) v.getTag();
                deleteAttributeValue(dto);
                break;
            }
        }
    }

    private void deleteAttributeValue(AttributeValueDTO dto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete attribute: " + dto.getAttributeName() + " - " + dto.getAttributeValueName() + "?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ignore
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dto.getVariantAttributeValueId() != null) {
                    // Delete from server and remove item from UI
                    deleteAttribute(dto);
                } else {
                    // Remove item from UI
                    int foundIndex = 0;
                    for (int i = 0; i < llAttributeValues.getChildCount(); i++) {
                        View v = llAttributeValues.getChildAt(i);
                        AttributeValueDTO savedAttributeValueDTO = (AttributeValueDTO) v.getTag();
                        if (savedAttributeValueDTO.getAttributeName().equalsIgnoreCase(dto.getAttributeName())) {
                            foundIndex = i;
                            break;
                        }
                    }
                    llAttributeValues.removeView(llAttributeValues.getChildAt(foundIndex));
                }
            }
        });
        builder.show();
    }

    private void deleteAttribute(AttributeValueDTO dto) {
        DeleteVariantAttributeValueRequest request = new DeleteVariantAttributeValueRequest();
        request.setId(dto.getVariantAttributeValueId());
        showProgress(true);
        Call<BaseResponse<VariantAttributeValue>> saveVariantCall = Flytekart.getApiService().deleteVariantAttributeValue(accessToken, clientId, request);
        saveVariantCall.enqueue(new CustomCallback<BaseResponse<VariantAttributeValue>>() {
            @Override
            public void onFailure(Call<BaseResponse<VariantAttributeValue>> call, Throwable t) {
                Logger.i("Variant API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<VariantAttributeValue>> call, Response<BaseResponse<VariantAttributeValue>> response) {
                showProgress(false);
                VariantAttributeValue vav = response.body().getBody();
                int foundIndex = 0;
                for (int i = 0; i < llAttributeValues.getChildCount(); i++) {
                    View v = llAttributeValues.getChildAt(i);
                    AttributeValueDTO savedAttributeValueDTO = (AttributeValueDTO) v.getTag();
                    if (savedAttributeValueDTO.getAttributeName().equals(dto.getAttributeName())) {
                        foundIndex = i;
                        break;
                    }
                }
                llAttributeValues.removeView(llAttributeValues.getChildAt(foundIndex));
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<VariantAttributeValue>> call, BaseErrorResponse responseBody) {
                Logger.e("Variant API call  response status code : " + responseBody.getStatusCode());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAttributeDialog(View selectedView) {
        View dialogView = layoutInflater.inflate(R.layout.dialog_attribute_value, null);
        etAttributeName = dialogView.findViewById(R.id.et_attribute_name);
        EditText etAttributeValueName = dialogView.findViewById(R.id.et_attribute_value_name);
        etAttributeName.addTextChangedListener(new AttributeNamePrefixListener());
        etAttributeName.setAdapter(attributeNameAdapter);
        //etAttributeValueName.addTextChangedListener(new AttributeValueNamePrefixListener());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String attributeName = etAttributeName.getText().toString().trim();
                String attributeValueName = etAttributeValueName.getText().toString().trim();

                if (!attributeName.equals(Constants.EMPTY) && !attributeValueName.equals(Constants.EMPTY)) {
                    if (selectedView != null) {
                        TextView tvAttributeName = selectedView.findViewById(R.id.tv_attribute_name);
                        TextView tvAttributeValue = selectedView.findViewById(R.id.tv_attribute_value);

                        tvAttributeName.setText(attributeName);
                        tvAttributeValue.setText(attributeValueName);
                    } else {
                        View v = layoutInflater.inflate(R.layout.list_item_attribute_value, null);

                        TextView tvAttributeName = v.findViewById(R.id.tv_attribute_name);
                        TextView tvAttributeValue = v.findViewById(R.id.tv_attribute_value);
                        View ivAttributeValueDelete = v.findViewById(R.id.iv_attribute_value_delete);

                        tvAttributeName.setText(attributeName);
                        tvAttributeValue.setText(attributeValueName);
                        ivAttributeValueDelete.setOnClickListener(CreateVariantActivity.this);

                        AttributeValueDTO attributeValueDTO = new AttributeValueDTO();
                        attributeValueDTO.setAttributeName(attributeName);
                        attributeValueDTO.setAttributeValueName(attributeValueName);
                        v.setTag(attributeValueDTO);
                        ivAttributeValueDelete.setTag(attributeValueDTO);

                        llAttributeValues.addView(v);
                    }
                } else {
                    Toast.makeText(CreateVariantActivity.this, "Please enter valid data.", Toast.LENGTH_SHORT).show();
                }
                etAttributeName = null;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Ignore the click
                etAttributeName = null;
            }
        });

        builder.show();
    }

    /**
     * 1. Get attribute and attributeValue names from llAttributeValues
     * 2. Send attribute and attributeValue names along with variant details
     */
    private void saveVariantAlongWithAttributeValue() {
        List<AttributeValueDTO> attributeValueDTOs = new ArrayList<>(llAttributeValues.getChildCount());
        for (int i = 0; i < llAttributeValues.getChildCount(); i++) {
            View v = llAttributeValues.getChildAt(i);
            AttributeValueDTO attributeValueDTO = (AttributeValueDTO) v.getTag();
            attributeValueDTOs.add(attributeValueDTO);
        }

        CreateVariantVavRequest request = new CreateVariantVavRequest();
        if (variant != null) {
            request.setId(variant.getId());
        }
        request.setProductId(product.getId());
        request.setName(etVariantName.getText().toString().trim());
        request.setActive(swIsActive.isChecked());
        request.setSku(etSku.getText().toString().trim());
        String priceString = etPrice.getText().toString().trim();
        if (!priceString.isEmpty()) {
            request.setPrice(Double.parseDouble(priceString));
        }
        String taxString = etTax.getText().toString().trim();
        if (!taxString.isEmpty()) {
            request.setTax(Double.parseDouble(taxString));
        }
        String originalPriceString = etOriginalPrice.getText().toString().trim();
        if (!priceString.isEmpty()) {
            request.setOriginalPrice(Double.parseDouble(originalPriceString));
        }
        request.setAttributeValueDTOs(attributeValueDTOs);

        showProgress(true);
        Call<BaseResponse<Variant>> saveVariantCall = Flytekart.getApiService().saveVariantVav(accessToken, clientId, request);
        saveVariantCall.enqueue(new CustomCallback<BaseResponse<Variant>>() {
            @Override
            public void onFailure(Call<BaseResponse<Variant>> call, Throwable t) {
                Logger.i("Variant API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Variant>> call, Response<BaseResponse<Variant>> response) {
                showProgress(false);
                variant = response.body().getBody();
                setData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Variant>> call, BaseErrorResponse responseBody) {
                Logger.e("Variant API call  response status code : " + responseBody.getStatusCode());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class AttributeNamePrefixListener implements TextWatcher {
        String currentTest;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            currentTest = charSequence.toString();
            if (charSequence.length() > 2) {
                Call<BaseResponse<List<Attribute>>> getAttributesCall = Flytekart.getApiService().getAttributesByPrefix(accessToken, charSequence.toString(), clientId);
                getAttributesCall.enqueue(new CustomCallback<BaseResponse<List<Attribute>>>() {
                    @Override
                    public void onFailure(Call<BaseResponse<List<Attribute>>> call, Throwable t) {
                        Logger.i("Variant API call failure.");
                        Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFlytekartSuccessResponse(Call<BaseResponse<List<Attribute>>> call, Response<BaseResponse<List<Attribute>>> response) {
                        if (currentTest.equals(charSequence.toString())) {
                            List<Attribute> attributes = response.body().getBody();
                            setAttributesToSpinner(attributes);
                        }
                    }

                    @Override
                    public void onFlytekartErrorResponse(Call<BaseResponse<List<Attribute>>> call, BaseErrorResponse responseBody) {
                        Logger.e("Variant API call  response status code : " + responseBody.getStatusCode());
                        Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void setAttributesToSpinner(List<Attribute> attributes) {
        List<String> attributeNames = new ArrayList<>(attributes.size());
        for (Attribute attribute : attributes) {
            attributeNames.add(attribute.getName());
        }
        this.attributes.clear();
        this.attributes.addAll(attributeNames);
        attributeNameAdapter.clear();
        attributeNameAdapter.addAll(this.attributes);
        attributeNameAdapter.notifyDataSetChanged();
    }

    private class AttributeValueNamePrefixListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
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