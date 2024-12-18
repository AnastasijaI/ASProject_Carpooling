package com.example.carpooling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class MyRidesActivity extends AppCompatActivity {
    private MyDBHelper dbHelper;
    private RecyclerView myRidesRecyclerView;
    private Ride3Adapter rideAdapter;
    private List<Ride2> myRides;
    private int passengerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        dbHelper = new MyDBHelper(this);
        myRidesRecyclerView = findViewById(R.id.myRidesRecyclerView);
        myRidesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        // Get the passengerId of the logged-in user
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        passengerId = getCurrentPassengerId(email);

        // Fetch and filter rides where hasAccepterRide == 1 and passengerId matches
        myRides = getMyAcceptedRides(passengerId);

        // Create and set the adapter for the RecyclerView
        rideAdapter = new Ride3Adapter(this, myRides);  // Use Ride3Adapter here
        myRidesRecyclerView.setAdapter(rideAdapter);

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

    private List<Ride2> getMyAcceptedRides(int passengerId) {
        List<Ride2> acceptedRides = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Ride WHERE passengerID = ?",
                new String[]{String.valueOf(passengerId)}
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int rideIdIndex = cursor.getColumnIndex("rideID");
                int driverIdIndex = cursor.getColumnIndex("driverID");
                int startLocationIndex = cursor.getColumnIndex("startLocation");
                int destinationIndex = cursor.getColumnIndex("destination");
                int priceIndex = cursor.getColumnIndex("price");
                //int hasAccepterRideIndex = cursor.getColumnIndex("hasAccepterRide");

                if (rideIdIndex >= 0 && driverIdIndex >= 0 && startLocationIndex >= 0 &&
                        destinationIndex >= 0 && priceIndex >= 0) {

                    int rideId = cursor.getInt(rideIdIndex);
                    int driverId = cursor.getInt(driverIdIndex);
                    String startLocation = cursor.getString(startLocationIndex);
                    String destination = cursor.getString(destinationIndex);
                    double price = cursor.getDouble(priceIndex);
                    //int hasAccepterRide = cursor.getInt(hasAccepterRideIndex);
                    Ride2 ride = new Ride2(rideId, driverId, passengerId, startLocation, destination, price, 0,0);
                    acceptedRides.add(ride);
                }
            }
            cursor.close();
        }
        return acceptedRides;
    }
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}

//public class MyRidesActivity extends AppCompatActivity {
//    private RecyclerView myRidesRecyclerView;
//    private List<Ride2> myRidesList;
//    private Ride3Adapter ride3Adapter;
//   // private List<Ride2> allRides;
//    private MyDBHelper dbHelper;
//    private int passengerId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_my_rides);
//
//        dbHelper = new MyDBHelper(this);
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
//        }
//
//        myRidesRecyclerView = findViewById(R.id.myRidesRecyclerView);
//        myRidesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        passengerId = getIntent().getIntExtra("passengerID", -1);
//
//        List<Ride2> allRides = dbHelper.getAllRides2();  // Fetch all rides
//        myRidesList = loadPassengerRides(allRides, passengerId, true); // Filter accepted rides
//        ride3Adapter = new Ride3Adapter(this, myRidesList);
//        myRidesRecyclerView.setAdapter(ride3Adapter);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//    private List<Ride2> loadPassengerRides(List<Ride2> allRides, int passengerId, boolean isAccepted) {
//        List<Ride2> filteredRides = new ArrayList<>();
//        for (Ride2 ride : allRides) {
//            if (ride.getPassengerID() == passengerId) {
//                if ((isAccepted && ride.isAccepted()) || (!isAccepted && !ride.isAccepted())) {
//                    filteredRides.add(ride);
//                }
//            }
//        }
//        return filteredRides;
//    }
//    public boolean onSupportNavigateUp() {
//        finish();
//        return true;
//    }
//}
