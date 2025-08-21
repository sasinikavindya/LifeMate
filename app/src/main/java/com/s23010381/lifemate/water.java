package com.s23010381.lifemate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class water extends AppCompatActivity {
    private EditText etWater;
    private TextView tvCurrentWater;
    private Button btnAdd, btnReset;
    private WaterLogDbHelper dbHelper;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        // Initialize views
        etWater = findViewById(R.id.etWater);
        tvCurrentWater = findViewById(R.id.tvCurrentWater);
        btnAdd = findViewById(R.id.btnAdd);
        btnReset = findViewById(R.id.btnReset);
        bottomNav = findViewById(R.id.bottomNav);
        dbHelper = new WaterLogDbHelper(this);

        updateWaterDisplay();
        setupBottomNavigation();

        btnAdd.setOnClickListener(v -> {
            String amountStr = etWater.getText().toString().trim();
            if (!amountStr.isEmpty()) {
                int amount = Integer.parseInt(amountStr);
                if (amount > 0) {
                    if (dbHelper.addWaterLog(amount)) {
                        Toast.makeText(this, "Water added!", Toast.LENGTH_SHORT).show();
                        updateWaterDisplay();
                        etWater.setText("");
                    } else {
                        Toast.makeText(this, "Failed to add water", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Amount must be positive", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> {
            if (dbHelper.resetTodaysWater()) {
                Toast.makeText(this, "Reset successful", Toast.LENGTH_SHORT).show();
                updateWaterDisplay();
            } else {
                Toast.makeText(this, "Failed to reset", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_add); // Set water as selected

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(water.this, dashboard.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_add) {
                return true; // We're already in water activity
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(water.this, setting.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void updateWaterDisplay() {
        int total = dbHelper.getTodaysWaterTotal();
        tvCurrentWater.setText("Today: " + total + " glasses");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}