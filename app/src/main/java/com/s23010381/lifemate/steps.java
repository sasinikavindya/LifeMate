package com.s23010381.lifemate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class steps extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private TextView tvStepCount;
    private boolean isSensorAvailable = false;
    private int stepCount = 0;
    private boolean isCountingSteps = false;
    private MaterialButton btnStart;
    private MediaPlayer mediaPlayer;
    private BottomNavigationView bottomNav;
    private float initialStepCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        // Initialize views
        tvStepCount = findViewById(R.id.tvStepCount);
        btnStart = findViewById(R.id.btnStart);
        bottomNav = findViewById(R.id.bottomNav);

        // Set initial step count
        tvStepCount.setText("0");

        // Initialize MediaPlayer for alert sound
        mediaPlayer = MediaPlayer.create(this, R.raw.step_sound);

        // Initialize SensorManager and Sensor
        initializeSensor();

        // Set up button click listener
        btnStart.setOnClickListener(v -> toggleStepCounting());

        // Setup bottom navigation
        setupBottomNavigationView();
    }

    private void initializeSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            if (stepCounterSensor == null) {
                stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            }

            if (stepCounterSensor != null) {
                isSensorAvailable = true;
                btnStart.setEnabled(true);
            } else {
                isSensorAvailable = false;
                btnStart.setEnabled(false);
                Toast.makeText(this, "Step Counter Sensor not available on this device!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void toggleStepCounting() {
        if (!isCountingSteps) {
            // Start counting
            if (isSensorAvailable) {
                isCountingSteps = true;
                initialStepCount = -1;
                stepCount = 0;
                tvStepCount.setText("0");
                btnStart.setText("Stop Counting");
                btnStart.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                sensorManager.registerListener(this, stepCounterSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
                Toast.makeText(this, "Step counting started!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Stop counting
            stopCounting();
        }
    }

    private void stopCounting() {
        isCountingSteps = false;
        btnStart.setText("Start Counting");
        btnStart.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.accent_color)));
        sensorManager.unregisterListener(this);
        saveStepCount();
        Toast.makeText(this, "Final step count: " + stepCount, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isCountingSteps) return;

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount < 0) {
                initialStepCount = event.values[0];
                stepCount = 0;
            } else {
                stepCount = (int) (event.values[0] - initialStepCount);
            }
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepCount++;
        }

        runOnUiThread(() -> {
            tvStepCount.setText(String.valueOf(stepCount));
            playStepSound();
        });
    }

    private void setupBottomNavigationView() {
        bottomNav.setSelectedItemId(R.id.nav_add);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(steps.this, dashboard.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(steps.this, profile.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void playStepSound() {
        try {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveStepCount() {
        SharedPreferences prefs = getSharedPreferences("StepCounterPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        editor.putInt("steps_" + date, stepCount);
        editor.apply();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                Toast.makeText(this, "Step counter accuracy is low",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorAvailable && isCountingSteps) {
            sensorManager.registerListener(this, stepCounterSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorAvailable && isCountingSteps) {
            saveStepCount();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isSensorAvailable) {
            sensorManager.unregisterListener(this);
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}