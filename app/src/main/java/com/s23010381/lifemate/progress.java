package com.s23010381.lifemate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class progress extends AppCompatActivity {
    private TextView tvSteps, tvSleep, tvWater, tvMood;

    // Define goals
    private static final int STEPS_GOAL = 10000;
    private static final float SLEEP_GOAL = 8.0f;
    private static final int WATER_GOAL = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        initializeViews();
        updateProgress();
        setupBottomNavigation();
    }

    private void initializeViews() {
        // Text views for metrics
        tvSteps = findViewById(R.id.tvSteps);
        tvSleep = findViewById(R.id.tvSleep);
        tvWater = findViewById(R.id.tvWater);
        tvMood = findViewById(R.id.tvMood);
    }

    private void updateProgress() {
        // Example current values (replace with actual data)
        int currentSteps = 8543;
        float currentSleep = 7.5f;
        int currentWater = 6;

        // Update TextViews with formatted text
        tvSteps.setText(String.format("Steps: %,d/%,d", currentSteps, STEPS_GOAL));
        tvSleep.setText(String.format("Sleep: %.1f/%.1f hours", currentSleep, SLEEP_GOAL));
        tvWater.setText(String.format("Water: %d/%d glasses", currentWater, WATER_GOAL));
        tvMood.setText("Mood: Happy");
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_add);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(progress.this, dashboard.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }
}