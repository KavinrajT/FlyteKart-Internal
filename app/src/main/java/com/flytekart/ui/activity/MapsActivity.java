package com.flytekart.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.flytekart.R;
import com.flytekart.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView ivCurrentLocation;
    private TextView tvLocationName;
    private TextView tvLocationDesc;
    private Button btnAddress;
    private String txtAddress;
    private TextInputEditText etBuildingName;
    private TextInputEditText etLandmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ivCurrentLocation = findViewById(R.id.iv_current_location);
        tvLocationName = findViewById(R.id.tv_location_name);
        tvLocationDesc = findViewById(R.id.tv_location_desc);
        btnAddress = findViewById(R.id.btn_add_address);
        etBuildingName = findViewById(R.id.et_building_name);
        etLandmark = findViewById(R.id.et_landmark);

        etBuildingName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etLandmark.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        ivCurrentLocation.setOnClickListener(view -> {
            showCurrentLocation();
        });

        btnAddress.setOnClickListener(view -> {
            String txtBuildName = etBuildingName.getText().toString();
            String txtLandmark = etLandmark.getText().toString();
            String strAddress = "";
            if (txtAddress != null && !txtAddress.isEmpty()) {
                if (!txtBuildName.isEmpty()) {
                    strAddress = txtBuildName + ", ";
                }
                if (!txtLandmark.isEmpty()) {
                    strAddress = txtLandmark + ", ";
                }
                strAddress = strAddress + txtAddress;
                Intent intent = new Intent();
                intent.putExtra("address", txtAddress);
                setResult(Activity.RESULT_OK, intent);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), R.string.err_add_address, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ArrayList<String> permissionsList = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(MapsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (permissionsList.size() > 0) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        permissionsList.toArray(new String[permissionsList.size()]),
                        1001);
            }
            return;
        }
        LocationManager locationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(latLng.latitude + " : " + latLng.longitude);
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            mMap.addMarker(markerOptions);
            getAddress(latLng.latitude, latLng.longitude);

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showCurrentLocation();

        mMap.setOnMapClickListener(latLng -> {

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(latLng.latitude + " : " + latLng.longitude);
            googleMap.clear();
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.addMarker(markerOptions);
            getAddress(latLng.latitude, latLng.longitude);
        });

    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            tvLocationName.setText(obj.getFeatureName());
            tvLocationDesc.setText(obj.getAddressLine(0));
            txtAddress = obj.getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                showCurrentLocation();
            }
        }
    }
}