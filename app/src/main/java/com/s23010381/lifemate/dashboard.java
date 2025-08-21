package com.s23010381.lifemate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class dashboard extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize cards
        CardView cardProgress = findViewById(R.id.cardProgress);
        CardView cardMood = findViewById(R.id.cardMood);
        CardView cardSteps = findViewById(R.id.cardSteps);
        CardView cardWater = findViewById(R.id.cardWater);
        CardView cardSleep = findViewById(R.id.cardSleep);
        CardView cardLocation = findViewById(R.id.cardlocationFind);

        // Progress card click listener
        cardProgress.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, progress.class);
            startActivity(intent);
        });

        // Mood card click listener
        cardMood.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, Mood.class);
            startActivity(intent);
        });

        // Steps card click listener
        cardSteps.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, steps.class);
            startActivity(intent);
        });

        // Water card click listener
        cardWater.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, water.class);
            startActivity(intent);
        });

        // Sleep card click listener
        cardSleep.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, sleep.class);
            startActivity(intent);
        });

        // Location card click listener
        cardLocation.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, locationFind.class);
            startActivity(intent);
        });

        // Bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                return true;
            } else if (item.getItemId() == R.id.nav_add) {
                startActivity(new Intent(dashboard.this, progress.class));
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(dashboard.this, setting.class));
                return true;
            }
            return false;
        });
    }

    // Add error handling method
    private void navigateToActivity(Class<?> activityClass) {
        try {
            Intent intent = new Intent(dashboard.this, activityClass);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Feature coming soon!", Toast.LENGTH_SHORT).show();
        }
    }
}