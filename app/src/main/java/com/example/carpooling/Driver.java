package com.example.carpooling;

public class Driver {
    private int driverId;
    private String name;
    private String email;
    private String password;
    private String carBrand;
    private String carColor;
    private String licensePlate;
    private String availabilityStart;
    private String availabilityEnd;

    public Driver(int driverId, String name, String email, String password, String carBrand, String carColor, String licensePlate, String availabilityStart, String availabilityEnd) {
        this.driverId = driverId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.carBrand = carBrand;
        this.carColor = carColor;
        this.licensePlate = licensePlate;
        this.availabilityStart = availabilityStart;
        this.availabilityEnd = availabilityEnd;
    }
    public int getDriverId() {
        return driverId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public String getCarColor() {
        return carColor;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getAvailabilityStart() {
        return availabilityStart;
    }

    public String getAvailabilityEnd() {
        return availabilityEnd;
    }
}
