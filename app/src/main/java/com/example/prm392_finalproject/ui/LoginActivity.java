package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.User;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;
    DatabaseHelper db;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("MyLibrary", MODE_PRIVATE);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            if (email.isEmpty() || pass.isEmpty()) {
                android.widget.Toast.makeText(this, "Nhập đủ thông tin", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            User u = db.loginUser(email, pass);
            if (u != null) {
                prefs.edit().putInt("user_id", u.getId()).apply();
                prefs.edit().putString("username", u.getUsername()).apply();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                android.widget.Toast.makeText(this, "Sai email hoặc mật khẩu", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}
