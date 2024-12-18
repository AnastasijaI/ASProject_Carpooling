package com.example.carpooling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

public class ChooseActivity extends AppCompatActivity {
    private TextView startLocationText, destinationText, priceText;
    private Button acceptRideButton, ratingButton, followRide;

    private boolean accepted = false;
    private Driver driver;
    //private Ride2 ride;
    private MyDBHelper dbHelper;
    private boolean isAccepted = false;
    //private String passengerEmail;
    private int passengerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose);
        dbHelper = new MyDBHelper(this);

        startLocationText = findViewById(R.id.startLocationText);
        destinationText = findViewById(R.id.destinationText);
        priceText = findViewById(R.id.priceText);
        acceptRideButton = findViewById(R.id.acceptRideButton);
        ratingButton = findViewById(R.id.ratingButton);
        followRide = findViewById(R.id.followRide);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        TextView driverNameText = findViewById(R.id.driverNameText);
        TextView carBrandText = findViewById(R.id.carBrandText);
        TextView carColorText = findViewById(R.id.carColorText);
        TextView carRegistrationText = findViewById(R.id.carRegistrationText);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        Intent intent = getIntent();
        int rideId = intent.getIntExtra("rideId", -1);
        int driverId = intent.getIntExtra("driverID", -1);
        String startLocation = intent.getStringExtra("startLocation");
        String destination = intent.getStringExtra("destination");
        double price = intent.getDoubleExtra("price", 0.0);
        double startLocationLat = intent.getDoubleExtra("startLocationLat", 0.0);
        double startLocationLng = intent.getDoubleExtra("startLocationLng", 0.0);
        double endLocationLat = intent.getDoubleExtra("endLocationLat", 0.0);
        double endLocationLng = intent.getDoubleExtra("endLocationLng", 0.0);

        driver = dbHelper.getDriverById(driverId);

        startLocationText.setText("From: " + startLocation);
        destinationText.setText("To: " + destination);
        priceText.setText("Price: $" + price);

        driverNameText.setText("Driver Name: " + driver.getName());
        carBrandText.setText("Car Brand: " + driver.getCarBrand());
        carColorText.setText("Car Color: " + driver.getCarColor());
        carRegistrationText.setText("Car Registration: " + driver.getLicensePlate());

        passengerId = getCurrentPassengerId(email);
        isAccepted = checkIfRideAccepted(rideId, passengerId);
        if (isAccepted) {
            acceptRideButton.setEnabled(false);
            Toast.makeText(this, "You have already accepted this ride.", Toast.LENGTH_SHORT).show();
            ratingButton.setVisibility(View.VISIBLE);
            followRide.setVisibility(View.VISIBLE);
        }
//        int passengerId = dbHelper.getPassengerIdByEmail(passengerEmail);
        acceptRideButton.setOnClickListener(v -> {
            if (!isAccepted) {
                dbHelper.acceptRide(rideId, passengerId);
                acceptRideButton.setEnabled(false);
                Toast.makeText(this, "Ride accepted successfully!", Toast.LENGTH_SHORT).show();
                ratingButton.setVisibility(View.VISIBLE);
                followRide.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "You have already accepted this ride.", Toast.LENGTH_SHORT).show();
            }
        });
        ratingButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Rate the Driver");

            final RadioGroup ratingGroup = new RadioGroup(this);
            for (int i = 1; i <= 5; i++) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(String.valueOf(i));
                ratingGroup.addView(radioButton);
            }

            builder.setView(ratingGroup);

            builder.setPositiveButton("Submit", (dialog, which) -> {
                int selectedId = ratingGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedButton = ratingGroup.findViewById(selectedId);
                    int rating = Integer.parseInt(selectedButton.getText().toString());

                    dbHelper.updateDriverRating(rideId, rating);
                    Toast.makeText(this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please select a rating!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });
        followRide.setOnClickListener(v -> {
            Intent followRideIntent = new Intent(ChooseActivity.this, FollowRideActivity.class);
            followRideIntent.putExtra("startLocationLat", startLocationLat);
            followRideIntent.putExtra("startLocationLng", startLocationLng);
            followRideIntent.putExtra("endLocationLat", endLocationLat);
            followRideIntent.putExtra("endLocationLng", endLocationLng);
            startActivity(followRideIntent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private int getCurrentPassengerId(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Passenger", new String[]{"passengerID"}, "email = ?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int passengerIdIndex = cursor.getColumnIndex("passengerID");
            if (passengerIdIndex != -1) {
                return cursor.getInt(passengerIdIndex);
            }
        }
        return -1; // Default value, if not found
    }

    private boolean checkIfRideAccepted(int rideId, int passengerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT hasAcceptedRide FROM Ride WHERE rideID = ? AND passengerID = ?",
                new String[]{String.valueOf(rideId), String.valueOf(passengerId)});
        if (cursor != null && cursor.moveToFirst()) {
            // Log the column names for debugging
            String[] columnNames = cursor.getColumnNames();
            Log.d("DB_COLUMNS", "Columns: " + Arrays.toString(columnNames));

            int hasAcceptedRideIndex = cursor.getColumnIndex("hasAcceptedRide");
            if (hasAcceptedRideIndex != -1) {
                int hasAcceptedRide = cursor.getInt(hasAcceptedRideIndex);
                cursor.close();
                return hasAcceptedRide == 1;
            } else {
                Log.e("DB_ERROR", "'hasAcceptedRide' column not found in the result set");
            }
        }
        return false;
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}