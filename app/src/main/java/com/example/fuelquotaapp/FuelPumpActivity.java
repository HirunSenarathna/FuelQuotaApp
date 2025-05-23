package com.example.fuelquotaapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class FuelPumpActivity extends AppCompatActivity {

    private TextView vehicleNumberText, chassisNumberText, vehicleTypeText,
            fuelTypeText, remainingQuotaText;
    private EditText pumpedAmountInput;
    private MaterialButton[] numberButtons;
    private MaterialButton deleteButton, confirmButton;
    private VehicleInfo vehicleInfo;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_pump);

        vehicleInfo = (VehicleInfo) getIntent().getSerializableExtra("vehicle_info");
        apiService = new ApiService();

        initViews();
        displayVehicleInfo();
        setupNumberPad();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Fuel Pump");
    }

    private void initViews() {
        vehicleNumberText = findViewById(R.id.vehicleNumberText);
        chassisNumberText = findViewById(R.id.chassisNumberText);
        vehicleTypeText = findViewById(R.id.vehicleTypeText);
        fuelTypeText = findViewById(R.id.fuelTypeText);
        remainingQuotaText = findViewById(R.id.remainingQuotaText);
        pumpedAmountInput = findViewById(R.id.pumpedAmountInput);

        // Number buttons
        numberButtons = new MaterialButton[10];
        numberButtons[0] = findViewById(R.id.btn0);
        numberButtons[1] = findViewById(R.id.btn1);
        numberButtons[2] = findViewById(R.id.btn2);
        numberButtons[3] = findViewById(R.id.btn3);
        numberButtons[4] = findViewById(R.id.btn4);
        numberButtons[5] = findViewById(R.id.btn5);
        numberButtons[6] = findViewById(R.id.btn6);
        numberButtons[7] = findViewById(R.id.btn7);
        numberButtons[8] = findViewById(R.id.btn8);
        numberButtons[9] = findViewById(R.id.btn9);

        deleteButton = findViewById(R.id.deleteButton);
        confirmButton = findViewById(R.id.confirmButton);
    }

    private void displayVehicleInfo() {
        if (vehicleInfo != null) {
            vehicleNumberText.setText("Vehicle: " + vehicleInfo.getVehicleNumber());
            chassisNumberText.setText("Chassis: " + vehicleInfo.getChassisNumber());
            vehicleTypeText.setText("Type: " + vehicleInfo.getVehicleType());
            fuelTypeText.setText("Fuel: " + vehicleInfo.getFuelType());
            remainingQuotaText.setText(String.format("%.2f L", vehicleInfo.getRemainingQuota()));
        }
    }

    private void setupNumberPad() {
        // Number button listeners
        for (int i = 0; i < numberButtons.length; i++) {
            final int number = i;
            numberButtons[i].setOnClickListener(v -> appendNumber(String.valueOf(number)));
        }

        // Decimal point button
        findViewById(R.id.btnDecimal).setOnClickListener(v -> appendNumber("."));

        // Delete button
        deleteButton.setOnClickListener(v -> deleteLastCharacter());

        // Confirm button
        confirmButton.setOnClickListener(v -> processFuelPump());
    }

    private void appendNumber(String number) {
        String currentText = pumpedAmountInput.getText().toString();

        Log.d("FuelPump", "Current text: " + currentText + ", Adding: " + number);

        // Handle decimal point logic
        if (number.equals(".") && currentText.contains(".")) {
            return;
        }

        pumpedAmountInput.setText(currentText + number);

        Log.d("FuelPump", "New text set: " + pumpedAmountInput.getText().toString());
    }

    private void deleteLastCharacter() {
        String currentText = pumpedAmountInput.getText().toString();
        if (!currentText.isEmpty()) {
            pumpedAmountInput.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    private void processFuelPump() {
        String amountStr = pumpedAmountInput.getText().toString().trim();

        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Please enter the pumped amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double pumpedAmount = Double.parseDouble(amountStr);

            if (pumpedAmount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pumpedAmount > vehicleInfo.getRemainingQuota()) {
                Toast.makeText(this, "Amount exceeds remaining quota!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create fuel transaction request
            FuelTransactionRequest request = new FuelTransactionRequest();
            request.setVehicleNumber(vehicleInfo.getVehicleNumber());
            request.setChassisNumber(vehicleInfo.getChassisNumber());
            request.setPumpedAmount(pumpedAmount);

            // Process the transaction
            processFuelTransaction(request);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void processFuelTransaction(FuelTransactionRequest request) {
        // Disable confirm button to prevent multiple clicks
        confirmButton.setEnabled(false);
        confirmButton.setText("Processing...");

        apiService.processFuelTransaction(request, new ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(FuelPumpActivity.this,
                            "Fuel pumped successfully! SMS sent to vehicle owner.",
                            Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(FuelPumpActivity.this,
                            "Error: " + error, Toast.LENGTH_LONG).show();
                    confirmButton.setEnabled(true);
                    confirmButton.setText("Confirm");
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
