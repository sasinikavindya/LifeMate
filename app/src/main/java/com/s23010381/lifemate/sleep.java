package com.s23010381.lifemate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class sleep extends AppCompatActivity {
    private EditText etSleepHours, etSleepNotes;
    private TextView tvCurrentSleep;
    private Button btnAddSleep, btnResetSleep;
    private SleepDbHelper dbHelper;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        // Initialize views
        etSleepHours = findViewById(R.id.etSleepHours);
        etSleepNotes = findViewById(R.id.etSleepNotes);
        tvCurrentSleep = findViewById(R.id.tvCurrentSleep);
        btnAddSleep = findViewById(R.id.btnAddSleep);
        btnResetSleep = findViewById(R.id.btnResetSleep);
        bottomNav = findViewById(R.id.bottomNav);
        dbHelper = new SleepDbHelper(this);

        updateSleepDisplay();
        setupBottomNavigation();

        btnAddSleep.setOnClickListener(v -> {
            String hoursStr = etSleepHours.getText().toString().trim();
            String notes = etSleepNotes.getText().toString().trim();
            if (!hoursStr.isEmpty()) {
                try {
                    double hours = Double.parseDouble(hoursStr);
                    if (hours > 0) {
                        boolean inserted = dbHelper.addSleepLog(hours, notes);
                        if (inserted) {
                            Toast.makeText(this, "Sleep log added!", Toast.LENGTH_SHORT).show();
                            updateSleepDisplay();
                            etSleepHours.setText("");
                            etSleepNotes.setText("");
                        } else {
                            Toast.makeText(this, "Failed to add sleep log", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Enter a positive number", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter hours of sleep", Toast.LENGTH_SHORT).show();
            }
        });

        btnResetSleep.setOnClickListener(v -> {
            boolean reset = dbHelper.resetTodaysSleep();
            if (reset) {
                Toast.makeText(this, "Sleep log reset", Toast.LENGTH_SHORT).show();
                updateSleepDisplay();
            } else {
                Toast.makeText(this, "No sleep log to reset", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_add);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(sleep.this, dashboard.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                return true; // Already in sleep activity
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(sleep.this, profile.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void updateSleepDisplay() {
        double totalHours = dbHelper.getTodaysSleepTotal();
        String notes = dbHelper.getTodaysSleepNotes();
        String displayText = "Today: " + totalHours + " hours";
        if (!notes.isEmpty()) {
            displayText += "\nNotes: " + notes;
        }
        tvCurrentSleep.setText(displayText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}