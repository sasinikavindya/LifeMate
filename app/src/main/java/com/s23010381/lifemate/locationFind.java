package com.s23010381.lifemate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class locationFind extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextInputEditText searchInput;
    private static final int LOCATION_PERMISSION_CODE = 101;
    private Context context;

    // Declare bottom navigation globally
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_find);
        context = this;

        // Initialize views
        initializeViews();

        // Setup bottom navigation
        setupBottomNavigation();

        // Setup map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Request location permissions
        requestLocationPermission();
    }

    private void initializeViews() {
        // Initialize back button
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        // Initialize search input
        searchInput = findViewById(R.id.searchInput);

        // Initialize show location button
        MaterialButton btnShowLocation = findViewById(R.id.btnShowLocation);
        btnShowLocation.setOnClickListener(v -> performSearch());

        // Initialize bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_add);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(locationFind.this, dashboard.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                return true; // Already in mood activity
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(locationFind.this, setting.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String locationQuery = searchInput.getText() != null ? searchInput.getText().toString() : "";
        if (locationQuery.isEmpty()) {
            Toast.makeText(context, "Please enter a location to search", Toast.LENGTH_SHORT).show();
            return;
        }

        searchLocation(locationQuery);
    }

    private void searchLocation(String locationName) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                // Clear previous markers
                mMap.clear();

                // Add marker for the searched location
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(locationName));

                // Move camera to the location
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            } else {
                Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(context, "Error finding location", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableMyLocation() {
        if (mMap != null && ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Set default location (Sri Lanka)
        LatLng sriLanka = new LatLng(7.8731, 80.7718);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 7f));

        enableMyLocation();
    }
}
