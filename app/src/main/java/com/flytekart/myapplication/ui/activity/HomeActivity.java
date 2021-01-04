package com.flytekart.myapplication.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.flytekart.myapplication.R;
import com.flytekart.myapplication.models.Organisation;
import com.flytekart.myapplication.models.Product;
import com.flytekart.myapplication.models.Store;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class HomeActivity extends BaseActivity {

    private Button btnCreateOrg;
    private Button btnCreateStore;
    private Button btnCreateProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        btnCreateOrg = findViewById(R.id.btn_create_org);
        btnCreateStore = findViewById(R.id.btn_add_stores);
        btnCreateProduct = findViewById(R.id.btn_add_products);
        Button btnClearAll = findViewById(R.id.btn_clear_all);

        btnCreateOrg.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, CreateOrgActivity.class);
            startActivityForResult(intent, Constants.CREATE_ORG_ACTIVITY_REQUEST_CODE);
        });

        btnCreateStore.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, StoreListActivity.class);
            startActivityForResult(intent, Constants.STORE_LIST_ACTIVITY_REQUEST_CODE);
        });

        btnCreateProduct.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
            startActivityForResult(intent, Constants.ADD_PRODUCT_ACTIVITY_REQUEST_CODE);
        });

        btnClearAll.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Constants.SHARED_PREF_KEY_ORGANISATION);
            editor.remove(Constants.SHARED_PREF_KEY_STORES);
            editor.remove(Constants.SHARED_PREF_KEY_PRODUCTS);
            editor.apply();
            setData();
        });

        setData();
    }

    private void setData() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();

        String orgJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ORGANISATION, null);
        Organisation organisation = gson.fromJson(orgJsonStr, Organisation.class);
        if (organisation != null) {
            setButtonBackground(btnCreateOrg, R.drawable.selector_green_button_done);
            if (TextUtils.equals(organisation.getStoreType(),
                    getResources().getStringArray(R.array.store_type_array)[0])) {
                setButtonBackground(btnCreateStore, R.drawable.selector_green_button_done);
            } else {
                btnCreateStore.setEnabled(true);
            }
        } else {
            setButtonBackground(btnCreateOrg, R.drawable.selector_red_button_num_1);
            btnCreateOrg.setEnabled(true);
            btnCreateStore.setEnabled(false);
            btnCreateProduct.setEnabled(false);
            return;
        }

        String storesJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_STORES, null);
        List<Store> stores = gson.fromJson(storesJsonStr, new TypeToken<List<Store>>() {
        }.getType());
        if (stores != null && !stores.isEmpty()) {
            setButtonBackground(btnCreateStore, R.drawable.selector_green_button_done);
            btnCreateProduct.setEnabled(true);
        } else {
            setButtonBackground(btnCreateStore, R.drawable.selector_red_button_num_2);
            btnCreateStore.setEnabled(true);
            btnCreateProduct.setEnabled(false);
            return;
        }

        String productsJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_PRODUCTS, null);
        List<Product> products = gson.fromJson(productsJsonStr, new TypeToken<List<Product>>() {
        }.getType());
        if (products != null && !products.isEmpty()) {
            setButtonBackground(btnCreateProduct, R.drawable.selector_green_button_done);
        } else {
            setButtonBackground(btnCreateProduct, R.drawable.selector_red_button_num_3);
            btnCreateProduct.setEnabled(true);
        }
    }

    private void setButtonBackground(View view, int drawable) {
        view.setBackground(ResourcesCompat.getDrawable(getResources(), drawable, getTheme()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setData();
    }
}