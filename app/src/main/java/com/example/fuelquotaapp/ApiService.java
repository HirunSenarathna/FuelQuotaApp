package com.example.fuelquotaapp;

import static android.content.Context.MODE_PRIVATE;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import okhttp3.*;
import com.google.gson.Gson;


public class ApiService {

    private static final String PREFS_NAME = "FuelAppPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String BASE_URL = "http://localhost:8080";
    private static final String TAG = "ApiService";

    private OkHttpClient client;
    private Gson gson;

    private Context context; // Add Context field

    public ApiService(Context context) {
        this.context = context;
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    // Login method
    public void login(LoginRequest loginRequest, ApiCallback<LoginResponse> callback) {
        String json = gson.toJson(loginRequest);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json);

        Request httpRequest = new Request.Builder()
                .url(BASE_URL + "/account/login")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Login API call failed", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Login response: " + responseBody);

                    try {
                        LoginResponse loginResponse = gson.fromJson(responseBody, LoginResponse.class);
                        callback.onSuccess(loginResponse);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing login response", e);
                        callback.onError("Error parsing login response: " + e.getMessage());
                    }
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    Log.e(TAG, "Login failed: " + response.code() + " - " + errorBody);

                    String errorMessage = getErrorMessage(response.code());
                    callback.onError(errorMessage);
                }
            }
        });
    }

    public void getCurrentUser(ApiCallback<FuelOperatorUserResponseDto> callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "SharedPreferences keys: " + prefs.getAll().keySet());
        String accessToken = prefs.getString(KEY_ACCESS_TOKEN, null);
        Log.d(TAG, "Retrieved access_token: " + accessToken);

        if (accessToken == null) {
            callback.onError("No access token found");
            return;
        }

        Request httpRequest = new Request.Builder()
                .url(BASE_URL + "/account/currentuser")
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Current user API call failed", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Current user response: " + responseBody);

                    try {
                        FuelOperatorUserResponseDto userResponse = gson.fromJson(responseBody, FuelOperatorUserResponseDto.class);
                        callback.onSuccess(userResponse);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing current user response", e);
                        callback.onError("Error parsing current user response: " + e.getMessage());
                    }
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    Log.e(TAG, "Current user fetch failed: " + response.code() + " - " + errorBody);
                    callback.onError("Server error: " + response.code() + " - " + errorBody);
                }
            }
        });
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


    // Helper method to get appropriate error messages based on HTTP status codes
    private String getErrorMessage(int statusCode) {
        switch (statusCode) {
            case 401:
                return "Invalid username or password";
            case 403:
                return "Access denied";
            case 404:
                return "Server not found";
            case 500:
                return "Server error. Please try again later.";
            default:
                return "Network error (Code: " + statusCode + ")";
        }
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
