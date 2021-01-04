package com.flytekart.myapplication.ui.activity;

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

import com.flytekart.myapplication.R;
import com.flytekart.myapplication.models.SubCategory;
import com.flytekart.myapplication.ui.views.TitleBarLayout;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class CreateSubCategoryActivity extends AppCompatActivity {

    private TitleBarLayout titleBarLayout;
    private TextInputEditText etSubCategoryName;
    private Spinner spIsActive;
    private Button btnCreateSubCategory;

    private String strIsActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sub_category);

        titleBarLayout = findViewById(R.id.titleBar);
        titleBarLayout.setTitleText("SUB CATEGORIES");
        titleBarLayout.removeRightImg();

        etSubCategoryName = findViewById(R.id.et_sub_category_name);
        spIsActive = findViewById(R.id.sp_is_active);
        btnCreateSubCategory = findViewById(R.id.btn_create_sub_category);

        etSubCategoryName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        btnCreateSubCategory.setOnClickListener(v -> {
            SubCategory subCategory = new SubCategory();
            if (etSubCategoryName.getText() != null && !etSubCategoryName.getText().toString().isEmpty()) {
                subCategory.setName(etSubCategoryName.getText().toString());
            } else {
                showErrorToast(R.string.err_enter_sub_category_name);
                return;
            }
            if (strIsActive != null) {
                subCategory.setActive(strIsActive.equals("yes"));
            } else {
                showErrorToast(R.string.err_enter_is_active);
                return;
            }

            Gson gson = new Gson();
            SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
            String subCategoriesJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_SUB_CATEGORIES, null);
            List<SubCategory> subCategories = gson.fromJson(subCategoriesJsonStr, new TypeToken<List<SubCategory>>() {
            }.getType());

            if (subCategories == null) {
                subCategories = new ArrayList<>();
            }
            subCategories.add(subCategory);

            subCategoriesJsonStr = gson.toJson(subCategories);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.SHARED_PREF_KEY_SUB_CATEGORIES, subCategoriesJsonStr);
            editor.apply();
            finish();
        });

        ArrayAdapter<CharSequence> subCategorySpAdapter = ArrayAdapter.createFromResource(this,
                R.array.yes_no_array, android.R.layout.simple_spinner_item);
        subCategorySpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIsActive.setAdapter(subCategorySpAdapter);
        spIsActive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strIsActive = getResources().getStringArray(R.array.yes_no_array)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showErrorToast(int messageStr) {
        Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_SHORT).show();
    }
}