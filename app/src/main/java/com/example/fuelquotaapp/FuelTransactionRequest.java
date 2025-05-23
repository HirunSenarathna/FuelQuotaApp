package com.example.fuelquotaapp;

public class FuelTransactionRequest {

    private String vehicleNumber;
    private String chassisNumber;
    private double pumpedAmount;

    // Constructors
    public FuelTransactionRequest() {}

    public FuelTransactionRequest(String vehicleNumber, String chassisNumber, double pumpedAmount) {
        this.vehicleNumber = vehicleNumber;
        this.chassisNumber = chassisNumber;
        this.pumpedAmount = pumpedAmount;
    }

    // Getters and Setters
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getChassisNumber() { return chassisNumber; }
    public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }

    public double getPumpedAmount() { return pumpedAmount; }
    public void setPumpedAmount(double pumpedAmount) { this.pumpedAmount = pumpedAmount; }
}
