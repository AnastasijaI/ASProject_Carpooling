package com.example.carpooling;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.location.Address;
import android.location.Geocoder;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {
    private EditText priceInput;
    private Button saveRouteButton;
    private GoogleMap mMap;
    private LatLng startLocation, endLocation;
    private MyDBHelper dbHelper;
    private int driverId;
    Marker marker1 = null;
    Marker marker2 = null;
    Polyline polyline1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        dbHelper = new MyDBHelper(this);
        priceInput = findViewById(R.id.priceInput);
        saveRouteButton = findViewById(R.id.saveRouteButton);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        if (email != null) {
            driverId = dbHelper.getDriverIdByEmail(email);
            Log.d("RouteActivity", "Driver ID: " + driverId);
        } else {
            Toast.makeText(this, "Email not found in preferences.", Toast.LENGTH_SHORT).show();
            driverId = -1;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error initializing the map.", Toast.LENGTH_SHORT).show();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        saveRouteButton.setOnClickListener(v -> saveRoute());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("RouteActivity", "GoogleMap is ready!");
        mMap = googleMap;
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.9981, 21.4254), 12));

        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.7749, -122.4194), 12));

            mMap.setOnMapClickListener(latLng -> {
                if (startLocation == null) {
                    startLocation = latLng;
                    if (marker1 != null) {
                        marker1.remove();
                    }
                    marker1 = mMap.addMarker(new MarkerOptions().position(latLng).title("Start Location"));
                } else if (endLocation == null) {
                    endLocation = latLng;
                    if (marker2 != null) {
                        marker2.remove();
                    }
                    marker2 = mMap.addMarker(new MarkerOptions().position(latLng).title("End Location"));
                }
                if (startLocation != null && endLocation != null) {
                    drawPolyline(startLocation, endLocation);
                }
            });
            configureMap(mMap);
        }
    }
    private void configureMap(GoogleMap map) {
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
    }

    private void drawPolyline(LatLng start, LatLng end) {
        if (polyline1 != null) {
            polyline1.remove();
        }
        PolylineOptions polylineOptions = new PolylineOptions().add(start).add(end).width(5).color(Color.RED);
        polyline1 = mMap.addPolyline(polylineOptions);
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveRoute() {
        if (mMap != null) {
            if (startLocation != null && endLocation != null) {
                String priceText = priceInput.getText().toString();
                if (!priceText.isEmpty()) {
                    double price = Double.parseDouble(priceText);

                    if (driverId != -1) {
                        convertLatLngToLocationNameAsync(startLocation, startLocationName -> {
                            convertLatLngToLocationNameAsync(endLocation, endLocationName -> {
                                dbHelper.insertRide(driverId, 0, startLocationName, endLocationName, price, 0, endLocation.latitude, endLocation.longitude, startLocation.latitude, startLocation.longitude);

                                Toast.makeText(this, "Route saved successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        });
                    } else {
                        Toast.makeText(this, "Driver ID not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please enter a valid price.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select both start and end locations.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void convertLatLngToLocationNameAsync(LatLng latLng, Callback<String> callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String locationName = address.getLocality() != null ? address.getLocality() : address.getAddressLine(0);
                    runOnUiThread(() -> callback.onResult(locationName));
                } else {
                    runOnUiThread(() -> callback.onResult("Unknown Location"));
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> callback.onResult("Unknown Location"));
            }
        });
    }
    interface Callback<T> {
        void onResult(T result);
    }
}