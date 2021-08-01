package com.flytekart.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.flytekart.R;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Utilities;
import com.flytekart.models.Category;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class CreateCategoryActivity extends AppCompatActivity {

    private TextInputEditText etCategoryName;
    private SwitchCompat swIsActive;
    private Button btnCreateCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        etCategoryName = findViewById(R.id.et_category_name);
        swIsActive = findViewById(R.id.sw_is_active);
        btnCreateCategory = findViewById(R.id.btn_create_category);

        etCategoryName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        btnCreateCategory.setOnClickListener(v -> {
            Category category = new Category();
            if (etCategoryName.getText() != null && !etCategoryName.getText().toString().isEmpty()) {
                category.setName(etCategoryName.getText().toString());
            } else {
                showErrorToast(R.string.err_enter_category_name);
                return;
            }

            Gson gson = new Gson();
            SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
            String categoriesJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CATEGORIES, null);
            List<Category> categories = gson.fromJson(categoriesJsonStr, new TypeToken<List<Category>>() {
            }.getType());

            if (categories == null) {
                categories = new ArrayList<>();
            }
            categories.add(category);

            categoriesJsonStr = gson.toJson(categories);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.SHARED_PREF_KEY_CATEGORIES, categoriesJsonStr);
            editor.apply();
            finish();
        });
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

    private void showErrorToast(int messageStr) {
        Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_SHORT).show();
    }

}