package com.flytekart.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.ProductStoreProductDTO;
import com.flytekart.models.Store;
import com.flytekart.models.StoreVariant;
import com.flytekart.models.Variant;
import com.flytekart.models.VariantStoreVariantDTO;
import com.flytekart.models.request.CreateStoreVariantRequest;
import com.flytekart.models.request.CreateVariantVavRequest;
import com.flytekart.models.response.BaseErrorResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Response;

public class StoreVariantActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etVariantName;
    private SwitchCompat swIsActive;
    private TextInputEditText etSku;
    private TextInputEditText etPrice;
    private TextInputEditText etOriginalPrice;
    private TextInputEditText etQuantity;
    private TextInputEditText etTax;

    private String accessToken;
    private String clientId;
    private Store store;
    private VariantStoreVariantDTO variantStoreVariantDTO;
    private ProductStoreProductDTO product;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_variant);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        etVariantName = findViewById(R.id.et_variant_name);
        swIsActive = findViewById(R.id.sw_is_active);
        etSku = findViewById(R.id.et_sku);
        etPrice = findViewById(R.id.et_price);
        etOriginalPrice = findViewById(R.id.et_original_price);
        etQuantity = findViewById(R.id.et_quantity);
        etTax = findViewById(R.id.et_tax);
        Button btnSaveVariant = findViewById(R.id.btn_save_variant);

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        btnSaveVariant.setOnClickListener(this);
        store = getIntent().getParcelableExtra(Constants.STORE);
        variantStoreVariantDTO = getIntent().getParcelableExtra(Constants.VARIANT);
        product = getIntent().getParcelableExtra(Constants.PRODUCT);
        getSupportActionBar().setTitle(product.getName());
        getSupportActionBar().setSubtitle(store.getName());
        setData();
        // TODO Show progress, get data from server and re-setData
        getData();
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
        etVariantName.setText(variantStoreVariantDTO.getName());
        swIsActive.setChecked(variantStoreVariantDTO.isStoreVariantIsActive());
        etSku.setText(variantStoreVariantDTO.getSku());

        if (variantStoreVariantDTO.getStoreVariantPrice() != null) {
            etPrice.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variantStoreVariantDTO.getStoreVariantPrice()));
        } else if (variantStoreVariantDTO.getPrice() != null) {
            etPrice.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variantStoreVariantDTO.getPrice()));
        }

        if (variantStoreVariantDTO.getStoreVariantOriginalPrice() != null) {
            etOriginalPrice.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variantStoreVariantDTO.getStoreVariantOriginalPrice()));
        } else if (variantStoreVariantDTO.getOriginalPrice() != null) {
            etOriginalPrice.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variantStoreVariantDTO.getOriginalPrice()));
        }

        if (variantStoreVariantDTO.getStoreVariantTax() != null) {
            etTax.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variantStoreVariantDTO.getStoreVariantTax()));
        } else if (variantStoreVariantDTO.getTax() != null) {
            etTax.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variantStoreVariantDTO.getTax()));
        }
        if (variantStoreVariantDTO.getStoreVariantQuantity() != null) {
            etQuantity.setText(String.valueOf(variantStoreVariantDTO.getStoreVariantQuantity()));
        }
    }

    private void showErrorToast(int messageStr) {
        Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_SHORT).show();
    }

    private void getData() {
        if (variantStoreVariantDTO.getStoreVariantId() != null) {
            Call<BaseResponse<VariantStoreVariantDTO>> getStoreVariantCall = Flytekart.getApiService().getStoreVariant(accessToken, variantStoreVariantDTO.getStoreVariantId(), clientId);
            getStoreVariantCall.enqueue(new CustomCallback<BaseResponse<VariantStoreVariantDTO>>() {
                @Override
                public void onFailure(Call<BaseResponse<VariantStoreVariantDTO>> call, Throwable t) {
                    Logger.i("Variant API call failure.");
                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFlytekartSuccessResponse(Call<BaseResponse<VariantStoreVariantDTO>> call, Response<BaseResponse<VariantStoreVariantDTO>> response) {
                    variantStoreVariantDTO = response.body().getBody();
                    setData();
                }

                @Override
                public void onFlytekartErrorResponse(Call<BaseResponse<VariantStoreVariantDTO>> call, BaseErrorResponse responseBody) {
                    Logger.e("Variant API call  response status code : " + responseBody.getStatusCode());
                    Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_variant: {
                saveStoreVariant();
                break;
            }
        }
    }

    private void saveStoreVariant() {
        CreateStoreVariantRequest request = new CreateStoreVariantRequest();
        if (variantStoreVariantDTO.getStoreVariantId() != null) {
            request.setStoreVariantId(variantStoreVariantDTO.getStoreVariantId());
        }
        request.setStoreId(store.getId());
        request.setVariantId(variantStoreVariantDTO.getId());
        String quantityString = etQuantity.getText().toString().trim();
        String priceString = etPrice.getText().toString().trim();
        if (priceString.length() > 0) {
            request.setPrice(Double.parseDouble(priceString));
        } else {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }
        String taxString = etTax.getText().toString().trim();
        if (taxString.length() > 0) {
            request.setTax(Double.parseDouble(taxString));
        } else {
            Toast.makeText(this, "Please enter a valid tax", Toast.LENGTH_SHORT).show();
            return;
        }
        String originalPriceString = etOriginalPrice.getText().toString().trim();
        if (originalPriceString.length() > 0) {
            request.setOriginalPrice(Double.parseDouble(originalPriceString));
        } else {
            request.setOriginalPrice(null);
        }
        if (quantityString.length() > 0) {
            request.setQuantity(Integer.parseInt(quantityString));
        } else {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }
        request.setActive(swIsActive.isChecked());

        Call<BaseResponse<StoreVariant>> saveVariantCall = Flytekart.getApiService().saveStoreVariant(accessToken, clientId, request);
        saveVariantCall.enqueue(new CustomCallback<BaseResponse<StoreVariant>>() {
            @Override
            public void onFailure(Call<BaseResponse<StoreVariant>> call, Throwable t) {
                Logger.i("Store variant API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<StoreVariant>> call, Response<BaseResponse<StoreVariant>> response) {
                StoreVariant storeVariant = response.body().getBody();
                variantStoreVariantDTO.setStoreVariantId(storeVariant.getId());
                Toast.makeText(getApplicationContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                getData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<StoreVariant>> call, BaseErrorResponse responseBody) {
                Logger.e("Store variant API call  response status code : " + responseBody.getStatusCode());
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}