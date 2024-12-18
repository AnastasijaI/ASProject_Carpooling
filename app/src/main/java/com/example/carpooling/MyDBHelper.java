package com.example.carpooling;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;


import android.util.Log;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Carpooling";
    private static final int DATABASE_VERSION = 9;

    public MyDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDriverTable = "CREATE TABLE Driver (" +
                "driverID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "carBrand TEXT, " +
                "carColor TEXT, " +
                "licensePlate TEXT, " +
                "availabilityStart TEXT NOT NULL," +
                "availabilityEnd TEXT NOT NULL" +
                ");";

        String createPassengerTable = "CREATE TABLE Passenger (" +
                "passengerID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL" +
                ");";

        String createRideTable = "CREATE TABLE Ride (" +
                "rideID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "driverID INTEGER, " +
                "passengerID INTEGER, " +
                "startLocation TEXT DEFAULT 'Unknown', " +
                "destination TEXT DEFAULT 'Unknown', " +
                "price REAL DEFAULT 0, " +
                "destinationLat REAL, " +
                "destinationLng REAL, " +
                "startLat REAL, " +
                "startLng REAL, " +
                "driverRating INTEGER DEFAULT 0, " +
                "passengerRating INTEGER DEFAULT 0, " +
                "hasAcceptedRide INTEGER," +
                "FOREIGN KEY(driverID) REFERENCES Driver(driverID), " +
                "FOREIGN KEY(passengerID) REFERENCES Passenger(passengerID)" +
                ");";

        db.execSQL(createDriverTable);
        db.execSQL(createPassengerTable);
        db.execSQL(createRideTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Ride");
        db.execSQL("DROP TABLE IF EXISTS Passenger");
        db.execSQL("DROP TABLE IF EXISTS Driver");
        onCreate(db);
    }

    public void insertDriver(SQLiteDatabase db, String name, String email, String password, String carBrand, String carColor, String licensePlate, String availabilityStart, String availabilityEnd) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("carBrand", carBrand);
        values.put("carColor", carColor);
        values.put("licensePlate", licensePlate);
        values.put("availabilityStart", availabilityStart);
        values.put("availabilityEnd", availabilityEnd);

        db.insert("Driver", null, values);
        db.close();
    }

    public void insertPassenger(SQLiteDatabase db, String name, String email, String password) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);

        long rowId = db.insert("Passenger", null, values);
        if (rowId == -1) {
            Log.e("MyDBHelper", "Failed to insert passenger data.");
        } else {
            Log.d("MyDBHelper", "Passenger inserted successfully with row ID: " + rowId);
        }
    }
    public void insertRide(int driverID, int passengerID, String startLocation, String destination, double price, int hasAcceptedRide, double destinationLat, double destinationLng, double startLat, double startLng) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("driverID", driverID);
        values.put("passengerID", passengerID);
        values.put("startLocation", startLocation);
        values.put("destination", destination);
        values.put("price", price);
        values.put("hasAcceptedRide", hasAcceptedRide);
        values.put("destinationLat", destinationLat);
        values.put("destinationLng", destinationLng);
        values.put("startLat", startLat);
        values.put("startLng", startLng);

        db.insert("Ride", null, values);
        db.close();
    }

    public int getDriverIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Driver", new String[]{"driverID"}, "email = ?",
                new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int driverIdIndex = cursor.getColumnIndex("driverID");
            if (driverIdIndex != -1) {
                int driverId = cursor.getInt(driverIdIndex);
                cursor.close();
                return driverId;
            } else {
                Log.e("MyDBHelper", "Column 'driverID' not found in the query result.");
            }
        } else {
            Log.e("MyDBHelper", "No driver found with the provided email: " + email);
        }

        if (cursor != null) cursor.close();
        return -1;
    }
    public List<Ride2> getAllRides2() {
        List<Ride2> rides = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Ride", null);

        if (cursor != null) {
            String[] columnNames = cursor.getColumnNames();
            for (String column : columnNames) {
                Log.d("Column Name", column);
            }

            while (cursor.moveToNext()) {
                int rideIDIndex = cursor.getColumnIndex("rideID");
                int driverIDIndex = cursor.getColumnIndex("driverID");
                int passengerIDIndex = cursor.getColumnIndex("passengerID");
                int startLocationIndex = cursor.getColumnIndex("startLocation");
                int destinationIndex = cursor.getColumnIndex("destination");
                int priceIndex = cursor.getColumnIndex("price");
                int driverRatingIndex = cursor.getColumnIndex("driverRating");
                int passengerRatingIndex = cursor.getColumnIndex("passengerRating");
                int destinationLatIndex = cursor.getColumnIndex("destinationLat");
                int destinationLngIndex = cursor.getColumnIndex("destinationLng");
                int startLatIndex = cursor.getColumnIndex("startLat");
                int startLngIndex = cursor.getColumnIndex("startLng");

                if (rideIDIndex != -1 && driverIDIndex != -1 && passengerIDIndex != -1 &&
                        startLocationIndex != -1 && destinationIndex != -1 && priceIndex != -1 &&
                        driverRatingIndex != -1 && passengerRatingIndex != -1 &&
                        destinationLatIndex != -1 && destinationLngIndex != -1 &&
                        startLatIndex != -1 && startLngIndex != -1) {

                    int rideID = cursor.getInt(rideIDIndex);
                    int driverID = cursor.getInt(driverIDIndex);
                    int passengerID = cursor.getInt(passengerIDIndex);
                    String startLocation = cursor.getString(startLocationIndex);
                    String destination = cursor.getString(destinationIndex);
                    double price = cursor.getDouble(priceIndex);
                    int driverRating = cursor.getInt(driverRatingIndex);
                    int passengerRating = cursor.getInt(passengerRatingIndex);
                    double destinationLat = cursor.getDouble(destinationLatIndex);
                    double destinationLng = cursor.getDouble(destinationLngIndex);
                    double startLat = cursor.getDouble(startLatIndex);
                    double startLng = cursor.getDouble(startLngIndex);

                    Ride2 ride = new Ride2(rideID, driverID, passengerID, startLocation, destination, price, driverRating, passengerRating);
                    ride.setDestinationLat(destinationLat);
                    ride.setDestinationLng(destinationLng);
                    ride.setStartLat(startLat);
                    ride.setStartLng(startLng);
                    rides.add(ride);
                }
            }
            cursor.close();
        }
        return rides;
    }

    public Driver getDriverById(int driverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Driver", null, "driverID = ?", new String[]{String.valueOf(driverId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            int emailIndex = cursor.getColumnIndex("email");
            int passwordIndex = cursor.getColumnIndex("password");
            int carBrandIndex = cursor.getColumnIndex("carBrand");
            int carColorIndex = cursor.getColumnIndex("carColor");
            int licensePlateIndex = cursor.getColumnIndex("licensePlate");
            int availabilityStartIndex = cursor.getColumnIndex("availabilityStart");
            int availabilityEndIndex = cursor.getColumnIndex("availabilityEnd");

            if (nameIndex != -1 && emailIndex != -1 && passwordIndex != -1 && carBrandIndex != -1 &&
                    carColorIndex != -1 && licensePlateIndex != -1 && availabilityStartIndex != -1 &&
                    availabilityEndIndex != -1) {

                String name = cursor.getString(nameIndex);
                String email = cursor.getString(emailIndex);
                String password = cursor.getString(passwordIndex);
                String carBrand = cursor.getString(carBrandIndex);
                String carColor = cursor.getString(carColorIndex);
                String licensePlate = cursor.getString(licensePlateIndex);
                String availabilityStart = cursor.getString(availabilityStartIndex);
                String availabilityEnd = cursor.getString(availabilityEndIndex);

                Driver driver = new Driver(driverId, name, email, password, carBrand, carColor, licensePlate, availabilityStart, availabilityEnd);
                cursor.close();
                return driver;
            } else {
                Log.e("Column Missing", "One or more columns are missing in the Driver table");
            }
        }
        if (cursor != null) cursor.close();
        return null;
    }
    public void updateDriverRating(int rideId, int rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("driverRating", rating);

        int rows = db.update("Ride", values, "rideID = ?", new String[]{String.valueOf(rideId)});
        if (rows > 0) {
            Log.d("MyDBHelper", "Driver rating updated successfully.");
        } else {
            Log.d("MyDBHelper", "Failed to update driver rating.");
        }
    }
    public void updatePassengerRating(int rideId, int newRating) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("passengerRating", newRating);

        db.update("Ride", values, "rideID = ?", new String[]{String.valueOf(rideId)});
        db.close();
    }
    public void updateRideWithPassenger(int rideId, int passengerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("passengerID", passengerId);

        db.update("Ride", values, "rideID = ?", new String[]{String.valueOf(rideId)});
        db.close();
    }
    public int getPassengerIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Passenger", new String[]{"passengerID"}, "email = ?",
                new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int passengerIdIndex = cursor.getColumnIndex("passengerID");
            if (passengerIdIndex != -1) {
                int passengerId = cursor.getInt(passengerIdIndex);
                cursor.close();
                return passengerId;
            } else {
                Log.e("MyDBHelper", "Column 'passengerID' not found in the query result.");
            }
        } else {
            Log.e("MyDBHelper", "No passenger found with the provided email: " + email);
        }
        if (cursor != null) cursor.close();
        return -1;
    }
    public void acceptRide(int rideID, int passengerID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("passengerID", passengerID);
        values.put("hasAcceptedRide", 1);

        int rowsAffected = db.update("Ride", values, "rideID = ?", new String[]{String.valueOf(rideID)});
        if (rowsAffected > 0) {
            Log.d("MyDBHelper", "Ride accepted successfully for rideID: " + rideID);
        } else {
            Log.e("MyDBHelper", "Failed to accept ride for rideID: " + rideID);
        }
        db.close();
    }
    public SQLiteDatabase getReadableDb() {
        return this.getReadableDatabase();
    }
}
