package com.example.carpooling;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.content.Intent;

public class UsetTypeSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_uset_type_selection);

        Button btnPassenger = findViewById(R.id.btnPassenger);
        Button btnDriver = findViewById(R.id.btnDriver);

        btnPassenger.setOnClickListener(view -> openNextActivity("Passenger"));
        btnDriver.setOnClickListener(view -> openNextActivity("Driver"));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void openNextActivity(String userType) {
        SharedPreferences sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userType", userType);
        editor.apply();

        Intent intent = new Intent(UsetTypeSelectionActivity.this, LoginRegisterActivity.class);
        intent.putExtra("USER_TYPE", userType);
        startActivity(intent);
    }

}