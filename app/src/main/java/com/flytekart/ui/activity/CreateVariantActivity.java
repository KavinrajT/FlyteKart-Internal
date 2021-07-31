package com.flytekart.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.flytekart.models.AttributeValueDTO;
import com.flytekart.models.Product;
import com.flytekart.models.Variant;
import com.flytekart.models.VariantAttributeValue;
import com.flytekart.models.request.CreateVariantRequest;
import com.flytekart.models.response.BaseErrorResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CreateVariantActivity extends AppCompatActivity implements View.OnClickListener {

    /*private TitleBarLayout titleBarLayout;

    private TextInputEditText etProductName;
    private TextInputEditText etPrice;
    private TextInputEditText etOriginalPrice;
    private TextInputEditText etDescription;
    private TextInputEditText etQuantity;
    private Spinner spInStock;
    private SwitchCompat swAdvancedOptions;
    private SwitchCompat swAdvancedInventory;
    private Button btnCreateProduct;*/

    private EditText etVariantName;
    private SwitchCompat swIsActive;
    private TextInputEditText etSku;
    private TextInputEditText etPrice;
    private TextInputEditText etOriginalPrice;
    private Button btnSaveVariant;
    private View llAddAttributeValues;
    private LinearLayout llAttributeValues;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;

    private String accessToken;
    private String clientId;
    private Variant variant;
    private Product product;
    private List<VariantAttributeValue> variantAttributeValues;

    private boolean isInStock;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_variant);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create variant");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        etVariantName = findViewById(R.id.et_variant_name);
        swIsActive = findViewById(R.id.sw_is_active);
        etSku = findViewById(R.id.et_sku);
        etPrice = findViewById(R.id.et_price);
        etOriginalPrice = findViewById(R.id.et_original_price);
        btnSaveVariant = findViewById(R.id.btn_save_variant);
        llAddAttributeValues = findViewById(R.id.ll_add_attribute_values);
        llAttributeValues = findViewById(R.id.ll_attribute_values);

        sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        layoutInflater = LayoutInflater.from(this);

        llAddAttributeValues.setOnClickListener(this);
        btnSaveVariant.setOnClickListener(this);
        variant = getIntent().getParcelableExtra(Constants.VARIANT);
        product = getIntent().getParcelableExtra(Constants.PRODUCT);
        if (variant != null) {
            setData();
            // TODO Show progress, get data from server and re-setData
            getData();
            // TODO Get data about variantAttributeValues and display
            getVariantAttributeValuesData();
        }
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

    private void setData() {
        etVariantName.setText(variant.getName());
        swIsActive.setChecked(variant.isActive());
        etSku.setText(variant.getSku());
        etPrice.setText(Utilities.getFormattedMoney(variant.getPrice()));
        etOriginalPrice.setText(Utilities.getFormattedMoney(variant.getOriginalPrice()));
    }

    private void setVariantAttributeValuesData() {
        if (variantAttributeValues != null) {
            for (VariantAttributeValue value : variantAttributeValues) {
                View v = layoutInflater.inflate(R.layout.list_item_attribute_value, null);

                TextView tvAttributeName = v.findViewById(R.id.tv_attribute_name);
                TextView tvAttributeValue = v.findViewById(R.id.tv_attribute_value);

                tvAttributeName.setText(value.getAttributeValue().getAttribute().getName());
                tvAttributeValue.setText(value.getAttributeValue().getName());

                AttributeValueDTO attributeValueDTO = new AttributeValueDTO();
                attributeValueDTO.setAttributeValueId(value.getAttributeValue().getId());
                attributeValueDTO.setAttributeName(value.getAttributeValue().getAttribute().getName());
                attributeValueDTO.setAttributeValueName(value.getAttributeValue().getName());
                v.setTag(attributeValueDTO);

                llAttributeValues.addView(v);
            }
        }
    }

    private void showErrorToast(int messageStr) {
        Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_SHORT).show();
    }

    private void setProductData() {
        etVariantName.setText(variant.getName());
        //etPrice.setText(String.valueOf(product.getPrice()));
        //etOriginalPrice.setText(String.valueOf(product.getOriginalPrice()));
        //etDescription.setText(product.getDescription());
        //etQuantity.setText(String.valueOf(product.getQuantity()));
    }

    private void getData() {
        Call<BaseResponse<Variant>> getVariantCall = Flytekart.getApiService().getVariantById(accessToken, variant.getId(), clientId);
        getVariantCall.enqueue(new CustomCallback<BaseResponse<Variant>>() {
            @Override
            public void onFailure(Call<BaseResponse<Variant>> call, Throwable t) {
                Logger.i("Variant API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Variant>> call, Response<BaseResponse<Variant>> response) {
                variant = response.body().getBody();
                setData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Variant>> call, BaseErrorResponse responseBody) {
                Logger.e("Variant API call  response status code : " + responseBody.getStatusCode());
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getVariantAttributeValuesData() {
        Call<BaseResponse<List<VariantAttributeValue>>> getStoresCall = Flytekart.getApiService().getAttributeValuesByVariantId(accessToken, variant.getId(), clientId);
        getStoresCall.enqueue(new CustomCallback<BaseResponse<List<VariantAttributeValue>>>() {
            @Override
            public void onFailure(Call<BaseResponse<List<VariantAttributeValue>>> call, Throwable t) {
                Logger.i("Variants API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<VariantAttributeValue>>> call, Response<BaseResponse<List<VariantAttributeValue>>> response) {
                variantAttributeValues = response.body().getBody();
                setVariantAttributeValuesData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<VariantAttributeValue>>> call, BaseErrorResponse responseBody) {
                Logger.e("Variants API call  response status code : " + responseBody.getStatusCode());
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_view_variants: {
                Intent variantsIntent = new Intent(this, VariantListActivity.class);
                variantsIntent.putExtra(Constants.PRODUCT, variant);
                startActivity(variantsIntent);
            }
            case R.id.ll_add_attribute_values: {
                showAttributeDialog(null);
            }
        }
    }

    private void showAttributeDialog(View selectedView) {
        View dialogView = layoutInflater.inflate(R.layout.dialog_attribute_value, null);
        EditText etAttributeName = dialogView.findViewById(R.id.et_attribute_name);
        EditText etAttributeValueName = dialogView.findViewById(R.id.et_attribute_value_name);
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

                        tvAttributeName.setText(attributeName);
                        tvAttributeValue.setText(attributeValueName);

                        AttributeValueDTO attributeValueDTO = new AttributeValueDTO();
                        attributeValueDTO.setAttributeName(attributeName);
                        attributeValueDTO.setAttributeValueName(attributeValueName);
                        v.setTag(attributeValueDTO);

                        llAttributeValues.addView(v);
                    }
                } else {
                    Toast.makeText(CreateVariantActivity.this, "Please enter valid data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Ignore the click
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

        CreateVariantRequest request = new CreateVariantRequest();
        if (variant != null) {
            request.setVariantId(variant.getId());
        }
        request.setProductId(product.getId());
        request.setName(etVariantName.getText().toString().trim());
        request.setActive(swIsActive.isChecked());
        request.setSku(etSku.getText().toString().trim());
        String priceString = etPrice.getText().toString().trim();
        if (!priceString.isEmpty()) {
            request.setPrice(Float.parseFloat(priceString));
        }
        String originalPriceString = etOriginalPrice.getText().toString().trim();
        if (!priceString.isEmpty()) {
            request.setOriginalPrice(Float.parseFloat(originalPriceString));
        }
        request.setAttributeValueDTOs(attributeValueDTOs);

        // TODO Write a new method for saving
        // TODO Call the new method and save data
    }
}