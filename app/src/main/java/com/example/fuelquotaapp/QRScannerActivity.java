package com.example.fuelquotaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Collection;

public class QRScannerActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;
    private boolean isScanning = true;
    private ApiService apiService;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null && isScanning) {
                isScanning = false;
                handleQRResult(result.getText());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        barcodeView = findViewById(R.id.barcode_scanner);
        apiService = new ApiService(this);

        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Scan Vehicle QR Code");
    }

    private void handleQRResult(String result) {
        Log.d("QRScanner", "QR Code scanned: " + result);

        // Parse QR data and extract vehicle information (without quota)
        VehicleInfo vehicleInfo = parseQRData(result);

        if (vehicleInfo != null) {
            // Fetch current quota from database
            fetchVehicleQuota(vehicleInfo);
        } else {
            Toast.makeText(this, "Invalid QR Code format", Toast.LENGTH_SHORT).show();
            isScanning = true;
        }
    }

    private void fetchVehicleQuota(VehicleInfo vehicleInfo) {
        // Show loading message
        Toast.makeText(this, "Fetching vehicle data...", Toast.LENGTH_SHORT).show();

        apiService.getVehicleQuota(vehicleInfo.getVehicleNumber(), new ApiCallback<VehicleInfo>() {
            @Override
            public void onSuccess(VehicleInfo updatedVehicleInfo) {
                runOnUiThread(() -> {
                    // Update the vehicle info with current quota from database
                    vehicleInfo.setRemainingQuota(updatedVehicleInfo.getRemainingQuota());

                    // Navigate to FuelPumpActivity with updated data
                    Intent intent = new Intent(QRScannerActivity.this, FuelPumpActivity.class);
                    intent.putExtra("vehicle_info", vehicleInfo);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(QRScannerActivity.this,
                            "Error fetching vehicle data: " + error, Toast.LENGTH_LONG).show();
                    isScanning = true; // Allow scanning again
                });
            }
        });
    }

    private VehicleInfo parseQRData(String qrData) {
        try {
            VehicleInfo info = new VehicleInfo();
            String[] lines = qrData.split("\n");

            for (String line : lines) {
                if (line.startsWith("Vehicle Number:")) {
                    info.setVehicleNumber(line.split(":")[1].trim());
                } else if (line.startsWith("Chassis Number:")) {
                    info.setChassisNumber(line.split(":")[1].trim());
                } else if (line.startsWith("Vehicle Type:")) {
                    info.setVehicleType(line.split(":")[1].trim());
                } else if (line.startsWith("Fuel Type:")) {
                    info.setFuelType(line.split(":")[1].trim());
                }
                // Remove the Remaining Quota Limit parsing since we'll fetch it from DB
            }

            // Validate that we have the essential information
            if (info.getVehicleNumber() != null && info.getChassisNumber() != null) {
                return info;
            } else {
                return null;
            }

        } catch (Exception e) {
            Log.e("QRScanner", "Error parsing QR data", e);
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
