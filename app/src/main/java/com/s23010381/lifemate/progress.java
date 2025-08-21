package com.s23010381.lifemate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class progress extends AppCompatActivity {

    private TextView tvSteps, tvSleep, tvWater, tvMood;

    // Goals
    private static final int STEPS_GOAL = 10000;
    private static final float SLEEP_GOAL = 8.0f;
    private static final int WATER_GOAL = 8;

    private SharedPreferences prefs;
    private String todayKey;

    // Declare the listener as a field to prevent garbage collection
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        prefs = getSharedPreferences("DailyProgress", MODE_PRIVATE);
        todayKey = getTodayKey();

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();

        // Initialize the listener
        listener = (sharedPreferences, key) -> {
            // Check if the changed key is for today's data
            if (key.startsWith("steps_" + todayKey) ||
                    key.startsWith("sleep_" + todayKey) ||
                    key.startsWith("water_" + todayKey) ||
                    key.startsWith("mood_" + todayKey)) {

                // If the key is relevant, update the UI
                loadProgress();
                Log.d("ProgressActivity", "UI updated due to preference change for key: " + key);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        todayKey = getTodayKey();  // Ensure we use the current date
        loadProgress();            // Reload today's progress

        // Register the listener when the activity is visible
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener to prevent memory leaks
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    private void initializeViews() {
        tvSteps = findViewById(R.id.tvSteps);
        tvSleep = findViewById(R.id.tvSleep);
        tvWater = findViewById(R.id.tvWater);
        tvMood = findViewById(R.id.tvMood);

        tvSteps.setClickable(true);
        tvSleep.setClickable(true);
        tvWater.setClickable(true);
        tvMood.setClickable(true);
    }

    private void loadProgress() {
        int currentSteps = prefs.getInt("steps_" + todayKey, 0);
        float currentSleep = prefs.getFloat("sleep_" + todayKey, 0f);
        int currentWater = prefs.getInt("water_" + todayKey, 0);
        String currentMood = prefs.getString("mood_" + todayKey, "Neutral");

        tvSteps.setText(String.format("Steps: %,d/%,d", currentSteps, STEPS_GOAL));
        tvSleep.setText(String.format("Sleep: %.1f/%.1f hours", currentSleep, SLEEP_GOAL));
        tvWater.setText(String.format("Water: %d/%d glasses", currentWater, WATER_GOAL));
        tvMood.setText("Mood: " + currentMood);
    }

    private void setupClickListeners() {
        tvSteps.setOnClickListener(v -> showInputDialog("Steps", "Enter steps:",
                String.valueOf(prefs.getInt("steps_" + todayKey, 0)), input -> {
                    try {
                        int steps = Integer.parseInt(input);
                        prefs.edit().putInt("steps_" + todayKey, steps).apply();
                    } catch (NumberFormatException e) {
                        Log.e("ProgressActivity", "Invalid input for steps", e);
                    }
                }));

        tvSleep.setOnClickListener(v -> showInputDialog("Sleep", "Enter hours of sleep:",
                String.valueOf(prefs.getFloat("sleep_" + todayKey, 0f)), input -> {
                    try {
                        float sleep = Float.parseFloat(input);
                        prefs.edit().putFloat("sleep_" + todayKey, sleep).apply();
                    } catch (NumberFormatException e) {
                        Log.e("ProgressActivity", "Invalid input for sleep", e);
                    }
                }));

        tvWater.setOnClickListener(v -> showInputDialog("Water", "Enter glasses of water:",
                String.valueOf(prefs.getInt("water_" + todayKey, 0)), input -> {
                    try {
                        int water = Integer.parseInt(input);
                        prefs.edit().putInt("water_" + todayKey, water).apply();
                    } catch (NumberFormatException e) {
                        Log.e("ProgressActivity", "Invalid input for water", e);
                    }
                }));

        tvMood.setOnClickListener(v -> showInputDialog("Mood", "Enter your mood:",
                prefs.getString("mood_" + todayKey, "Neutral"), input -> {
                    prefs.edit().putString("mood_" + todayKey, input).apply();
                }));
    }

    private void showInputDialog(String title, String message, String prefill, InputCallback callback) {
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setText(prefill);
        input.setSelection(prefill.length());

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String value = input.getText().toString().trim();
                    if (!value.isEmpty()) {
                        callback.onInput(value);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private interface InputCallback {
        void onInput(String input);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_add); // Progress page

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(progress.this, dashboard.class));
                return true;
            } else if (itemId == R.id.nav_add) {
                return true; // Already on progress
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(progress.this, setting.class));
                return true;
            }
            return false;
        });
    }

    private String getTodayKey() {
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
    }
}