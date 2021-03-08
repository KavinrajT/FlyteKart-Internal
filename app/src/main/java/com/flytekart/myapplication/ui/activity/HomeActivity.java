package com.flytekart.myapplication.ui.activity;

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

import com.flytekart.myapplication.MyApplication;
import com.flytekart.myapplication.R;
import com.flytekart.myapplication.models.ApiCallResponse;
import com.flytekart.myapplication.models.MenuModel;
import com.flytekart.myapplication.models.Organisation;
import com.flytekart.myapplication.models.OrganisationResponse;
import com.flytekart.myapplication.ui.adapters.MenuExpandableListAdapter;
import com.flytekart.myapplication.ui.fragment.OrganisationListFragment;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Logger;
import com.flytekart.myapplication.utils.Utilities;
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
        Call<OrganisationResponse> loginCall = MyApplication.getApiService().getAllOrganisation(accessToken);
        loginCall.enqueue(new Callback<OrganisationResponse>() {
            @Override
            public void onResponse(@NotNull Call<OrganisationResponse> call, @NotNull Response<OrganisationResponse> response) {
                Logger.i("Client Login API call response received.");
                if (response.isSuccessful() && response.body() != null) {
                    OrganisationResponse organisationResponse = response.body();
                    organisations = organisationResponse.getOrganisations();
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
            public void onFailure(@NotNull Call<OrganisationResponse> call, @NotNull Throwable t) {
                Logger.i("Organisation List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}