package com.example.carpooling;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FollowRideActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;
    private MyDBHelper dbHelper;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_ride);

        dbHelper = new MyDBHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String userType = sharedPreferences.getString("userType", "");
        Button endRideButton = findViewById(R.id.endRideButton);
        TextView rideCompletedTextView = findViewById(R.id.rideCompletedTextView);
        rideCompletedTextView.setVisibility(View.GONE);

        if (userType.equals("driver")) {
            endRideButton.setOnClickListener(v -> {
                rideCompletedTextView.setVisibility(View.VISIBLE);
                rideCompletedTextView.setText("The ride has been completed successfully.");
                endRideButton.setVisibility(View.GONE);
            });
        }

        Intent intent = getIntent();
        double startLocationLat = intent.getDoubleExtra("startLocationLat", 0.0);
        double startLocationLng = intent.getDoubleExtra("startLocationLng", 0.0);
        double endLocationLat = intent.getDoubleExtra("endLocationLat", 0.0);
        double endLocationLng = intent.getDoubleExtra("endLocationLng", 0.0);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation(startLocationLat, startLocationLng, endLocationLat, endLocationLng);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        if (myMap != null) {
            myMap.getUiSettings().setZoomControlsEnabled(true);
            myMap.getUiSettings().setCompassEnabled(true);
        }
    }
    private void getLastLocation(double startLat, double startLng, double endLat, double endLng) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(googleMap -> {
                        myMap = googleMap;

                        LatLng startLatLng = new LatLng(startLat, startLng);
                        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        LatLng endLatLng = new LatLng(endLat, endLng);
                        Log.d("FollowRideActivity", "Primer za lokacii: lat i long "+startLat+" , "+startLng+" Sega i krajna: "+endLat+" , "+endLng);
                        myMap.addMarker(new MarkerOptions().position(startLatLng).title("Start Location"));
                        myMap.addMarker(new MarkerOptions().position(currentLatLng).title("Passenger"));
                        myMap.addMarker(new MarkerOptions().position(endLatLng).title("End Location"));

                        PolylineOptions polylineOptions = new PolylineOptions()
                                .add(startLatLng)
                                .add(currentLatLng)
                                .add(endLatLng)
                                .width(8)
                                .color(Color.BLUE);
                        myMap.addPolyline(polylineOptions);

                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        boundsBuilder.include(startLatLng);
                        boundsBuilder.include(currentLatLng);
                        boundsBuilder.include(endLatLng);

                        myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
                    });
                } else {
                    Toast.makeText(this, "Map initialization failed.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Unable to get current location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = getIntent();
                double startLocationLat = intent.getDoubleExtra("startLocationLat", 0.0);
                double startLocationLng = intent.getDoubleExtra("startLocationLng", 0.0);
                double endLocationLat = intent.getDoubleExtra("endLocationLat", 0.0);
                double endLocationLng = intent.getDoubleExtra("endLocationLng", 0.0);
                getLastLocation(startLocationLat, startLocationLng, endLocationLat, endLocationLng);
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}