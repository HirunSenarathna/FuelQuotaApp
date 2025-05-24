package com.example.fuelquotaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String PREFS_NAME = "FuelAppPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USERNAME = "username";

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton loginButton;
    private ProgressBar progressBar;
    private TextView errorTextView;

    private ApiService apiService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        initViews();
        setupClickListeners();

        // Initialize ApiService and Handler for UI updates
        apiService = new ApiService();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void initViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        // Hide error message
        hideError();

        // Get input values
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(username)) {
            showError("Please enter your username");
            usernameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showError("Please enter your password");
            passwordEditText.requestFocus();
            return;
        }

        // Show loading state
        showLoading(true);

        // Perform login using ApiService
        performLogin(username, password);
    }

    private void performLogin(String username, String password) {
        // Create login request
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Call login API
        apiService.login(loginRequest, new ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                // Switch to main thread for UI updates
                mainHandler.post(() -> {
                    Log.d(TAG, "Login successful");
                    handleLoginSuccess(response, username);
                });
            }

            @Override
            public void onError(String error) {
                // Switch to main thread for UI updates
                mainHandler.post(() -> {
                    Log.e(TAG, "Login error: " + error);
                    handleLoginError(error);
                });
            }
        });
    }

    private void handleLoginSuccess(LoginResponse response, String username) {
        showLoading(false);

        try {
            // Extract token from response
            String accessToken = response.getAccessToken();
            long expiresIn = response.getExpiresIn();

            // Save login data
            saveLoginData(accessToken, username, expiresIn);

            // Show success message
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Navigate to main activity
            navigateToMainActivity();

        } catch (Exception e) {
            Log.e(TAG, "Error processing login response", e);
            showError("Error processing login response");
        }
    }

    private void handleLoginError(String errorMessage) {
        showLoading(false);
        showError(errorMessage);
    }

    private void saveLoginData(String accessToken, String username, long expiresIn) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_USERNAME, username);
        editor.putLong("expires_in", expiresIn);
        editor.putLong("login_time", System.currentTimeMillis());

        editor.apply();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(KEY_ACCESS_TOKEN, null);

        if (token == null) {
            return false;
        }

        // Check if token is expired (optional)
        long loginTime = prefs.getLong("login_time", 0);
        long expiresIn = prefs.getLong("expires_in", 0);
        long currentTime = System.currentTimeMillis();

        // If token is expired, clear it
        if (currentTime - loginTime > expiresIn * 1000) {
            clearLoginData();
            return false;
        }

        return true;
    }

    private void clearLoginData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!show);

        if (show) {
            loginButton.setText("Signing In...");
        } else {
            loginButton.setText("Sign In");
        }
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorTextView.setVisibility(View.GONE);
    }
}
