package com.example.carpooling;
public class Ride2 {
    private int rideID;
    private int driverID;
    private int passengerID;
    private String startLocation;
    private String destination;
    private double price;
    private int driverRating;
    private int passengerRating;
    private boolean isAccepted;
    private double destinationLat;
    private double destinationLng;
    private double startLat;
    private double startLng;

    public Ride2(int rideID, int driverID, int passengerID, String startLocation, String destination, double price, int driverRating, int passengerRating) {
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
    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }
    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public double getStartLat() {
        return startLat;
    }

    public double getStartLng() {
        return startLng;
    }
}
