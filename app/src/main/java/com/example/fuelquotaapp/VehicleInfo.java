package com.example.fuelquotaapp;

import java.io.Serializable;

public class VehicleInfo implements Serializable {

    private String vehicleNumber;
    private String chassisNumber;
    private String vehicleType;
    private String fuelType;
    private double remainingQuota;

    // Constructors
    public VehicleInfo() {}

    public VehicleInfo(String vehicleNumber, String chassisNumber, String vehicleType,
                       String fuelType, double remainingQuota) {
        this.vehicleNumber = vehicleNumber;
        this.chassisNumber = chassisNumber;
        this.vehicleType = vehicleType;
        this.fuelType = fuelType;
        this.remainingQuota = remainingQuota;
    }

    // Getters and Setters
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getChassisNumber() { return chassisNumber; }
    public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public double getRemainingQuota() { return remainingQuota; }
    public void setRemainingQuota(double remainingQuota) { this.remainingQuota = remainingQuota; }
}
