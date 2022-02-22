package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.Category;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.models.response.FileUploadResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.FileUtils;
import com.flytekart.utils.Logger;
import com.flytekart.utils.PhotoUtils;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class CreateDiscountActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText etCategoryName;
    private SwitchCompat swIsActive;
    private Button btnCreateDiscount;
    private ImageView ivCategoryImage;
    private ProgressDialog progressDialog;
    private ImagePicker imagePicker;
    private CameraImagePicker cameraImagePicker;

    private Category category;
    private String selectedImageUri;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_discount);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        etCategoryName = findViewById(R.id.et_category_name);
        swIsActive = findViewById(R.id.sw_is_active);
        btnCreateDiscount = findViewById(R.id.btn_save_discount);
        ivCategoryImage = findViewById(R.id.iv_category_img);

        //etCategoryName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        btnCreateDiscount.setOnClickListener(this);

        position = getIntent().getIntExtra(Constants.POSITION, -1);
        category = getIntent().getParcelableExtra(Constants.CATEGORY);
        if (category != null) {
            etCategoryName.setText(category.getName());
            swIsActive.setChecked(category.isIsActive());
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty())
                new Picasso.Builder(this).build().load(category.getImageUrl())
                        .resize(Constants.IMAGE_MAX_DIM, Constants.IMAGE_MAX_DIM).onlyScaleDown()
                        .centerCrop().placeholder(R.drawable.ic_photo_camera).into(ivCategoryImage);
            getSupportActionBar().setTitle(R.string.edit_category);
        } else {
            getSupportActionBar().setTitle(R.string.create_category);
        }

        ivCategoryImage.setOnClickListener(v -> {
            showImageDialog();
        });
    }

    private void showImageDialog() {
        AlertDialog.Builder pickerDialog = new AlertDialog.Builder(this);
        pickerDialog.setTitle(R.string.str_choose_image);
        pickerDialog.setPositiveButton(R.string.str_gallery,
                (dialog, which) -> {
                    pickImage(true);
                    dialog.dismiss();
                });
        pickerDialog.setNegativeButton(R.string.str_camera,
                (dialog, which) -> {
                    pickImage(false);
                    dialog.dismiss();
                });
        pickerDialog.show();
    }

    private void pickImage(boolean isDevice) {
        ImagePickerCallback imagePickerCallback = new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> images) {
                String originalPath = images.get(0).getOriginalPath();
                String uri = FileUtils.getUriFromPath(originalPath);
                File imageFile = PhotoUtils.compressImage(CreateDiscountActivity.this, uri, 0.8f);
                selectedImageUri = FileUtils.getUriFromPath(imageFile.getAbsolutePath());
                new Picasso.Builder(CreateDiscountActivity.this).build().load(selectedImageUri)
                        .resize(Constants.IMAGE_MAX_DIM, Constants.IMAGE_MAX_DIM).onlyScaleDown()
                        .centerCrop().placeholder(R.drawable.ic_photo_camera).into(ivCategoryImage);
            }

            @Override
            public void onError(String message) {
                Logger.e(message);
            }
        };
        imagePicker = new ImagePicker(this);
        imagePicker.setCacheLocation(CacheLocation.INTERNAL_APP_DIR);
        cameraImagePicker = new CameraImagePicker(this);
        if (isDevice) {
            imagePicker.setImagePickerCallback(imagePickerCallback);
            imagePicker.pickImage();
        } else {
            cameraImagePicker.setImagePickerCallback(imagePickerCallback);
            cameraImagePicker.pickImage();
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
                if (selectedImageUri == null) {
                    saveCategory();
                } else {
                    uploadCategoryImage();
                }
                break;
            }
        }
    }

    public void uploadCategoryImage() {
        Uri uri = Uri.parse(selectedImageUri);
        File file = FileUtils.getFile(getApplicationContext(), uri);
        getApplicationContext().getContentResolver().getType(uri);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), fileBody);
        MultipartBody body = builder.build();

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        String clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        showProgress(true);
        Call<FileUploadResponse> uploadImageCall = Flytekart.getApiService().uploadFile(accessToken, clientId, body);
        uploadImageCall.enqueue(new CustomCallback<>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<FileUploadResponse> call) {
                Logger.i("Category image upload API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                showProgress(false);
                if (response != null && response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getBody();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        if (category == null) {
                            category = new Category();
                        }
                        category.setImageUrl(imageUrl);
                        saveCategory();
                    }
                }
            }

            @Override
            public void onFlytekartErrorResponse(Call<FileUploadResponse> call, APIError responseBody) {
                Logger.e("Category image upload API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        saveCategoryCall.enqueue(new CustomCallback<>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<Category>> call) {
                Logger.i("Category API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE && imagePicker != null) {
                imagePicker.submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA && cameraImagePicker != null) {
                cameraImagePicker.submit(data);
            }
        }
    }
}