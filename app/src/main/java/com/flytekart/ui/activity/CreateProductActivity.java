package com.flytekart.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.AttributeValueDTO;
import com.flytekart.models.Category;
import com.flytekart.models.Product;
import com.flytekart.models.Variant;
import com.flytekart.models.request.CreateProductRequest;
import com.flytekart.models.request.CreateVariantRequest;
import com.flytekart.models.request.CreateVariantVavRequest;
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

public class CreateProductActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText etProductName;
    private SwitchCompat swIsActive;
    private TextView tvMoreOptionsLabel;
    private View rlSupportVariants;
    private SwitchCompat swSupportVariants;
    private TextInputEditText etDescription;
    private View tilDescription;
    private TextInputEditText etSku;
    private View tilSku;
    private View llPrice;
    private TextInputEditText etPrice;
    private TextInputEditText etOriginalPrice;
    private Button btnSaveProduct;
    private View rlViewVariants;
    private SharedPreferences sharedPreferences;
    private String accessToken;
    private String clientId;

    private Category category;
    private Product product;
    private List<Variant> variants;

    private boolean isInStock;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        etProductName = findViewById(R.id.et_product_name);
        swIsActive = findViewById(R.id.sw_is_active);
        tvMoreOptionsLabel = findViewById(R.id.tv_more_options_label);
        rlSupportVariants = findViewById(R.id.rl_support_variants);
        swSupportVariants = findViewById(R.id.sw_support_variants);
        etDescription = findViewById(R.id.et_description);
        tilDescription = findViewById(R.id.til_description);
        etSku = findViewById(R.id.et_sku);
        tilSku = findViewById(R.id.til_sku);
        llPrice = findViewById(R.id.ll_price);
        etPrice = findViewById(R.id.et_price);
        etOriginalPrice = findViewById(R.id.et_original_price);
        btnSaveProduct = findViewById(R.id.btn_save_product);
        rlViewVariants = findViewById(R.id.rl_view_variants);

        sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        rlViewVariants.setOnClickListener(this);
        btnSaveProduct.setOnClickListener(this);
        category = getIntent().getParcelableExtra(Constants.CATEGORY);
        product = getIntent().getParcelableExtra(Constants.PRODUCT);
        if (product != null) {
            getSupportActionBar().setTitle("Edit product");
            handleUIWhenProductAvailable();
            setData();
            // TODO Show progress, get data from server and re-setData
            getData();
            // TODO Get data about variants and display more data
            getVariantsData();
        } else {
            getSupportActionBar().setTitle("Create product");
            tvMoreOptionsLabel.setVisibility(View.VISIBLE);
            rlSupportVariants.setVisibility(View.GONE);
            tilSku.setVisibility(View.GONE);
            llPrice.setVisibility(View.GONE);
            rlViewVariants.setVisibility(View.GONE);
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

    private void handleUIWhenProductAvailable() {
        tvMoreOptionsLabel.setVisibility(View.GONE);
        rlSupportVariants.setVisibility(View.GONE);
        tilSku.setVisibility(View.GONE);
        llPrice.setVisibility(View.GONE);
        rlViewVariants.setVisibility(View.GONE);
    }

    private void setData() {
        etProductName.setText(product.getName());
        etDescription.setText(product.getDescription());
        swIsActive.setChecked(product.isIsActive());
    }

    private void setVariantsData() {
        if (variants == null || variants.size() == 0) {
            rlSupportVariants.setVisibility(View.VISIBLE);
            swSupportVariants.setChecked(false);
            tilSku.setVisibility(View.VISIBLE);
            llPrice.setVisibility(View.VISIBLE);
        } else if (variants.size() == 1) {
            rlSupportVariants.setVisibility(View.VISIBLE);
            swSupportVariants.setChecked(false);
            tilSku.setVisibility(View.VISIBLE);
            etSku.setText(variants.get(0).getSku());
            llPrice.setVisibility(View.VISIBLE);
            etPrice.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variants.get(0).getPrice()));
            etOriginalPrice.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variants.get(0).getOriginalPrice()));
        } else { // variants.size() > 1
            // TODO Display variants button
            rlSupportVariants.setVisibility(View.GONE);
            rlViewVariants.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorToast(int messageStr) {
        Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_SHORT).show();
    }

    private void setProductData() {
        etProductName.setText(product.getName());
        //etPrice.setText(String.valueOf(product.getPrice()));
        //etOriginalPrice.setText(String.valueOf(product.getOriginalPrice()));
        //etDescription.setText(product.getDescription());
        //etQuantity.setText(String.valueOf(product.getQuantity()));
    }

    private void getData() {
        Call<BaseResponse<Product>> getStoresCall = Flytekart.getApiService().getProductById(accessToken, product.getId(), clientId);
        getStoresCall.enqueue(new CustomCallback<BaseResponse<Product>>() {
            @Override
            public void onFailure(Call<BaseResponse<Product>> call, Throwable t) {
                Logger.i("Product API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Product>> call, Response<BaseResponse<Product>> response) {
                product = response.body().getBody();
                setData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Product>> call, BaseErrorResponse responseBody) {
                Logger.e("Product API call  response status code : " + responseBody.getStatusCode());
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getVariantsData() {
        Call<BaseResponse<List<Variant>>> getStoresCall = Flytekart.getApiService().getVariantsByProductId(accessToken, product.getId(), clientId);
        getStoresCall.enqueue(new CustomCallback<BaseResponse<List<Variant>>>() {
            @Override
            public void onFailure(Call<BaseResponse<List<Variant>>> call, Throwable t) {
                Logger.i("Variants API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<Variant>>> call, Response<BaseResponse<List<Variant>>> response) {
                variants = response.body().getBody();
                setVariantsData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<Variant>>> call, BaseErrorResponse responseBody) {
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
                variantsIntent.putExtra(Constants.PRODUCT, product);
                startActivity(variantsIntent);
                break;
            }
            case R.id.btn_save_product: {
                saveProduct();
                break;
            }
        }
    }

    private void saveProduct() {
        if (product == null) {
            product = new Product();
            product.setCategoryId(category.getId());
        }

        product.setName(etProductName.getText().toString().trim());
        product.setDescription(etDescription.getText().toString());
        product.setIsActive(swIsActive.isChecked());

        CreateProductRequest request = new CreateProductRequest();
        request.setId(product.getId());
        request.setName(product.getName());
        request.setDescription(product.getDescription());
        request.setCategoryId(product.getCategoryId());
        request.setActive(product.isActive());

        Call<BaseResponse<Product>> saveProductCall = Flytekart.getApiService().saveProduct(accessToken, clientId, request);
        saveProductCall.enqueue(new CustomCallback<BaseResponse<Product>>() {
            @Override
            public void onFailure(Call<BaseResponse<Product>> call, Throwable t) {
                Logger.i("Save product API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Product>> call, Response<BaseResponse<Product>> response) {
                product = response.body().getBody();
                getSupportActionBar().setTitle("Edit product");
                tvMoreOptionsLabel.setVisibility(View.GONE);
                setData();
                // TODO Check if one variant is available. If yes, save the variant
                // TODO Get data about variants and display more data
                if ((variants != null && variants.size() == 1) || llPrice.getVisibility() == View.VISIBLE) {
                    saveVariant();
                } else {
                    getVariantsData();
                }
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Product>> call, BaseErrorResponse responseBody) {
                Logger.e("Save product API call  response status code : " + responseBody.getStatusCode());
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveVariant() {
        CreateVariantRequest request = new CreateVariantRequest();
        if (variants != null && variants.size() == 1) {
            Variant variant = variants.get(0);
            request.setId(variant.getId());
        }
        request.setProductId(product.getId());
        request.setName(product.getName());
        request.setActive(product.isActive());
        request.setSku(etSku.getText().toString().trim());
        String priceString = etPrice.getText().toString().trim();
        if (!priceString.isEmpty()) {
            request.setPrice(Float.parseFloat(priceString));
        }
        String originalPriceString = etOriginalPrice.getText().toString().trim();
        if (!priceString.isEmpty()) {
            request.setOriginalPrice(Float.parseFloat(originalPriceString));
        }

        Call<BaseResponse<Variant>> saveVariantCall = Flytekart.getApiService().saveVariant(accessToken, clientId, request);
        saveVariantCall.enqueue(new CustomCallback<BaseResponse<Variant>>() {
            @Override
            public void onFailure(Call<BaseResponse<Variant>> call, Throwable t) {
                Logger.i("Variant API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Variant>> call, Response<BaseResponse<Variant>> response) {
                Variant variant = response.body().getBody();
                variants = new ArrayList<>(1);
                variants.add(variant);
                setVariantsData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Variant>> call, BaseErrorResponse responseBody) {
                Logger.e("Variant API call  response status code : " + responseBody.getStatusCode());
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}