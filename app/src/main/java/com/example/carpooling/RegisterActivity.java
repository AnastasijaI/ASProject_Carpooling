package com.example.carpooling;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String userType = getIntent().getStringExtra("USER_TYPE");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        EditText edtName = findViewById(R.id.edtName);
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Сите полиња се задолжителни!", Toast.LENGTH_SHORT).show();
                return;
            }

            MyDBHelper dbHelper = new MyDBHelper(RegisterActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor passengerCursor = db.rawQuery("SELECT email FROM Passenger WHERE email = ?", new String[]{email});
            Cursor driverCursor = db.rawQuery("SELECT email FROM Driver WHERE email = ?", new String[]{email});

            if (passengerCursor.getCount() > 0 && "Passenger".equals(userType)) {
                Toast.makeText(RegisterActivity.this, "Е-мејлот веќе постои во табелата за патници!", Toast.LENGTH_SHORT).show();
            } else if (driverCursor.getCount() > 0 && "Driver".equals(userType)) {
                Toast.makeText(RegisterActivity.this, "Е-мејлот веќе постои во табелата за возачи!", Toast.LENGTH_SHORT).show();
            } else if (passengerCursor.getCount() > 0 && "Driver".equals(userType)) {
                Toast.makeText(RegisterActivity.this, "Е-мејлот веќе постои како патник. Продолжуваме со регистрација како возач!", Toast.LENGTH_SHORT).show();

                Intent driverIntent = new Intent(RegisterActivity.this, DriverActivity.class);
                driverIntent.putExtra("USER_NAME", name);
                driverIntent.putExtra("USER_EMAIL", email);
                driverIntent.putExtra("USER_PASSWORD", password);
                startActivity(driverIntent);
                finish();
            } else if (driverCursor.getCount() > 0 && "Passenger".equals(userType)) {
                Toast.makeText(RegisterActivity.this, "Е-мејлот веќе постои како возач. Продолжуваме со регистрација како патник!", Toast.LENGTH_SHORT).show();

                dbHelper.insertPassenger(db, name, email, password);
                Toast.makeText(RegisterActivity.this, "Успешно регистрирани како патник!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                if ("Driver".equals(userType)) {
                    Intent driverIntent = new Intent(RegisterActivity.this, DriverActivity.class);
                    driverIntent.putExtra("USER_NAME", name);
                    driverIntent.putExtra("USER_EMAIL", email);
                    driverIntent.putExtra("USER_PASSWORD", password);
                    startActivity(driverIntent);
                } else {
                    dbHelper.insertPassenger(db, name, email, password);
                    Toast.makeText(RegisterActivity.this, "Успешно регистрирани како патник!", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
            passengerCursor.close();
            driverCursor.close();
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