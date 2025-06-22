package com.s23010381.lifemate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.google.android.material.button.MaterialButton;

public class WelcomeScreen extends AppCompatActivity {

    private MaterialButton btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Make status bar transparent
        setupStatusBar();

        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && Intent.ACTION_MAIN.equals(getIntent().getAction())) {
            finish();
            return;
        }

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
        btnGetStarted = findViewById(R.id.btnGetStarted);
    }

    private void setupClickListeners() {
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign in button click
                navigateToSignIn();
            }
        });
    }

    private void navigateToSignIn() {
        // Navigate to sign in activity
        Intent intent = new Intent(WelcomeScreen.this, SignIn.class);
        startActivity(intent);
        // Add smooth transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void navigateToSignUp() {
        // Navigate to sign up activity
        Intent intent = new Intent(WelcomeScreen.this, SignUp.class);
        startActivity(intent);
        // Add smooth transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}