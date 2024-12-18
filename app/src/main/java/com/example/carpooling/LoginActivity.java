package com.example.carpooling;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private MyDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String userType = sharedPref.getString("userType", null);

        SharedPreferences.Editor editor = sharedPref.edit();

        dbHelper = new MyDBHelper(this);

        //String userType = getIntent().getStringExtra("USER_TYPE");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Сите полиња се задолжителни!", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = null;

            boolean isValidUser = false;

            if ("Driver".equals(userType)) {
                cursor = db.rawQuery("SELECT email, password FROM Driver WHERE email = ? AND password = ?", new String[]{email, password});
                if (cursor.getCount() > 0) {
                    isValidUser = true;
                    editor.putString("email", email);
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("userType", "driver");
                    editor.apply();
                    startActivity(new Intent(LoginActivity.this, DriverMainActivity.class));
                    finish();
                }
            } else if ("Passenger".equals(userType)) {
                cursor = db.rawQuery("SELECT email, password FROM Passenger WHERE email = ? AND password = ?", new String[]{email, password});
                if (cursor.getCount() > 0) {
                    isValidUser = true;
                    editor.putString("email", email);
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("userType", "passenger");
                    editor.apply();
                    startActivity(new Intent(LoginActivity.this, PassengerMainActivity.class));
                    finish();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Невалиден тип на корисник!", Toast.LENGTH_SHORT).show();
            }

            if (cursor != null) {
                cursor.close();
            }

            if (!isValidUser) {
                Toast.makeText(LoginActivity.this, "Невалиден корисник!", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
