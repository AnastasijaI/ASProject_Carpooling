package com.example.carpooling;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class PassengerMainActivity extends AppCompatActivity {
    private MyDBHelper dbHelper;
    private RatingBar passengerRatingBar;
    private TextView passengerRatingText;
    private int passengerId;
    private double selectedDestLat = 0.0, selectedDestLng = 0.0;
    private String selectedDestination;
    RecyclerView rideRecyclerView;
    private List<Ride2> availableRides = new ArrayList<>();
    private final ActivityResultLauncher<Intent> mapsActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedDestLat = data.getDoubleExtra("destLat", 0.0);
                        selectedDestLng = data.getDoubleExtra("destLng", 0.0);
                        selectedDestination = data.getStringExtra("destination");

                        Log.d("PassengerMainActivity", "Dest Lat: " + selectedDestLat + ", Dest Lng: " + selectedDestLng);
                        Log.d("PassengerMainActivity", "Destination: " + selectedDestination);

                        if (selectedDestLat != 0.0 && selectedDestLng != 0.0 && selectedDestination != null) {
                            Toast.makeText(this, "Destination selected: " + selectedDestination, Toast.LENGTH_SHORT).show();
                            sortRidesByProximity();
                        } else {
                            Toast.makeText(this, "No valid destination selected.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_main);

        dbHelper = new MyDBHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String userType = sharedPreferences.getString("userType", "");
        String email = sharedPreferences.getString("email", "");
        passengerId = dbHelper.getPassengerIdByEmail(email);

        Button searchDestinationButton = findViewById(R.id.searchDestinationButton);
        searchDestinationButton.setOnClickListener(v -> {
            Intent intent = new Intent(PassengerMainActivity.this, MapsActivity.class);
            mapsActivityLauncher.launch(intent);  // Start MapsActivity and await result
        });

        if (!isLoggedIn || !userType.equals("passenger")) {
            Intent intent = new Intent(PassengerMainActivity.this, LoginRegisterActivity.class);
            startActivity(intent);
            finish();
        }

        passengerRatingBar = findViewById(R.id.passengerRatingBar);
        passengerRatingText = findViewById(R.id.passengerRatingText);
        Button myRidesButton = findViewById(R.id.myRides);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> logout());

        Intent intent1 = getIntent();
        double selectedDestLat = intent1.getDoubleExtra("destLat", 0.0);
        double selectedDestLng = intent1.getDoubleExtra("destLng", 0.0);
        String selectedDestination = intent1.getStringExtra("destination");

        if (selectedDestLat != 0.0 && selectedDestLng != 0.0 && selectedDestination != null) {
            Toast.makeText(this, "Selected destination: " + selectedDestination, Toast.LENGTH_LONG).show();
        }

        rideRecyclerView = findViewById(R.id.rideRecyclerView);
        rideRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Ride2> allRides = dbHelper.getAllRides2();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date currentDate = new Date();
        String currentTime = timeFormat.format(currentDate);

        for (Ride2 ride : allRides) {
            Driver driver = dbHelper.getDriverById(ride.getDriverID());
            String availabilityStart = driver.getAvailabilityStart();
            String availabilityEnd = driver.getAvailabilityEnd();

            if (isRideAvailable(availabilityStart, availabilityEnd, currentTime)) {
                availableRides.add(ride);
            }
            Log.d("Ride", "Moja proba: Lat = " + ride.getDestinationLat() + ", Lng = " + ride.getDestinationLng());
            if (ride.getDestinationLat() != 0.0 && ride.getDestinationLng() != 0.0) {
                Log.d("Ride", "Ride destination: Lat = " + ride.getDestinationLat() + ", Lng = " + ride.getDestinationLng());
            } else {
                Log.d("Ride", "Invalid coordinates for ride: " + ride.getRideID());
            }
        }
        Ride2Adapter rideAdapter = new Ride2Adapter(availableRides);
        rideRecyclerView.setAdapter(rideAdapter);

        loadPassengerRating();

        myRidesButton.setOnClickListener(view -> {
            Intent intent = new Intent(PassengerMainActivity.this, MyRidesActivity.class);
            startActivity(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private boolean isRideAvailable(String availabilityStart, String availabilityEnd, String currentTime) {
        if (availabilityStart == null || availabilityEnd == null || currentTime == null) {
            return false;
        }
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date start = timeFormat.parse(availabilityStart);
            Date end = timeFormat.parse(availabilityEnd);
            Date current = timeFormat.parse(currentTime);
            return current != null && current.after(start) && current.before(end);
        } catch (ParseException e) {
            Log.e("PassengerMainActivity", "Time parsing error: " + e.getMessage());
            return false;
        }
    }
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    private void sortRidesByProximity() {
        if (selectedDestLat == 0.0 || selectedDestLng == 0.0) {
            Toast.makeText(this, "Destination is not selected", Toast.LENGTH_SHORT).show();
            return;
        }
        availableRides.sort((ride1, ride2) -> {
            double dist1 = haversine(selectedDestLat, selectedDestLng, ride1.getDestinationLat(), ride1.getDestinationLng());
            double dist2 = haversine(selectedDestLat, selectedDestLng, ride2.getDestinationLat(), ride2.getDestinationLng());
            return Double.compare(dist1, dist2);
        });
        rideRecyclerView.getAdapter().notifyDataSetChanged();
    }
    private void logout() {
        SharedPreferences sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(PassengerMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void loadPassengerRating() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT AVG(passengerRating) AS avgRating FROM Ride WHERE passengerID = ?",
                new String[]{String.valueOf(passengerId)}
        );
        if (cursor != null && cursor.moveToFirst()) {
            int avgRatingIndex = cursor.getColumnIndex("avgRating");
            if (avgRatingIndex != -1) {
                float avgRating = cursor.isNull(avgRatingIndex) ? 0f : cursor.getFloat(avgRatingIndex);
                passengerRatingBar.setRating(avgRating);
                passengerRatingText.setText(String.format("Rating: %.1f", avgRating));
            } else {
                passengerRatingBar.setRating(0);
                passengerRatingText.setText("Rating: N/A");
            }
            cursor.close();
        } else {
            passengerRatingBar.setRating(0);
            passengerRatingText.setText("Rating: N/A");
        }
    }
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}