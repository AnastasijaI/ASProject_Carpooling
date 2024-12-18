package com.example.carpooling;

import android.content.ContentValues;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DriverDetailsActivity extends AppCompatActivity {
    private TextView rideDetailsTextView;
    private MyDBHelper dbHelper;
    private int driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_details);

        dbHelper = new MyDBHelper(this);
        rideDetailsTextView = findViewById(R.id.rideDetailsTextView);

        Intent intent = getIntent();
        driverId = intent.getIntExtra("driverId", -1);

        if (driverId != -1) {
            loadDriverAndRideDetails(driverId);
        } else {
            rideDetailsTextView.setText("No ride details available.");
        }

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> showEditDialog());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Driver Details");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT name, carBrand, carColor, licensePlate, availabilityStart, availabilityEnd FROM Driver WHERE driverID = ?",
                new String[]{String.valueOf(driverId)}
        );

        String currentName = "", currentCarBrand = "", currentCarColor = "", currentLicensePlate = "", currentAvailabilityStart = "", currentAvailabilityEnd = "";
        if (cursor != null && cursor.moveToFirst()) {
            currentName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            currentCarBrand = cursor.getString(cursor.getColumnIndexOrThrow("carBrand"));
            currentCarColor = cursor.getString(cursor.getColumnIndexOrThrow("carColor"));
            currentLicensePlate = cursor.getString(cursor.getColumnIndexOrThrow("licensePlate"));
            currentAvailabilityStart = cursor.getString(cursor.getColumnIndexOrThrow("availabilityStart"));
            currentAvailabilityEnd = cursor.getString(cursor.getColumnIndexOrThrow("availabilityEnd"));
            cursor.close();
        }

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Name");
        nameInput.setText(currentName);
        layout.addView(nameInput);

        final EditText carBrandInput = new EditText(this);
        carBrandInput.setHint("Car Brand");
        carBrandInput.setText(currentCarBrand);
        layout.addView(carBrandInput);

        final EditText carColorInput = new EditText(this);
        carColorInput.setHint("Car Color");
        carColorInput.setText(currentCarColor);
        layout.addView(carColorInput);

        final EditText licensePlateInput = new EditText(this);
        licensePlateInput.setHint("License Plate");
        licensePlateInput.setText(currentLicensePlate);
        layout.addView(licensePlateInput);

        final EditText availabilityStartInput = new EditText(this);
        availabilityStartInput.setHint("Available From");
        availabilityStartInput.setText(currentAvailabilityStart);
        layout.addView(availabilityStartInput);

        final EditText availabilityEndInput = new EditText(this);
        availabilityEndInput.setHint("Available To");
        availabilityEndInput.setText(currentAvailabilityEnd);
        layout.addView(availabilityEndInput);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = nameInput.getText().toString();
            String newCarBrand = carBrandInput.getText().toString();
            String newCarColor = carColorInput.getText().toString();
            String newLicensePlate = licensePlateInput.getText().toString();
            String newAvailabilityStart = availabilityStartInput.getText().toString();
            String newAvailabilityEnd = availabilityEndInput.getText().toString();

            ContentValues values = new ContentValues();
            values.put("name", newName);
            values.put("carBrand", newCarBrand);
            values.put("carColor", newCarColor);
            values.put("licensePlate", newLicensePlate);
            values.put("availabilityStart", newAvailabilityStart);
            values.put("availabilityEnd", newAvailabilityEnd);

            int rowsAffected = db.update("Driver", values, "driverID = ?", new String[]{String.valueOf(driverId)});
            if (rowsAffected > 0) {
                Toast.makeText(this, "Driver details updated successfully!", Toast.LENGTH_SHORT).show();
                loadDriverAndRideDetails(driverId); // Refresh details
            } else {
                Toast.makeText(this, "Failed to update driver details.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void loadDriverAndRideDetails(int driverId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor driverCursor = db.rawQuery(
                "SELECT name, carBrand, carColor, licensePlate, availabilityStart, availabilityEnd FROM Driver WHERE driverID = ?",
                new String[]{String.valueOf(driverId)}
        );

        StringBuilder details = new StringBuilder();

        if (driverCursor != null && driverCursor.moveToFirst()) {
            String name = driverCursor.getString(driverCursor.getColumnIndexOrThrow("name"));
            String carBrand = driverCursor.getString(driverCursor.getColumnIndexOrThrow("carBrand"));
            String carColor = driverCursor.getString(driverCursor.getColumnIndexOrThrow("carColor"));
            String licensePlate = driverCursor.getString(driverCursor.getColumnIndexOrThrow("licensePlate"));
            String availabilityStart = driverCursor.getString(driverCursor.getColumnIndexOrThrow("availabilityStart"));
            String availabilityEnd = driverCursor.getString(driverCursor.getColumnIndexOrThrow("availabilityEnd"));

            details.append(String.format(
                    "Driver: %s\n\nCar: %s (%s)\n\nLicense Plate: %s\n\nAvailable: %s - %s",
                    name, carBrand, carColor, licensePlate, availabilityStart, availabilityEnd));

            driverCursor.close();
        }
        rideDetailsTextView.setText(details.length() > 0 ? details.toString() : "No details available.");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}