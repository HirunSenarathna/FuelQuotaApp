package com.example.fuelquotaapp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.util.Log;
import okhttp3.*;
import com.google.gson.Gson;


public class ApiService {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String TAG = "ApiService";

    private OkHttpClient client;
    private Gson gson;

    public ApiService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    public void processFuelTransaction(FuelTransactionRequest request, ApiCallback<String> callback) {
        String json = gson.toJson(request);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json);

        Request httpRequest = new Request.Builder()
                .url(BASE_URL + "/fuel/pump")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API call failed", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onSuccess(responseBody);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    callback.onError("Server error: " + response.code() + " - " + errorBody);
                }
            }
        });
    }

    public void getVehicleQuota(String vehicleNumber, ApiCallback<VehicleInfo> callback) {
        Request httpRequest = new Request.Builder()
                .url(BASE_URL + "/fuel/quota/" + vehicleNumber)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API call failed", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Vehicle quota response: " + responseBody);

                    try {
                        // Parse the Vehicle response from backend
                        VehicleResponse vehicleResponse = gson.fromJson(responseBody, VehicleResponse.class);

                        // Convert to VehicleInfo
                        VehicleInfo vehicleInfo = new VehicleInfo();
                        vehicleInfo.setVehicleNumber(vehicleResponse.getVehicleNumber());
                        vehicleInfo.setChassisNumber(vehicleResponse.getChassisNumber());
                        vehicleInfo.setVehicleType(vehicleResponse.getVehicleType());
                        vehicleInfo.setFuelType(vehicleResponse.getFuelType());
                        vehicleInfo.setRemainingQuota(vehicleResponse.getRemainingQuotaLimit());

                        callback.onSuccess(vehicleInfo);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing vehicle data", e);
                        callback.onError("Error parsing vehicle data: " + e.getMessage());
                    }
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    callback.onError("Server error: " + response.code() + " - " + errorBody);
                }
            }
        });
    }

    // Helper class to match the backend Vehicle model response
    private static class VehicleResponse {
        private String vehicleNumber;
        private String chassisNumber;
        private String vehicleType;
        private String fuelType;
        private double remainingQuotaLimit;

        // Getters
        public String getVehicleNumber() { return vehicleNumber; }
        public String getChassisNumber() { return chassisNumber; }
        public String getVehicleType() { return vehicleType; }
        public String getFuelType() { return fuelType; }
        public double getRemainingQuotaLimit() { return remainingQuotaLimit; }

        // Setters
        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
        public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }
        public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
        public void setFuelType(String fuelType) { this.fuelType = fuelType; }
        public void setRemainingQuotaLimit(double remainingQuotaLimit) { this.remainingQuotaLimit = remainingQuotaLimit; }
    }
}
