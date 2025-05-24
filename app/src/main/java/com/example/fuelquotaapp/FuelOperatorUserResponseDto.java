package com.example.fuelquotaapp;


public class FuelOperatorUserResponseDto {

    private int id;
    private String username;
    private String password;
    private String role;
    private int operatorId;
    private String fullName;
    private String nic;
    private String phoneNumber;
    private int stationId;

    public FuelOperatorUserResponseDto() {
    }

    public FuelOperatorUserResponseDto(int id, String username, String password, String role, int operatorId, String fullName, String nic, String phoneNumber, int stationId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.operatorId = operatorId;
        this.fullName = fullName;
        this.nic = nic;
        this.phoneNumber = phoneNumber;
        this.stationId = stationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }
}
