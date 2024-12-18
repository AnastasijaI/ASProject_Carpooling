package com.example.carpooling;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import java.util.Calendar;

public class DriverActivity extends AppCompatActivity {

    private EditText edtAvailabilityStart, edtAvailabilityEnd, edtCarType, edtCarColor, edtCarPlate;
    private Button btnRegisterDriver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edtAvailabilityStart = findViewById(R.id.edtAvailabilityStart);
        edtAvailabilityEnd = findViewById(R.id.edtAvailabilityEnd);


        edtAvailabilityStart.setOnClickListener(v -> showTimePicker(edtAvailabilityStart));
        edtAvailabilityEnd.setOnClickListener(v -> showTimePicker(edtAvailabilityEnd));


        EditText edtCarType = findViewById(R.id.edtCarType);
        EditText edtCarColor = findViewById(R.id.edtCarColor);
        EditText edtCarPlate = findViewById(R.id.edtCarPlate);
        EditText edtAvailabilityStart = findViewById(R.id.edtAvailabilityStart);
        EditText edtAvailabilityEnd = findViewById(R.id.edtAvailabilityEnd);
        Button btnRegisterDriver = findViewById(R.id.btnRegisterDriver);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");
        String userEmail = intent.getStringExtra("USER_EMAIL");
        String userPassword = intent.getStringExtra("USER_PASSWORD");

        btnRegisterDriver.setOnClickListener(v -> {
            String carBrand = edtCarType.getText().toString().trim();
            String carColor = edtCarColor.getText().toString().trim();
            String licensePlate = edtCarPlate.getText().toString().trim();

            String availabilityStart = edtAvailabilityStart.getText().toString().trim();
            String availabilityEnd = edtAvailabilityEnd.getText().toString().trim();

            String startAvailability = edtAvailabilityStart.getText().toString().trim();
            String endAvailability = edtAvailabilityEnd.getText().toString().trim();

            if (startAvailability.isEmpty() || endAvailability.isEmpty()) {
                Toast.makeText(DriverActivity.this, "Внесете достапност!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (carBrand.isEmpty() || carColor.isEmpty() || licensePlate.isEmpty() ||
                    availabilityStart.isEmpty() || availabilityEnd.isEmpty()) {
                Toast.makeText(DriverActivity.this, "Пополнете ги сите полиња!", Toast.LENGTH_SHORT).show();
                return;
            }

            MyDBHelper dbHelper = new MyDBHelper(DriverActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.insertDriver(db, userName, userEmail, userPassword, carBrand, carColor, licensePlate, availabilityStart, availabilityEnd);

            Toast.makeText(DriverActivity.this, "Возачот е успешно регистриран!", Toast.LENGTH_SHORT).show();
            Intent bckToLoginIntent = new Intent(DriverActivity.this, LoginRegisterActivity.class);
            startActivity(bckToLoginIntent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void showTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            String time = String.format("%02d:%02d", selectedHour, selectedMinute);
            editText.setText(time);
        }, hour, minute, true);
        timePickerDialog.show();
    }
}