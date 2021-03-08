package com.flytekart.myapplication.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.flytekart.myapplication.MyApplication;
import com.flytekart.myapplication.R;
import com.flytekart.myapplication.models.ApiCallResponse;
import com.flytekart.myapplication.models.LoginResponse;
import com.flytekart.myapplication.models.Organisation;
import com.flytekart.myapplication.models.Product;
import com.flytekart.myapplication.models.Store;
import com.flytekart.myapplication.ui.activity.CategoryListActivity;
import com.flytekart.myapplication.ui.activity.CreateOrgActivity;
import com.flytekart.myapplication.ui.activity.HomeActivity;
import com.flytekart.myapplication.ui.activity.LoginActivity;
import com.flytekart.myapplication.ui.activity.StoreListActivity;
import com.flytekart.myapplication.utils.Constants;
import com.flytekart.myapplication.utils.Logger;
import com.flytekart.myapplication.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button btnCreateOrg;
    private Button btnCreateStore;
    private Button btnCreateProduct;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    private void setData() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();

        String orgJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ORGANISATION, null);
        Organisation organisation = gson.fromJson(orgJsonStr, Organisation.class);
        if (organisation != null) {
            setButtonBackground(btnCreateOrg, R.drawable.selector_green_button_done);
            if (TextUtils.equals(organisation.getStoreType(),
                    getResources().getStringArray(R.array.store_type_array)[0])) {
                setButtonBackground(btnCreateStore, R.drawable.selector_green_button_done);
            } else {
                btnCreateStore.setEnabled(true);
            }
        } else {
            setButtonBackground(btnCreateOrg, R.drawable.selector_red_button_num_1);
            btnCreateOrg.setEnabled(true);
            btnCreateStore.setEnabled(false);
            btnCreateProduct.setEnabled(false);
            return;
        }

        String storesJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_STORES, null);
        List<Store> stores = gson.fromJson(storesJsonStr, new TypeToken<List<Store>>() {
        }.getType());
        if (stores != null && !stores.isEmpty()) {
            setButtonBackground(btnCreateStore, R.drawable.selector_green_button_done);
            btnCreateProduct.setEnabled(true);
        } else {
            setButtonBackground(btnCreateStore, R.drawable.selector_red_button_num_2);
            btnCreateStore.setEnabled(true);
            btnCreateProduct.setEnabled(false);
            return;
        }

        String productsJsonStr = sharedPreferences.getString(Constants.SHARED_PREF_KEY_PRODUCTS, null);
        List<Product> products = gson.fromJson(productsJsonStr, new TypeToken<List<Product>>() {
        }.getType());
        if (products != null && !products.isEmpty()) {
            setButtonBackground(btnCreateProduct, R.drawable.selector_green_button_done);
        } else {
            setButtonBackground(btnCreateProduct, R.drawable.selector_red_button_num_3);
            btnCreateProduct.setEnabled(true);
        }
    }

    private void setButtonBackground(View view, int drawable) {
        view.setBackground(ResourcesCompat.getDrawable(getResources(), drawable, getActivity().getTheme()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCreateOrg = view.findViewById(R.id.btn_create_org);
        btnCreateStore = view.findViewById(R.id.btn_add_stores);
        btnCreateProduct = view.findViewById(R.id.btn_add_products);
        Button btnClearAll = view.findViewById(R.id.btn_clear_all);

        btnCreateOrg.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), CreateOrgActivity.class);
            startActivityForResult(intent, Constants.CREATE_ORG_ACTIVITY_REQUEST_CODE);
        });

        btnCreateStore.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StoreListActivity.class);
            startActivityForResult(intent, Constants.STORE_LIST_ACTIVITY_REQUEST_CODE);
        });

        btnCreateProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryListActivity.class);
            startActivityForResult(intent, Constants.ADD_PRODUCT_ACTIVITY_REQUEST_CODE);
        });

        btnClearAll.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Constants.SHARED_PREF_KEY_ORGANISATION);
            editor.remove(Constants.SHARED_PREF_KEY_STORES);
            editor.remove(Constants.SHARED_PREF_KEY_PRODUCTS);
            editor.apply();
            setData();
        });

        setData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setData();
    }
}