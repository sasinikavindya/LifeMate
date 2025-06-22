package com.s23010381.lifemate;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class SignIn extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogIn;
    private TextView tvSignUp;
    private ImageView ivPasswordToggle;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Setup status bar
        setupStatusBar();

        // Initialize views
        initViews();

        // Set click listeners
        setupClickListeners();
    }

    private void setupStatusBar() {
        // Make the app full screen with transparent status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        if (windowInsetsController != null) {
            // Make status bar icons light colored for dark background
            windowInsetsController.setAppearanceLightStatusBars(false);
        }

        // Set status bar color to transparent
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogIn = findViewById(R.id.btnLogIn);
        tvSignUp = findViewById(R.id.tvSignUp);
        ivPasswordToggle = findViewById(R.id.ivPasswordToggle);
    }

    private void setupClickListeners() {
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignUp();
            }
        });

        ivPasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Show loading state
        btnLogIn.setEnabled(false);
        btnLogIn.setText("Logging in...");

        UserDbHelper dbHelper = UserDbHelper.getInstance(this);

        // Check login credentials
        if (dbHelper.checkLogin(email, password)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            btnLogIn.setEnabled(true);
            btnLogIn.setText("Log In");
        }

    }

    private boolean validateInputs(String email, String password) {
        // Reset any previous errors
        etEmail.setError(null);
        etPassword.setError(null);

        // Validate email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void simulateLogin(String email, String password) {
        // Simulate network delay
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Reset button state
                btnLogIn.setEnabled(true);
                btnLogIn.setText("Log In");

                // For demo purposes, accept any valid email/password
                // Replace this with actual authentication logic
                if (email.contains("@") && password.length() >= 6) {
                    // Login successful
                    Toast.makeText(SignIn.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                } else {
                    // Login failed
                    Toast.makeText(SignIn.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        }, 2000); // 2 second delay to simulate network request
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
            isPasswordVisible = false;
        } else {
            // Show password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
            isPasswordVisible = true;
        }

        // Move cursor to end of text
        etPassword.setSelection(etPassword.getText().length());
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(SignIn.this, SignUp.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void navigateToMainActivity() {
        // Replace with your main activity after login
        Intent intent = new Intent(SignIn.this, dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


}