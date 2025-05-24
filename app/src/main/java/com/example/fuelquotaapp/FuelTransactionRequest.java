package com.example.fuelquotaapp;

public class FuelTransactionRequest {

    private String vehicleNumber;
    private String chassisNumber;
    private double pumpedAmount;
    private int operatorId;
    private int stationId;
    private String fuelType;

    // Constructors
    public FuelTransactionRequest() {}

    public FuelTransactionRequest(String vehicleNumber, String chassisNumber, double pumpedAmount, int operatorId, int stationId, String fuelType) {
        this.vehicleNumber = vehicleNumber;
        this.chassisNumber = chassisNumber;
        this.pumpedAmount = pumpedAmount;
        this.operatorId = operatorId;
        this.stationId = stationId;
        this.fuelType = fuelType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public double getPumpedAmount() {
        return pumpedAmount;
    }

    public void setPumpedAmount(double pumpedAmount) {
        this.pumpedAmount = pumpedAmount;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
}
