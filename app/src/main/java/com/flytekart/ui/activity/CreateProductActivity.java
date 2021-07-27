package com.flytekart.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.flytekart.R;
import com.flytekart.models.Product;
import com.flytekart.ui.views.TitleBarLayout;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class CreateProductActivity extends AppCompatActivity {

    private TitleBarLayout titleBarLayout;

    private TextInputEditText etProductName;
    private TextInputEditText etPrice;
    private TextInputEditText etOriginalPrice;
    private TextInputEditText etDescription;
    private TextInputEditText etQuantity;
    private Spinner spInStock;
    private SwitchCompat swAdvancedOptions;
    private SwitchCompat swAdvancedInventory;
    private Button btnCreateProduct;

    private Product product;

    private boolean isInStock;

    @Override
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
}