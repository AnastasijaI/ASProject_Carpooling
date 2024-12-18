//package com.example.carpooling;
//
//import android.os.Bundle;
//
//import android.content.Intent;
//import android.util.Log;
//import android.content.SharedPreferences;
//import android.widget.Button;
//import android.view.View;
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.widget.Toolbar;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.carpooling.RideRequest;
//import java.util.ArrayList;
//import java.util.List;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.Cursor;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.RatingBar;
//
//public class DriverMainActivity extends AppCompatActivity {
//    private Button dropdownButton;
//    private Button logoutButton;
//    private Button addRouteButton;
//    private RatingBar driverRatingBar;
//    private RecyclerView ridesRecyclerView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_driver_main);
//
//        dropdownButton = findViewById(R.id.dropdownButton);
//        logoutButton = findViewById(R.id.logoutButton);
//        addRouteButton = findViewById(R.id.addRouteButton);
//        dropdownButton.setOnClickListener(v -> {
//            if (logoutButton.getVisibility() == View.GONE) {
//                logoutButton.setVisibility(View.VISIBLE);
//                addRouteButton.setVisibility(View.VISIBLE);
//            } else {
//                logoutButton.setVisibility(View.GONE);
//                addRouteButton.setVisibility(View.GONE);
//            }
//        });
//
//        logoutButton.setOnClickListener(v -> logout());
//        addRouteButton.setOnClickListener(v -> openRouteActivity());
//
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
//        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
//        String userType = sharedPreferences.getString("userType", "");
//        Log.d("DriverMainActivity", "isLoggedIn: " + isLoggedIn + ", userType: " + userType);
//
//        if (!isLoggedIn || userType.equals("driver") == false) {
//            Intent intent = new Intent(DriverMainActivity.this, LoginRegisterActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
//        }
//
////        Button logoutButton = findViewById(R.id.logoutButton);
////        logoutButton.setOnClickListener(view -> logout());
////
////        Button addRouteButton = findViewById(R.id.addRouteButton);
////        addRouteButton.setOnClickListener(v -> openRouteActivity());
//
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//    public boolean onSupportNavigateUp() {
//        finish();
//        return true;
//    }
//    private void logout() {
//        SharedPreferences sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.clear();
//        editor.apply();
//
//        Intent intent = new Intent(DriverMainActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
//    private void openRouteActivity() {
//        Intent intent = new Intent(DriverMainActivity.this, RouteActivity.class);
//        startActivity(intent);
//    }
//
//}
package com.example.carpooling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DriverMainActivity extends AppCompatActivity {
    private Button dropdownButton;
    private Button logoutButton;
    private Button addRouteButton;
    private Button detailsButton;
    private Button acceptButton;
    private RatingBar driverRatingBar;
    private TextView driverRatingText;
    private MyDBHelper dbHelper;
    private int driverId;
    private int rideID;
    private int passengerID;
    private String startLocation;
    private String destination;
    private double price;
    private int driverRating;
    private int passengerRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_main);

        dbHelper = new MyDBHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String userType = sharedPreferences.getString("userType", "");
        String email = sharedPreferences.getString("email", "");

        if (!isLoggedIn || !userType.equals("driver")) {
            Intent intent = new Intent(DriverMainActivity.this, LoginRegisterActivity.class);
            startActivity(intent);
            finish();
        }
        driverId = dbHelper.getDriverIdByEmail(email);
        RecyclerView recyclerView = findViewById(R.id.completedRoutesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Ride> rides = getCompletedRides(driverId);
        RideAdapter adapter = new RideAdapter(rides);
        recyclerView.setAdapter(adapter);


        dropdownButton = findViewById(R.id.dropdownButton);
        logoutButton = findViewById(R.id.logoutButton);
        addRouteButton = findViewById(R.id.addRouteButton);
        driverRatingBar = findViewById(R.id.driverRatingBar);
        driverRatingText = findViewById(R.id.driverRatingText);
        detailsButton = findViewById(R.id.detailsButton);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        dropdownButton.setOnClickListener(v -> toggleMenuVisibility());
        logoutButton.setOnClickListener(v -> logout());
        addRouteButton.setOnClickListener(v -> openRouteActivity());
        detailsButton.setOnClickListener(v -> openDetailsActivity());

        loadDriverRating();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private List<Ride> getCompletedRides(int driverId) {
        List<Ride> rides = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT rideID, startLocation, destination, price FROM Ride WHERE driverID = ?",
                new String[]{String.valueOf(driverId)}
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int rideID = cursor.getInt(cursor.getColumnIndexOrThrow("rideID"));
                String startLocation = cursor.getString(cursor.getColumnIndexOrThrow("startLocation"));
                String destination = cursor.getString(cursor.getColumnIndexOrThrow("destination"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                int driverRating = 0;
                int passengerRating = 0;

                rides.add(new Ride(rideID, driverId, 0, startLocation, destination, price, driverRating, passengerRating));
            }
            cursor.close();
        }
        return rides;
    }
    private void toggleMenuVisibility() {
        int visibility = (logoutButton.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
        if (visibility == View.VISIBLE) {
            logoutButton.animate().alpha(1).setDuration(300);
            addRouteButton.animate().alpha(1).setDuration(300);
            detailsButton.animate().alpha(1).setDuration(300);
        } else {
            logoutButton.animate().alpha(0).setDuration(300);
            addRouteButton.animate().alpha(0).setDuration(300);
            detailsButton.animate().alpha(0).setDuration(300);
        }
        logoutButton.setVisibility(visibility);
        addRouteButton.setVisibility(visibility);
        detailsButton.setVisibility(visibility);
    }

    private void logout() {
        SharedPreferences sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(DriverMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openRouteActivity() {
        Intent intent = new Intent(DriverMainActivity.this, RouteActivity.class);
        startActivity(intent);
    }

    private void loadDriverRating() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT AVG(driverRating) AS avgRating FROM Ride WHERE driverID = ?",
                new String[]{String.valueOf(driverId)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            int avgRatingIndex = cursor.getColumnIndex("avgRating");
            if (avgRatingIndex != -1) {
                float avgRating = cursor.isNull(avgRatingIndex) ? 0f : cursor.getFloat(avgRatingIndex);
                driverRatingBar.setRating(avgRating);
                driverRatingText.setText(String.format("Rating: %.1f", avgRating));
            } else {
                Log.e("DriverRating", "avgRating column not found in the query result.");
                driverRatingBar.setRating(0);
                driverRatingText.setText("Rating: N/A");
            }
            cursor.close();
        } else {
            driverRatingBar.setRating(0);
            driverRatingText.setText("Rating: N/A");
        }
    }
    private void openDetailsActivity() {
        Intent intent = new Intent(DriverMainActivity.this, DriverDetailsActivity.class);

        intent.putExtra("driverId", driverId);

        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}