package com.example.carpooling;

public class Ride3 {
    private int rideID;
    private int driverID;
    private int passengerID;
    private String startLocation;
    private String destination;
    private double price;
    private int driverRating;
    private int passengerRating;
    private boolean isAccepted;

    public Ride3(int rideID, int driverID, int passengerID, String startLocation, String destination, double price, int driverRating, int passengerRating) {
        this.rideID = rideID;
        this.driverID = driverID;
        this.passengerID = passengerID;
        this.startLocation = startLocation;
        this.destination = destination;
        this.price = price;
        this.driverRating = driverRating;
        this.passengerRating = passengerRating;
    }

    public int getRideID() {
        return rideID;
    }

    public int getDriverID() {
        return driverID;
    }

    public int getPassengerID() {
        return passengerID;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getDestination() {
        return destination;
    }

    public double getPrice() {
        return price;
    }

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

    public int getPassengerRating() {
        return passengerRating;
    }

    public void setPassengerRating(int passengerRating) {
        this.passengerRating = passengerRating;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}