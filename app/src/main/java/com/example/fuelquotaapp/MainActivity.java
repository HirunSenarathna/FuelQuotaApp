package com.example.fuelquotaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;


import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private static final String PREFS_NAME = "FuelAppPrefs";
    private static final String TAG = "MainActivity";

    private MaterialButton scanQRButton;
    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable the action bar/toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Fuel Station");
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        scanQRButton = findViewById(R.id.scanQRButton);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupClickListeners() {
        scanQRButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                startQRScanner();
            } else {
                requestCameraPermission();
            }
        });
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
    }

    private void startQRScanner() {
        Intent intent = new Intent(this, QRScannerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanner();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        try {
            // Clear all saved data
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            boolean success = editor.commit();

            Log.d(TAG, "Logout - SharedPreferences cleared: " + success);

            // Show logout message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Navigate back to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }

}