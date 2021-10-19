package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.flytekart.models.Category;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Response;

public class CreateCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText etCategoryName;
    private SwitchCompat swIsActive;
    private Button btnCreateCategory;
    private ProgressDialog progressDialog;

    private Category category;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        etCategoryName = findViewById(R.id.et_category_name);
        swIsActive = findViewById(R.id.sw_is_active);
        btnCreateCategory = findViewById(R.id.btn_save_category);

        //etCategoryName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        btnCreateCategory.setOnClickListener(this);

        position = getIntent().getIntExtra(Constants.POSITION, -1);
        category = getIntent().getParcelableExtra(Constants.CATEGORY);
        if (category != null) {
            etCategoryName.setText(category.getName());
            swIsActive.setChecked(category.isIsActive());
            getSupportActionBar().setTitle(R.string.edit_category);
        } else {
            getSupportActionBar().setTitle(R.string.create_category);
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

    private void showErrorToast(int messageStr) {
        Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_category: {
                saveCategory();
                break;
            }
        }
    }

    private void saveCategory() {
        if (category == null) {
            category = new Category();
        }
        if (etCategoryName.getText() != null && !etCategoryName.getText().toString().isEmpty()) {
            category.setName(etCategoryName.getText().toString());
        } else {
            showErrorToast(R.string.err_enter_category_name);
            return;
        }
        category.setIsActive(swIsActive.isChecked());

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        String clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        showProgress(true);
        Call<BaseResponse<Category>> saveCategoryCall = Flytekart.getApiService().saveCategory(accessToken, clientId, category);
        saveCategoryCall.enqueue(new CustomCallback<BaseResponse<Category>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<Category>> call) {
                Logger.i("Category API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Category>> call, Response<BaseResponse<Category>> response) {
                showProgress(false);
                category = response.body().getBody();
                Toast.makeText(getApplicationContext(), "Category saved successfully.", Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra(Constants.POSITION, position);
                data.putExtra(Constants.CATEGORY, category);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Category>> call, APIError responseBody) {
                Logger.e("Category save API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showProgress(boolean show) {
        if (show) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this);
            }
            progressDialog.setMessage(getResources().getString(R.string.progress_please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}