package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.flytekart.R;
import com.flytekart.models.MenuModel;
import com.flytekart.models.Organisation;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.adapters.MenuExpandableListAdapter;
import com.flytekart.ui.fragment.OrganisationListFragment;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainHomeActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    ActionBarDrawerToggle drawerToggle;
    private ProgressDialog progressDialog;

    private List<Organisation> organisations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTitle = mDrawerTitle = getTitle();
        // mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);

        setupToolbar();
        prepareMenuData();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        expandableListView = findViewById(R.id.left_drawer);
        expandableListAdapter = new MenuExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnItemClickListener(new DrawerItemClickListener());
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(drawerToggle);
        setupDrawerToggle();

        getAllOrganisations();
    }


    private void prepareMenuData() {
        headerList.add(new MenuModel("Change Password", true, true, null));
        headerList.add(new MenuModel("Sign Out", true, true, null));
    }

    private class DrawerItemClickListener implements ExpandableListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                MenuModel menuModel = headerList.get(position);
                if (TextUtils.equals(menuModel.getMenuName(), "Change Password")) {

                } else if (TextUtils.equals(menuModel.getMenuName(), "Sign Out")) {
                    SharedPreferences.Editor editor = Utilities.getSharedPreferences().edit();
                    editor.putBoolean(Constants.SHARED_PREF_KEY_IS_MAIN_ACCOUNT_LOGGED_IN, false);
                    editor.putString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, "");
                    editor.apply();
                    finish();
                }
            }

        }
    }

    private void populateFragment() {
        Fragment fragment = OrganisationListFragment.newInstance(organisations);
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            //setTitle(mNavigationDrawerItemTitles[position]);
            //drawerLayout.closeDrawer(expandableListView);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        drawerToggle.syncState();
    }

    private void getAllOrganisations() {
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, "");
        showProgress(true);
        Call<BaseResponse<Organisation>> loginCall = com.flytekart.Flytekart.getApiService().getAllOrganisations(accessToken);
        loginCall.enqueue(new CustomCallback<BaseResponse<Organisation>>() {
            @Override
            public void onFlytekartSuccessResponse(@NotNull Call<BaseResponse<Organisation>> call, @NotNull Response<BaseResponse<Organisation>> response) {
                Logger.i("Client Login API call response received.");
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    //organisations = response.body().getBody();
                    Toast.makeText(getApplicationContext(), "Organisation List API call successful.", Toast.LENGTH_SHORT).show();
                } else if (response.errorBody() != null) {
                    try {
                        ApiCallResponse apiCallResponse = new Gson().fromJson(
                                response.errorBody().string(), ApiCallResponse.class);
                        Toast.makeText(getApplicationContext(), apiCallResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Logger.e("Organisation List API call  response status code : " + response.code());
                populateFragment();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<Organisation>> call, APIError responseBody) {
                Logger.e("Organisation List API call failed.");
                showProgress(false);
            }

            @Override
            public void onFlytekartGenericErrorResponse(@NotNull Call<BaseResponse<Organisation>> call) {
                Logger.i("Organisation List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
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