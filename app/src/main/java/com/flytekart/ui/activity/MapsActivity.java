package com.flytekart.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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
import com.flytekart.utils.Logger;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private ImageView ivCurrentLocation;
    private TextView tvLocationDesc;
    private Button btnAddress;
    private TextInputEditText etLine1;
    private TextInputEditText etLine2;
    private com.flytekart.models.Address address;
    private boolean cameraMoveStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ivCurrentLocation = findViewById(R.id.iv_current_location);
        tvLocationDesc = findViewById(R.id.tv_location_desc);
        btnAddress = findViewById(R.id.btn_add_address);
        etLine1 = findViewById(R.id.et_line_1);
        etLine2 = findViewById(R.id.et_line_2);

        /*etLine1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etLine2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etLandmark.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);*/

        ivCurrentLocation.setOnClickListener(view -> {
            showCurrentLocation();
        });

        btnAddress.setOnClickListener(view -> {
            String line1 = etLine1.getText().toString().trim();
            if (line1.endsWith(Constants.COMMA)) {
                line1 = line1.substring(0, line1.length() - 1);
            }
            if (line1.isEmpty()) {
                Toast.makeText(MapsActivity.this, "Shop number cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            String line2 = etLine2.getText().toString().trim();
            if (line2.endsWith(Constants.COMMA)) {
                line2 = line2.substring(0, line1.length() - 1);
            }
            if (line2.isEmpty()) {
                Toast.makeText(MapsActivity.this, "Address line cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (address == null) {
                Toast.makeText(getApplicationContext(), R.string.err_add_address, Toast.LENGTH_SHORT).show();
                //Toast.makeText(MapsActivity.this, "Address did not fetched. Please try again later.", Toast.LENGTH_SHORT).show();
                return;
            }
            address.setLine1(line1);
            address.setLine2(line2);
            Intent intent = new Intent();
            intent.putExtra(Constants.ADDRESS, address);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        address = getIntent().getParcelableExtra(Constants.ADDRESS);
        setAddressToFields();
    }

    private void setAddressToFields() {
        if (address != null) {
            etLine1.setText(address.getLine1());
            etLine2.setText(address.getLine2());

            StringBuilder builder = new StringBuilder();
            builder.append(address.getCity()).append(Constants.COMMA_SPACE);
            builder.append(address.getState()).append(Constants.COMMA_SPACE);
            builder.append(address.getCountry()).append(Constants.COMMA_SPACE);
            builder.append(address.getZip());
            tvLocationDesc.setText(builder.toString());
        }
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

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            loadMapFromLatLng(location.getLatitude(), location.getLongitude());
                            getAddress(location.getLatitude(), location.getLongitude());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Logger.e("Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    private void loadMapFromLatLng(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
        googleMap.clear();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        googleMap.addMarker(markerOptions);
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
        this.googleMap = googleMap;
        if (address == null) {
            showCurrentLocation();
        } else {
            loadMapFromLatLng(Double.parseDouble(address.getLatitude()),
                    Double.parseDouble(address.getLongitude()));
        }

        this.googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == REASON_GESTURE) {
                    Logger.d("Gesture");
                    cameraMoveStarted = true;
                } else if (reason == REASON_API_ANIMATION) {
                    Logger.d("API animation");
                } else if (reason == REASON_DEVELOPER_ANIMATION) {
                    Logger.d("Dev animation");
                }
            }
        });
        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (cameraMoveStarted) {
                    LatLng latLng = MapsActivity.this.googleMap.getCameraPosition().target;
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                    googleMap.clear();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.addMarker(markerOptions);
                    MapsActivity.this.getAddress(latLng.latitude, latLng.longitude);
                    cameraMoveStarted = false;
                }
            }
        });

    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            if (address == null) {
                address = new com.flytekart.models.Address();
            }
            address.setCity(obj.getSubAdminArea());
            address.setState(obj.getAdminArea());
            address.setCountry(obj.getCountryName());
            address.setZip(obj.getPostalCode());
            address.setLatitude(String.valueOf(lat));
            address.setLongitude(String.valueOf(lng));
            setAddressToFields();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE
                || requestCode == 1001) {
            if (grantResults.length > 0) {
                showCurrentLocation();
            }
        }
    }
}