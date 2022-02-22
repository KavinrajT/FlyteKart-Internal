package com.flytekart.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.Category;
import com.flytekart.models.Store;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.models.response.FileUploadResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.CategoriesAdapter;
import com.flytekart.utils.Constants;
import com.flytekart.utils.FileUtils;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.kbeanie.multipicker.api.FilePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class CategoryListActivity extends AppCompatActivity implements CategoriesAdapter.CategoryClickListener {

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvCategoryList;
    private CategoriesAdapter adapter;
    private List<Category> categories;
    private LinearLayoutManager categoriesLayoutManager;
    private String clientId;
    private String accessToken;
    private ProgressDialog progressDialog;
    private FilePicker filePicker;
    private ActivityResultLauncher<Intent> editCategoryActivityResultLauncher;
    private ActivityResultLauncher<Intent> createCategoryActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.categories);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        llNoRecordsFound = findViewById(R.id.ll_no_records_found);
        rvCategoryList = findViewById(R.id.rv_category_list);
        rvCategoryList.setHasFixedSize(true);
        categoriesLayoutManager = new LinearLayoutManager(this);
        rvCategoryList.setLayoutManager(categoriesLayoutManager);
        rvCategoryList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        registerForActivityResults();
        getData();
        //setListeners();
        //setData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_refresh: {
                getData();
                return true;
            }
            case R.id.menu_create: {
                Intent createCategoryIntent = new Intent(this, CreateCategoryActivity.class);
                createCategoryActivityResultLauncher.launch(createCategoryIntent);
                return true;
            }
            case R.id.menu_upload: {
                //pickFile();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateCategoriesData(Intent data) {
        Category addedCategory = data.getParcelableExtra(Constants.CATEGORY);
        if (addedCategory != null) {
            if (categories == null) {
                categories = new ArrayList<>(10);
                categories.add(addedCategory);
                setCategoriesData();
            } else {
                categories.add(addedCategory);
                if (adapter == null) {
                    adapter = new CategoriesAdapter(this, categories);
                }
                adapter.notifyItemInserted(categories.size() - 1);
            }
        }
    }

    /*private void pickFile() {
        FilePickerCallback callback = new FilePickerCallback() {
            @Override
            public void onFilesChosen(List<ChosenFile> list) {
                String originalPath = list.get(0).getOriginalPath();
                String uriString = FileUtils.getUriFromPath(originalPath);
                Uri uri = Uri.parse(uriString);
                File file = FileUtils.getFile(CategoryListActivity.this, uri);
                uploadFile(file, uri);
            }

            @Override
            public void onError(String s) {
                Logger.e("File pick failed: " + s);
            }
        };
        filePicker = new FilePicker(this);
        filePicker.setMimeType("text/csv");
        filePicker.setFilePickerCallback(callback);
        filePicker.pickFile();
    }*/

    public void uploadFile(File file, Uri uri) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("text/csv"), file);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), fileBody);
        MultipartBody body = builder.build();

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        String clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        showProgress(true);
        Call<FileUploadResponse> uploadFileCall = Flytekart.getApiService().uploadProductsFile(accessToken, clientId, body);
        uploadFileCall.enqueue(new CustomCallback<>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<FileUploadResponse> call) {
                Logger.i("Category file upload API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.categories_data_upload_success, Toast.LENGTH_SHORT).show();
                getData();
            }

            @Override
            public void onFlytekartErrorResponse(Call<FileUploadResponse> call, APIError responseBody) {
                Logger.e("Category file upload API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Picker.PICK_FILE && resultCode == RESULT_OK) {
            filePicker.submit(data);
        }
    }*/

    private void getData() {
        showProgress(true);
        Call<BaseResponse<List<Category>>> getCategoriesCall = Flytekart.getApiService().getAllCategories(accessToken, clientId);
        getCategoriesCall.enqueue(new CustomCallback<BaseResponse<List<Category>>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<List<Category>>> call, Response<BaseResponse<List<Category>>> response) {
                Logger.i("Categories list response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body().getBody();
                    setCategoriesData();
                }
                Logger.e("Categories List API call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<List<Category>>> call, APIError responseBody) {
                Logger.e("Categories List API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<List<Category>>> call) {
                Logger.i("Categories List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCategoriesData() {
        if (categories == null || categories.isEmpty()) {
            rvCategoryList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(CategoryListActivity.this, CreateCategoryActivity.class);
                createCategoryActivityResultLauncher.launch(intent);
            });

        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvCategoryList.setVisibility(View.VISIBLE);

            adapter = new CategoriesAdapter(this, categories);
            rvCategoryList.setAdapter(adapter);
        }
    }

    private void setListeners() {
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        rvCategoryList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    onCategoryClicked(pos);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void registerForActivityResults() {
        editCategoryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            int position = data.getIntExtra(Constants.POSITION, -1);
                            Category editedCategory = data.getParcelableExtra(Constants.CATEGORY);
                            if (position != -1 && editedCategory != null) {
                                categories.remove(position);
                                categories.add(position, editedCategory);
                                adapter.notifyItemChanged(position);
                            }
                        }
                    }
                });
        createCategoryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            updateCategoriesData(result.getData());
                        }
                    }
                });
    }

    /**
     * Open products list of this category
     *
     * @param position
     */
    @Override
    public void onCategoryClicked(int position) {
        Category category = categories.get(position);
        Intent itemIntent = new Intent(this, ProductListActivity.class);
        itemIntent.putExtra(Constants.CATEGORY, category);
        startActivity(itemIntent);
    }

    @Override
    public void onEdit(int position) {
        Intent editCategoryIntent = new Intent(this, CreateCategoryActivity.class);
        editCategoryIntent.putExtra(Constants.CATEGORY, categories.get(position));
        editCategoryIntent.putExtra(Constants.POSITION, position);
        editCategoryActivityResultLauncher.launch(editCategoryIntent);
    }

    @Override
    public void onDelete(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete the category: " + categories.get(position).getName());
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCategory(position);
            }
        });
        builder.show();
    }

    private void deleteCategory(int position) {
        Category category = categories.get(position);
        showProgress(true);
        Call<BaseResponse<Category>> deleteCategoryCall = Flytekart.getApiService().deleteCategory(accessToken, category.getId(), clientId);
        deleteCategoryCall.enqueue(new CustomCallback<BaseResponse<Category>>() {
            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<Category>> call, Response<BaseResponse<Category>> response) {
                Logger.i("Delete category response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    Category deletedCategory = response.body().getBody();
                    categories.remove(position);
                    adapter.notifyItemRemoved(position);
                    if (position <= categories.size() - 1) {
                        adapter.notifyItemRangeChanged(position, categories.size() - position);
                    }
                }
                Logger.e("Delete categoryAPI call response status code : " + response.code());
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Category>> call, APIError responseBody) {
                Logger.e("Delete category API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<Category>> call) {
                Logger.i("Delete category API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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