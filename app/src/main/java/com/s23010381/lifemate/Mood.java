package com.s23010381.lifemate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Mood extends AppCompatActivity {
    private RadioGroup rgMood;
    private EditText etNotes;
    private TextView tvCurrentMood;
    private Button btnLog, btnReset;
    private MoodTrackerDbHelper dbHelper;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        // Initialize views
        rgMood = findViewById(R.id.rgMood);
        etNotes = findViewById(R.id.etNotes);
        tvCurrentMood = findViewById(R.id.tvCurrentMood);
        btnLog = findViewById(R.id.btnLog);
        btnReset = findViewById(R.id.btnReset);
        bottomNav = findViewById(R.id.bottomNav);
        dbHelper = new MoodTrackerDbHelper(this);

        updateMoodDisplay();
        setupBottomNavigation();

        btnLog.setOnClickListener(v -> {
            int selectedId = rgMood.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Select a mood", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedRadioButton = findViewById(selectedId);
            String mood = selectedRadioButton.getText().toString();
            String notes = etNotes.getText().toString().trim();
            if (dbHelper.logMood(mood, notes)) {
                Toast.makeText(this, "Mood logged!", Toast.LENGTH_SHORT).show();
                updateMoodDisplay();
            } else {
                Toast.makeText(this, "Failed to log mood", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> {
            if (dbHelper.resetTodaysMood()) {
                Toast.makeText(this, "Mood reset", Toast.LENGTH_SHORT).show();
                updateMoodDisplay();
            } else {
                Toast.makeText(this, "No mood to reset", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_add); // Set current tab as selected

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(Mood.this, dashboard.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(Mood.this, progress.class));
                return true; // Already in mood activity
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(Mood.this, setting.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void updateMoodDisplay() {
        String mood = dbHelper.getTodaysMood();
        if (mood != null) {
            tvCurrentMood.setText("Today's Mood: " + mood);
        } else {
            tvCurrentMood.setText("Today's Mood: Not logged");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}