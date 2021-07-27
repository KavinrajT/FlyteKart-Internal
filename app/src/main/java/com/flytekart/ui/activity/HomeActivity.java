package com.flytekart.ui.activity;

import android.content.Intent;
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

import com.flytekart.R;
import com.flytekart.models.MenuModel;
import com.flytekart.models.Organisation;
import com.flytekart.models.response.ApiCallResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.ui.adapters.MenuExpandableListAdapter;
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
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    ActionBarDrawerToggle drawerToggle;

    private Organisation organisation;

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
        expandableListView.setOnGroupClickListener(new GroupClickListener());
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(drawerToggle);
        setupDrawerToggle();

        //getAllOrganisations();
    }


    private void prepareMenuData() {
        headerList.add(new MenuModel(getString(R.string.stores), true, true, null));
        headerList.add(new MenuModel(getString(R.string.categories_products), true, true, null));
        headerList.add(new MenuModel(getString(R.string.orderResponses), true, true, null));
        headerList.add(new MenuModel(getString(R.string.change_password), true, true, null));
        headerList.add(new MenuModel(getString(R.string.sign_out), true, true, null));
    }

    private class GroupClickListener implements ExpandableListView.OnGroupClickListener {

        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            MenuModel menuModel = headerList.get(groupPosition);
            if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.stores))) {
                Intent intent = new Intent(HomeActivity.this, StoreListActivity.class);
                startActivityForResult(intent, Constants.STORE_LIST_ACTIVITY_REQUEST_CODE);
            } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.categories_products))) {
                Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
                startActivityForResult(intent, Constants.CATEGORY_LIST_ACTIVITY_REQUEST_CODE);
            } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.change_password))) {

            } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.sign_out))) {
                SharedPreferences.Editor editor = Utilities.getSharedPreferences().edit();
                editor.remove(Constants.SHARED_PREF_KEY_IS_MAIN_ACCOUNT_LOGGED_IN);
                editor.remove(Constants.SHARED_PREF_KEY_ACCESS_TOKEN);
                editor.remove(Constants.SHARED_PREF_KEY_CLIENT_ID);
                editor.apply();
                finish();
                // TODO Open login screen
            }
            return false;
        }
    }

    private class DrawerItemClickListener implements ExpandableListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                MenuModel menuModel = headerList.get(position);
                if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.stores))) {
                    Intent intent = new Intent(HomeActivity.this, StoreListActivity.class);
                    startActivityForResult(intent, Constants.STORE_LIST_ACTIVITY_REQUEST_CODE);
                } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.categories_products))) {
                    Intent intent = new Intent(HomeActivity.this, CategoryListActivity.class);
                    startActivityForResult(intent, Constants.CATEGORY_LIST_ACTIVITY_REQUEST_CODE);
                } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.change_password))) {

                } else if (TextUtils.equals(menuModel.getMenuName(), getString(R.string.sign_out))) {
                    SharedPreferences.Editor editor = Utilities.getSharedPreferences().edit();
                    editor.remove(Constants.SHARED_PREF_KEY_IS_MAIN_ACCOUNT_LOGGED_IN);
                    editor.remove(Constants.SHARED_PREF_KEY_ACCESS_TOKEN);
                    editor.apply();
                    finish();
                }
            }

        }
    }

    private void populateFragment() {
        /*Fragment fragment = OrganisationListFragment.newInstance(organisations);
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            //setTitle(mNavigationDrawerItemTitles[position]);
            //drawerLayout.closeDrawer(expandableListView);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }*/
        Log.e("MainActivity", "In populateFragment");
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
        String accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        String clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        Call<BaseResponse<Organisation>> organisationCall = com.flytekart.Flytekart.getApiService().getOrganisation(accessToken, clientId);
        organisationCall.enqueue(new Callback<BaseResponse<Organisation>>() {
            @Override
            public void onResponse(@NotNull Call<BaseResponse<Organisation>> call, @NotNull Response<BaseResponse<Organisation>> response) {
                Logger.i("Client Login API call response received.");
                if (response.isSuccessful() && response.body() != null) {
                    organisation = response.body().getBody();
                    Toast.makeText(getApplicationContext(), "Organisation API call successful.", Toast.LENGTH_SHORT).show();
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
            public void onFailure(@NotNull Call<BaseResponse<Organisation>> call, @NotNull Throwable t) {
                Logger.i("Organisation List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}