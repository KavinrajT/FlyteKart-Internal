package com.flytekart.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.Attribute;
import com.flytekart.models.AttributeValue;
import com.flytekart.models.AttributeValueDTO;
import com.flytekart.models.Product;
import com.flytekart.models.Variant;
import com.flytekart.models.VariantAttributeValue;
import com.flytekart.models.request.CreateVariantVavRequest;
import com.flytekart.models.request.DeleteVariantAttributeValueRequest;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.AttributeResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.models.response.FileUploadResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.views.DecimalDigitsInputFilter;
import com.flytekart.utils.Constants;
import com.flytekart.utils.FileUtils;
import com.flytekart.utils.Logger;
import com.flytekart.utils.PhotoUtils;
import com.flytekart.utils.Utilities;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class CreateVariantActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etVariantName;
    private SwitchCompat swIsActive;
    private TextInputEditText etSku;
    private TextInputEditText etPrice;
    private TextInputEditText etTax;
    private TextInputEditText etOriginalPrice;
    private LinearLayout llAttributeValues;
    private LayoutInflater layoutInflater;
    private ArrayAdapter<String> attributeNameAdapter;
    private ArrayAdapter<String> attributeValueNameAdapter;
    MaterialAutoCompleteTextView etAttributeName;
    private ImageView ivVariantImage;
    private ProgressDialog progressDialog;
    private ImagePicker imagePicker;
    private CameraImagePicker cameraImagePicker;

    private String accessToken;
    private String clientId;
    private Variant variant;
    private Product product;
    private String selectedImageUri;
    private List<VariantAttributeValue> variantAttributeValues;
    private List<String> attributesStrings;
    private List<String> attributeValuesStrings;
    private List<AttributeResponse> attributeResponses;
    private int position;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_variant);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        etVariantName = findViewById(R.id.et_variant_name);
        swIsActive = findViewById(R.id.sw_is_active);
        etSku = findViewById(R.id.et_sku);

        etPrice = findViewById(R.id.et_price);
        etPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etTax = findViewById(R.id.et_tax);
        etTax.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        etOriginalPrice = findViewById(R.id.et_original_price);
        etOriginalPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});

        ivVariantImage = findViewById(R.id.iv_variant_img);

        Button btnSaveVariant = findViewById(R.id.btn_save_variant);
        View llAddAttributeValues = findViewById(R.id.ll_add_attribute_values);
        llAttributeValues = findViewById(R.id.ll_attribute_values);
        attributesStrings = new ArrayList<>(5);
        attributeNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attributesStrings);
        attributeValuesStrings = new ArrayList<>(5);
        attributeValueNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attributeValuesStrings);

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        layoutInflater = LayoutInflater.from(this);

        llAddAttributeValues.setOnClickListener(this);
        btnSaveVariant.setOnClickListener(this);
        variant = getIntent().getParcelableExtra(Constants.VARIANT);
        product = getIntent().getParcelableExtra(Constants.PRODUCT);
        position = getIntent().getIntExtra(Constants.POSITION, -1);
        if (variant != null) {
            getSupportActionBar().setTitle("Edit variant");
            setData();
            // TODO Show progress, get data from server and re-setData
            getData();
            // TODO Get data about variantAttributeValues and display
            getVariantAttributeValuesData();
        } else {
            getSupportActionBar().setTitle("Create variant");
        }
        getSupportActionBar().setSubtitle(product.getName());

        ivVariantImage.setOnClickListener(v -> {
            if (checkPermissions()) {
                showImageDialog();
            }
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
                File imageFile = PhotoUtils.compressImage(CreateVariantActivity.this, uri, 0.8f);
                selectedImageUri = FileUtils.getUriFromPath(imageFile.getAbsolutePath());
                new Picasso.Builder(CreateVariantActivity.this).build().load(selectedImageUri)
                        .resize(Constants.IMAGE_MAX_DIM, Constants.IMAGE_MAX_DIM).onlyScaleDown()
                        .centerCrop().placeholder(R.drawable.ic_photo_camera).into(ivVariantImage);
            }

            @Override
            public void onError(String message) {
                Logger.e(message);
            }
        };
        imagePicker = new ImagePicker(this);
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent data = new Intent();
        data.putExtra(Constants.POSITION, position);
        data.putExtra(Constants.VARIANT, variant);
        setResult(RESULT_OK, data);
        finish();
    }

    private void setData() {
        etVariantName.setText(variant.getName());
        swIsActive.setChecked(variant.isActive());
        etSku.setText(variant.getSku());
        if (variant.getPrice() != null) {
            etPrice.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variant.getPrice()));
        } else {
            etPrice.setText(Constants.EMPTY);
        }
        if (variant.getOriginalPrice() != null) {
            etOriginalPrice.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variant.getOriginalPrice()));
        } else {
            etOriginalPrice.setText(Constants.EMPTY);
        }
        if (variant.getTax() != null) {
            etTax.setText(Utilities.getFormattedMoneyWithoutCurrencyCode(variant.getTax()));
        } else {
            etTax.setText(Constants.EMPTY);
        }
        if (variant.getImageUrl() != null && !variant.getImageUrl().isEmpty()) {
            new Picasso.Builder(this).build().load(variant.getImageUrl())
                    .resize(Constants.IMAGE_MAX_DIM, Constants.IMAGE_MAX_DIM).onlyScaleDown()
                    .centerCrop().placeholder(R.drawable.ic_photo_camera).into(ivVariantImage);
        }
    }

    private void setVariantAttributeValuesData() {
        if (variantAttributeValues != null) {
            for (VariantAttributeValue value : variantAttributeValues) {
                View v = layoutInflater.inflate(R.layout.list_item_attribute_value, null);

                TextView tvAttributeName = v.findViewById(R.id.tv_attribute_name);
                TextView tvAttributeValue = v.findViewById(R.id.tv_attribute_value);
                View ivAttributeValueDelete = v.findViewById(R.id.iv_attribute_value_delete);

                tvAttributeName.setText(value.getAttributeValue().getAttribute().getName());
                tvAttributeValue.setText(value.getAttributeValue().getName());
                ivAttributeValueDelete.setOnClickListener(this);

                AttributeValueDTO attributeValueDTO = new AttributeValueDTO();
                attributeValueDTO.setVariantAttributeValueId(value.getId());
                attributeValueDTO.setAttributeValueId(value.getAttributeValue().getId());
                attributeValueDTO.setAttributeName(value.getAttributeValue().getAttribute().getName());
                attributeValueDTO.setAttributeValueName(value.getAttributeValue().getName());
                v.setTag(attributeValueDTO);
                ivAttributeValueDelete.setTag(attributeValueDTO);

                llAttributeValues.addView(v);
            }
        }
    }

    private void showErrorToast(int messageStr) {
        Toast.makeText(getApplicationContext(), messageStr, Toast.LENGTH_SHORT).show();
    }

    private void getData() {
        showProgress(true);
        Call<BaseResponse<Variant>> getVariantCall = Flytekart.getApiService().getVariantById(accessToken, variant.getId(), clientId);
        getVariantCall.enqueue(new CustomCallback<BaseResponse<Variant>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<Variant>> call) {
                Logger.i("Variant API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Variant>> call, Response<BaseResponse<Variant>> response) {
                showProgress(false);
                variant = response.body().getBody();
                setData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Variant>> call, APIError responseBody) {
                Logger.e("Variant API call  response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getVariantAttributeValuesData() {
        showProgress(true);
        Call<BaseResponse<List<VariantAttributeValue>>> getStoresCall = Flytekart.getApiService().getAttributeValuesByVariantId(accessToken, variant.getId(), clientId);
        getStoresCall.enqueue(new CustomCallback<BaseResponse<List<VariantAttributeValue>>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<List<VariantAttributeValue>>> call) {
                Logger.i("Variants API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<VariantAttributeValue>>> call, Response<BaseResponse<List<VariantAttributeValue>>> response) {
                showProgress(false);
                variantAttributeValues = response.body().getBody();
                setVariantAttributeValuesData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<VariantAttributeValue>>> call, APIError responseBody) {
                Logger.e("Variants API call  response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_add_attribute_values: {
                getAllAttributeValuesByAttributes();
                break;
            }
            case R.id.btn_save_variant: {
                if (selectedImageUri == null) {
                    saveVariantAlongWithAttributeValue(null);
                } else {
                    uploadVariantImage();
                }
                break;
            }
            case R.id.iv_attribute_value_delete: {
                AttributeValueDTO dto = (AttributeValueDTO) v.getTag();
                deleteAttributeValue(dto);
                break;
            }
        }
    }

    private void getAllAttributeValuesByAttributes() {
        showProgress(true);
        Call<BaseResponse<List<AttributeResponse>>> getAttributeValuesCall = Flytekart.getApiService().getAllAttributeValues(accessToken, clientId);
        getAttributeValuesCall.enqueue(new CustomCallback<BaseResponse<List<AttributeResponse>>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<List<AttributeResponse>>> call) {
                Logger.i("Variant API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<AttributeResponse>>> call,
                                                   Response<BaseResponse<List<AttributeResponse>>> response) {
                showProgress(false);
                attributeResponses = response.body().getBody();
                showAttributeDialog(null);
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<AttributeResponse>>> call,
                                                 APIError responseBody) {
                Logger.e("Variant API call  response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAttributeValue(AttributeValueDTO dto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete attribute: " + dto.getAttributeName() + " - " + dto.getAttributeValueName() + "?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ignore
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dto.getVariantAttributeValueId() != null) {
                    // Delete from server and remove item from UI
                    deleteAttribute(dto);
                } else {
                    // Remove item from UI
                    int foundIndex = 0;
                    for (int i = 0; i < llAttributeValues.getChildCount(); i++) {
                        View v = llAttributeValues.getChildAt(i);
                        AttributeValueDTO savedAttributeValueDTO = (AttributeValueDTO) v.getTag();
                        if (savedAttributeValueDTO.getAttributeName().equalsIgnoreCase(dto.getAttributeName())) {
                            foundIndex = i;
                            break;
                        }
                    }
                    llAttributeValues.removeView(llAttributeValues.getChildAt(foundIndex));
                }
            }
        });
        builder.show();
    }

    private void deleteAttribute(AttributeValueDTO dto) {
        DeleteVariantAttributeValueRequest request = new DeleteVariantAttributeValueRequest();
        request.setId(dto.getVariantAttributeValueId());
        showProgress(true);
        Call<BaseResponse<VariantAttributeValue>> saveVariantCall = Flytekart.getApiService().deleteVariantAttributeValue(accessToken, clientId, request);
        saveVariantCall.enqueue(new CustomCallback<BaseResponse<VariantAttributeValue>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<VariantAttributeValue>> call) {
                Logger.i("Variant API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<VariantAttributeValue>> call, Response<BaseResponse<VariantAttributeValue>> response) {
                showProgress(false);
                VariantAttributeValue vav = response.body().getBody();
                int foundIndex = 0;
                for (int i = 0; i < llAttributeValues.getChildCount(); i++) {
                    View v = llAttributeValues.getChildAt(i);
                    AttributeValueDTO savedAttributeValueDTO = (AttributeValueDTO) v.getTag();
                    if (savedAttributeValueDTO.getAttributeName().equals(dto.getAttributeName())) {
                        foundIndex = i;
                        break;
                    }
                }
                llAttributeValues.removeView(llAttributeValues.getChildAt(foundIndex));
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<VariantAttributeValue>> call, APIError responseBody) {
                Logger.e("Variant API call  response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAttributeDialog(View selectedView) {
        View dialogView = layoutInflater.inflate(R.layout.dialog_attribute_value, null);
        etAttributeName = dialogView.findViewById(R.id.et_attribute_name);
        MaterialAutoCompleteTextView etAttributeValueName = dialogView.findViewById(R.id.et_attribute_value_name);
        etAttributeName.addTextChangedListener(new AttributeNamePrefixListener());
        etAttributeName.setAdapter(attributeNameAdapter);
        etAttributeValueName.addTextChangedListener(new AttributeValueNamePrefixListener());
        etAttributeValueName.setAdapter(attributeValueNameAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String attributeName = etAttributeName.getText().toString().trim();
                String attributeValueName = etAttributeValueName.getText().toString().trim();

                if (!attributeName.equals(Constants.EMPTY) && !attributeValueName.equals(Constants.EMPTY)) {
                    if (selectedView != null) {
                        TextView tvAttributeName = selectedView.findViewById(R.id.tv_attribute_name);
                        TextView tvAttributeValue = selectedView.findViewById(R.id.tv_attribute_value);

                        tvAttributeName.setText(attributeName);
                        tvAttributeValue.setText(attributeValueName);
                    } else {
                        View v = layoutInflater.inflate(R.layout.list_item_attribute_value, null);

                        TextView tvAttributeName = v.findViewById(R.id.tv_attribute_name);
                        TextView tvAttributeValue = v.findViewById(R.id.tv_attribute_value);
                        View ivAttributeValueDelete = v.findViewById(R.id.iv_attribute_value_delete);

                        tvAttributeName.setText(attributeName);
                        tvAttributeValue.setText(attributeValueName);
                        ivAttributeValueDelete.setOnClickListener(CreateVariantActivity.this);

                        AttributeValueDTO attributeValueDTO = new AttributeValueDTO();
                        attributeValueDTO.setAttributeName(attributeName);
                        attributeValueDTO.setAttributeValueName(attributeValueName);
                        v.setTag(attributeValueDTO);
                        ivAttributeValueDelete.setTag(attributeValueDTO);

                        llAttributeValues.addView(v);
                    }
                } else {
                    Toast.makeText(CreateVariantActivity.this, "Please enter valid data.", Toast.LENGTH_SHORT).show();
                }
                etAttributeName = null;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Ignore the click
                etAttributeName = null;
            }
        });

        builder.show();
    }

    public void uploadVariantImage() {
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
                Logger.i("Variant image upload API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                showProgress(false);
                if (response != null && response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getBody();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        saveVariantAlongWithAttributeValue(imageUrl);
                    }
                }
            }

            @Override
            public void onFlytekartErrorResponse(Call<FileUploadResponse> call, APIError responseBody) {
                Logger.e("Variant image upload API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 1. Get attribute and attributeValue names from llAttributeValues
     * 2. Send attribute and attributeValue names along with variant details
     */
    private void saveVariantAlongWithAttributeValue(String variantImageUrl) {
        List<AttributeValueDTO> attributeValueDTOs = new ArrayList<>(llAttributeValues.getChildCount());
        for (int i = 0; i < llAttributeValues.getChildCount(); i++) {
            View v = llAttributeValues.getChildAt(i);
            AttributeValueDTO attributeValueDTO = (AttributeValueDTO) v.getTag();
            attributeValueDTOs.add(attributeValueDTO);
        }

        CreateVariantVavRequest request = new CreateVariantVavRequest();
        if (variant != null) {
            request.setId(variant.getId());
        }
        request.setProductId(product.getId());
        request.setName(etVariantName.getText().toString().trim());
        request.setActive(swIsActive.isChecked());
        String sku = etSku.getText().toString().trim();
        if (sku.length() > 0) {
            request.setSku(sku);
        }
        String priceString = etPrice.getText().toString().trim();
        if (!priceString.isEmpty()) {
            request.setPrice(Double.parseDouble(priceString));
        }
        String taxString = etTax.getText().toString().trim();
        if (!taxString.isEmpty()) {
            request.setTax(Double.parseDouble(taxString));
        }
        String originalPriceString = etOriginalPrice.getText().toString().trim();
        if (!originalPriceString.isEmpty()) {
            request.setOriginalPrice(Double.parseDouble(originalPriceString));
        }
        request.setAttributeValueDTOs(attributeValueDTOs);
        if (variantImageUrl != null) {
            request.setImageUrl(variantImageUrl);
        }
        showProgress(true);
        Call<BaseResponse<Variant>> saveVariantCall = Flytekart.getApiService().saveVariantVav(accessToken, clientId, request);
        saveVariantCall.enqueue(new CustomCallback<BaseResponse<Variant>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<Variant>> call) {
                Logger.i("Variant API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Variant>> call, Response<BaseResponse<Variant>> response) {
                showProgress(false);
                variant = response.body().getBody();
                getSupportActionBar().setTitle("Edit variant");
                setData();
                Toast.makeText(getApplicationContext(), "Variant saved successfully..", Toast.LENGTH_SHORT).show();
                //getVariantsData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Variant>> call, APIError responseBody) {
                Logger.e("Variant API call  response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class AttributeNamePrefixListener implements TextWatcher {
        String currentTest;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            currentTest = charSequence.toString().toLowerCase();
            if (charSequence.length() > 2) {
                List<Attribute> matchingAttributes = new ArrayList<>();
                for (AttributeResponse attributeResponse : attributeResponses) {
                    if (attributeResponse.getAttribute().getName().toLowerCase()
                            .startsWith(currentTest)) {
                        matchingAttributes.add(attributeResponse.getAttribute());
                    }
                }
                setAttributesToSpinner(matchingAttributes);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void setAttributesToSpinner(List<Attribute> attributes) {
        List<String> attributeNames = new ArrayList<>(attributes.size());
        for (Attribute attribute : attributes) {
            attributeNames.add(attribute.getName());
        }
        this.attributesStrings.clear();
        this.attributesStrings.addAll(attributeNames);
        attributeNameAdapter.clear();
        attributeNameAdapter.addAll(this.attributesStrings);
        attributeNameAdapter.notifyDataSetChanged();
    }

    private void setAttributeValuesToSpinner(List<AttributeValue> attributeValues) {
        List<String> attributeValueNames = new ArrayList<>(attributeValues.size());
        for (AttributeValue attributeValue : attributeValues) {
            attributeValueNames.add(attributeValue.getName());
        }
        this.attributeValuesStrings.clear();
        this.attributeValuesStrings.addAll(attributeValueNames);
        attributeValueNameAdapter.clear();
        attributeValueNameAdapter.addAll(this.attributeValuesStrings);
        attributeValueNameAdapter.notifyDataSetChanged();
    }

    private class AttributeValueNamePrefixListener implements TextWatcher {
        String currentTest;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            currentTest = charSequence.toString().toLowerCase();
            String attributeName = etAttributeName.getText().toString().trim().toLowerCase();
            if (charSequence.length() > 2) {
                for (AttributeResponse attributeResponse : attributeResponses) {
                    if (attributeResponse.getAttribute().getName().toLowerCase()
                            .equals(attributeName)) {
                        List<AttributeValue> matchingAttributeValues = new ArrayList<>();
                        for (AttributeValue attributeValue : attributeResponse.getAttributeValues()) {
                            if (attributeValue.getName().toLowerCase()
                                    .startsWith(currentTest)) {
                                matchingAttributeValues.add(attributeValue);
                            }
                        }
                        setAttributeValuesToSpinner(matchingAttributeValues);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
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

    private boolean checkPermissions() {
        boolean isPermissionGranted = true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ArrayList<String> permissionsList = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                isPermissionGranted = false;
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                isPermissionGranted = false;
            }

            if (permissionsList.size() > 0) {
                ActivityCompat.requestPermissions(this,
                        permissionsList.toArray(new String[0]),
                        Constants.STORAGE_PERMISSION_REQUEST_CODE);
            }
        }
        return isPermissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                showImageDialog();
            }
        }
    }
}