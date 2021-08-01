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
import com.flytekart.models.Product;
import com.flytekart.models.Variant;
import com.flytekart.models.response.BaseErrorResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CreateProductActivity extends AppCompatActivity implements View.OnClickListener {

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

    private Product product;
    private List<Variant> variants;

    private boolean isInStock;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        titleBarLayout = findViewById(R.id.titleBar);
        product = getIntent().getParcelableExtra(Constants.PRODUCT);
        if (product == null) {
            titleBarLayout.setTitleText("ADD PRODUCT");
        } else {
            titleBarLayout.setTitleText("EDIT PRODUCT");
        }
        titleBarLayout.removeRightImg();

        etProductName = findViewById(R.id.et_product_name);
        etPrice = findViewById(R.id.et_price);
        etOriginalPrice = findViewById(R.id.et_original_price);
        etDescription = findViewById(R.id.et_description);
        etQuantity = findViewById(R.id.et_quantity);
        spInStock = findViewById(R.id.sp_in_stock);
        swAdvancedOptions = findViewById(R.id.sw_advanced_options);
        swAdvancedInventory = findViewById(R.id.sw_advanced_inventory);
        btnCreateProduct = findViewById(R.id.btn_create_product);

        etProductName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etDescription.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        btnCreateProduct.setOnClickListener(v -> {
            Product product = new Product();
            if (etProductName.getText() != null && !etProductName.getText().toString().isEmpty()) {
                product.setName(etProductName.getText().toString());
            } else {
                showErrorToast(R.string.err_enter_product_name);
                return;
            }
            if (etPrice.getText() != null && !etPrice.getText().toString().isEmpty()) {
                //product.setPrice(Double.valueOf(etPrice.getText().toString()));
            } else {
                showErrorToast(R.string.err_enter_price);
                return;
            }
            if (etOriginalPrice.getText() != null && !etOriginalPrice.getText().toString().isEmpty()) {
                //product.setOriginalPrice(Double.valueOf(etOriginalPrice.getText().toString()));
            } else {
                showErrorToast(R.string.err_enter_original_price);
                return;
            }
            if (etDescription.getText() != null && !etDescription.getText().toString().isEmpty()) {
                //product.setDescription(etDescription.getText().toString());
            } else {
                showErrorToast(R.string.err_enter_description);
                return;
            }
            if (etQuantity.getText() != null && !etQuantity.getText().toString().isEmpty()) {
                //product.setQuantity(Double.valueOf(etQuantity.getText().toString()));
            } else {
                showErrorToast(R.string.err_enter_quantity);
                return;
            }

            //product.setInStock(isInStock);
            //product.setShowAdvanceOption(swAdvancedOptions.isChecked());
            //product.setShowAdvanceInventory(swAdvancedInventory.isChecked());

            Gson gson = new Gson();
            SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
            String productsJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_PRODUCTS, null);
            List<Product> products = gson.fromJson(productsJsonStr, new TypeToken<List<Product>>() {
            }.getType());

            if (products == null) {
                products = new ArrayList<>();
            }
            products.add(product);

            productsJsonStr = gson.toJson(products);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.SHARED_PREF_KEY_PRODUCTS, productsJsonStr);
            editor.apply();
            finish();
        });

        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(this,
                R.array.yes_no_array, android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spInStock.setAdapter(spAdapter);
        spInStock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isInStock = position == 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (product != null) {
            setProductData();
        }
    }*/

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create product");
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
        product = getIntent().getParcelableExtra(Constants.PRODUCT);
        if (product != null) {
            handleUIWhenProductAvailable();
            setData();
            // TODO Show progress, get data from server and re-setData
            getData();
            // TODO Get data about variants and display more data
            getVariantsData();
        } else {
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
            swSupportVariants.setChecked(false);
            tilSku.setVisibility(View.VISIBLE);
            llPrice.setVisibility(View.VISIBLE);
        } else if (variants.size() == 1) {
            swSupportVariants.setChecked(false);
            tilSku.setVisibility(View.VISIBLE);
            etSku.setText(variants.get(0).getSku());
            llPrice.setVisibility(View.VISIBLE);
            etPrice.setText(String.valueOf(variants.get(0).getPrice()));
            etOriginalPrice.setText(String.valueOf(variants.get(0).getPrice()));
        } else { // variants.size() > 1
            // TODO Display variants button
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
        }
    }
}