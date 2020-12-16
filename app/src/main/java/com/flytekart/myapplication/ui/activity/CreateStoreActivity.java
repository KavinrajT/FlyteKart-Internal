package com.flytekart.myapplication.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flytekart.myapplication.R;
import com.flytekart.myapplication.models.Store;
import com.flytekart.myapplication.ui.views.TitleBarLayout;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class CreateStoreActivity extends AppCompatActivity {

    private TitleBarLayout titleBarLayout;

    private TextInputEditText etStoreName;
    private Button btnLocateMe;
    private TextView tvLocation;
    private TextInputEditText etGstNo;
    private Button btnCreateStore;

    private String strAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);

        titleBarLayout = findViewById(R.id.titleBar);
        titleBarLayout.setTitleText("ADD STORE");
        titleBarLayout.removeRightImg();

        etStoreName = findViewById(R.id.et_store_name);
        btnLocateMe = findViewById(R.id.btn_locate_me);
        tvLocation = findViewById(R.id.tv_location);
        etGstNo = findViewById(R.id.et_gst_no);
        btnCreateStore = findViewById(R.id.btn_create_store);

        etStoreName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        btnLocateMe.setOnClickListener(arg0 -> {
            Intent intent = new Intent(CreateStoreActivity.this, MapsActivity.class);
            startActivityForResult(intent, Constants.MAPS_ACTIVITY_REQUEST_CODE);
        });

        btnCreateStore.setOnClickListener(v -> {
            Store store = new Store();
            if (etStoreName.getText() != null && !etStoreName.getText().toString().isEmpty()) {
                store.setName(etStoreName.getText().toString());
            } else {
                showErrorToast(R.string.err_enter_store_name);
                return;
            }
            if (strAddress != null) {
                store.setAddress(strAddress);
            } else {
                showErrorToast(R.string.err_add_address);
                return;
            }
            if (etGstNo.getText() != null && !etGstNo.getText().toString().isEmpty()) {
                store.setGstNo(etGstNo.getText().toString());
            } else {
                showErrorToast(R.string.err_enter_gst_no);
                return;
            }

            Gson gson = new Gson();

            SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
            String storesJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_STORES, null);
            List<Store> stores = gson.fromJson(storesJsonStr, new TypeToken<List<Store>>() {
            }.getType());

            if (stores == null) {
                stores = new ArrayList<>();
            }
            stores.add(store);

            storesJsonStr = gson.toJson(stores);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.SHARED_PREF_KEY_STORES, storesJsonStr);
            editor.apply();
            finish();
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
}