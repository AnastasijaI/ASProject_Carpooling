package com.example.carpooling;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import android.content.SharedPreferences;

public class LoginRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_register);

        SharedPreferences sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String tempUserType = sharedPref.getString("userType", null);

        if (tempUserType == null) {
            tempUserType = getIntent().getStringExtra("USER_TYPE");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userType", tempUserType);
            editor.apply();
        }

        final String userType = tempUserType;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(view -> openLoginActivity(userType));
        btnRegister.setOnClickListener(view -> openRegisterActivity(userType));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void openLoginActivity(String userType) {
        Intent intent = new Intent(LoginRegisterActivity.this, LoginActivity.class);
        intent.putExtra("USER_TYPE", userType);
        startActivity(intent);
    }

    private void openRegisterActivity(String userType) {
        Intent intent = new Intent(LoginRegisterActivity.this, RegisterActivity.class);
        intent.putExtra("USER_TYPE", userType);
        startActivity(intent);
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}