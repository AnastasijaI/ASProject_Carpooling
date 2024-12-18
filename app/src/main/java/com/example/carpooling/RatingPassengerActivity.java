package com.example.carpooling;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RatingPassengerActivity extends AppCompatActivity {
    private Button ratePassengerButton;
    private MyDBHelper dbHelper;
    private int rideId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rating_passenger);

        dbHelper = new MyDBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        Intent intent = getIntent();
        rideId = intent.getIntExtra("rideID", -1);

        if (rideId == -1) {
            Toast.makeText(this, "Invalid ride ID", Toast.LENGTH_SHORT).show();
            return;
        }

        ratePassengerButton = findViewById(R.id.ratePassengerButton);
        ratePassengerButton.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT passengerID FROM Ride WHERE rideID = ?", new String[]{String.valueOf(rideId)});

            if (cursor != null && cursor.moveToFirst()) {
                int passengerIndex = cursor.getColumnIndex("passengerID");
                if (passengerIndex != -1) {
                    int passengerID = cursor.getInt(passengerIndex);
                    Log.d("RatingPassengerActivity","Patnik id: "+ passengerID);
                    if (passengerID != 0) {
                        showRatingDialog();
                    } else {
                        Toast.makeText(RatingPassengerActivity.this, "Nema patnik za ovaa ruta..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RatingPassengerActivity.this, "Nema passenger", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RatingPassengerActivity.this, "Greska pri vcituvanje na rurtata", Toast.LENGTH_SHORT).show();
            }

            if (cursor != null) {
                cursor.close();
            }
            db.close();
        });

        Button rideButton = findViewById(R.id.rideButton);
        rideButton.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT startLat, startLng, destinationLat, destinationLng, passengerID FROM Ride WHERE rideID = ?", new String[]{String.valueOf(rideId)});

            if (cursor != null && cursor.moveToFirst()) {
                int startLatIndex = cursor.getColumnIndex("startLat");
                int startLngIndex = cursor.getColumnIndex("startLng");
                int destinationLatIndex = cursor.getColumnIndex("destinationLat");
                int destinationLngIndex = cursor.getColumnIndex("destinationLng");
                int passengerIndex = cursor.getColumnIndex("passengerID");
                if (passengerIndex != -1) {
                    int passengerID = cursor.getInt(passengerIndex);
                    Log.d("RatingPassengerActivity","Patnik id: "+ passengerID);
                    if (passengerID != 0) {
                        if (startLatIndex != -1 && startLngIndex != -1 && destinationLatIndex != -1 && destinationLngIndex != -1) {
                            double startLat = cursor.getDouble(startLatIndex);
                            double startLng = cursor.getDouble(startLngIndex);
                            double destinationLat = cursor.getDouble(destinationLatIndex);
                            double destinationLng = cursor.getDouble(destinationLngIndex);
                            Log.d("RatingPassengerActivity","Ane: latlng "+startLat+" , "+startLng+" , "+destinationLat+" , "+destinationLng);
                            Intent rideIntent = new Intent(RatingPassengerActivity.this, FollowRideActivity.class);
                            rideIntent.putExtra("startLocationLat", startLat);
                            rideIntent.putExtra("startLocationLng", startLng);
                            rideIntent.putExtra("endLocationLat", destinationLat);
                            rideIntent.putExtra("endLocationLng", destinationLng);
                            startActivity(rideIntent);
                        }
                    } else {
                        Toast.makeText(RatingPassengerActivity.this, "Nema patnik za ovaa ruta..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RatingPassengerActivity.this, "Nema passenger", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RatingPassengerActivity.this, "Greska pri vcituvanje na rurtata", Toast.LENGTH_SHORT).show();
            }

            if (cursor != null) {
                cursor.close();
            }
            db.close();
        });
    }
    private void showRatingDialog() {
        final String[] ratingOptions = {"1", "2", "3", "4", "5"};
        new AlertDialog.Builder(this)
                .setTitle("Select a rating")
                .setItems(ratingOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedRating = which + 1;
                        Toast.makeText(RatingPassengerActivity.this,
                                "Selected rating: " + selectedRating,
                                Toast.LENGTH_SHORT).show();
                        saveRating(selectedRating);
                    }
                })
                .show();
    }
    private void saveRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            dbHelper.updatePassengerRating(rideId, rating);
            Toast.makeText(this, "Rating saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid rating", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}