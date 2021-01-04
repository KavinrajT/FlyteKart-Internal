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
import com.flytekart.myapplication.models.Category;
import com.flytekart.myapplication.ui.views.TitleBarLayout;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class CreateCategoryActivity extends AppCompatActivity {

    private TitleBarLayout titleBarLayout;
    private TextInputEditText etCategoryName;
    private Spinner spIsActive;
    private Button btnCreateCategory;

    private String strIsActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        titleBarLayout = findViewById(R.id.titleBar);
        titleBarLayout.setTitleText("CATEGORIES");
        titleBarLayout.removeRightImg();

        etCategoryName = findViewById(R.id.et_category_name);
        spIsActive = findViewById(R.id.sp_is_active);
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
            if (strIsActive != null) {
                category.setActive(strIsActive.equals("yes"));
            } else {
                showErrorToast(R.string.err_enter_is_active);
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

        ArrayAdapter<CharSequence> categorySpAdapter = ArrayAdapter.createFromResource(this,
                R.array.yes_no_array, android.R.layout.simple_spinner_item);
        categorySpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIsActive.setAdapter(categorySpAdapter);
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