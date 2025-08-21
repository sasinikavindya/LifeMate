package com.s23010381.lifemate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class setting extends AppCompatActivity {

    private ImageButton btnBack;
    private LinearLayout layoutChangePassword, layoutExportData, layoutDeleteAccount;
    private SwitchMaterial switchNotifications, switchDarkMode;
    private TextInputEditText etEmail;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initializeViews();
        prefs = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);

        loadPreferences();
        setupListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        layoutChangePassword = findViewById(R.id.layoutChangePassword);
        layoutExportData = findViewById(R.id.layoutExportData);
        layoutDeleteAccount = findViewById(R.id.layoutDeleteAccount);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        etEmail = findViewById(R.id.tvEmail); // now editable
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Change password
        layoutChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // Export data
        layoutExportData.setOnClickListener(v -> Toast.makeText(this, "Export Data clicked", Toast.LENGTH_SHORT).show());

        // Delete account
        layoutDeleteAccount.setOnClickListener(v -> deleteAccount());

        // Notifications switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications", isChecked).apply();
            Toast.makeText(this, "Notifications " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        // Dark mode switch
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Save email when focus is lost
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newEmail = etEmail.getText().toString().trim();
                if (!newEmail.isEmpty()) {
                    prefs.edit().putString("email", newEmail).apply();
                    Toast.makeText(this, "Email updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadPreferences() {
        // Load email
        String savedEmail = prefs.getString("email", "user@example.com");
        etEmail.setText(savedEmail);

        // Load notifications
        switchNotifications.setChecked(prefs.getBoolean("notifications", true));
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        final EditText currentPassword = new EditText(this);
        currentPassword.setHint("Current Password");
        currentPassword.setInputType(0x00000081); // textPassword
        layout.addView(currentPassword);

        final EditText newPassword = new EditText(this);
        newPassword.setHint("New Password");
        newPassword.setInputType(0x00000081); // textPassword
        layout.addView(newPassword);

        builder.setView(layout);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String savedPassword = prefs.getString("password", "1234"); // default password
            String current = currentPassword.getText().toString();
            String newPass = newPassword.getText().toString();

            if (TextUtils.isEmpty(current) || TextUtils.isEmpty(newPass)) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!current.equals(savedPassword)) {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit().putString("password", newPass).apply();
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    prefs.edit().clear().apply();
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(setting.this, SignIn.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
