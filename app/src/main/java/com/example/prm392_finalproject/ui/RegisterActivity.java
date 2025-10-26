package com.example.prm392_finalproject.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.User;

public class RegisterActivity extends AppCompatActivity {
    EditText etName, etEmail, etPassword;
    Button btnLogin,btnRegister;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnGoToLogin);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                android.widget.Toast.makeText(this, "Nhập đủ thông tin", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            if (db.isEmailExists(email)) {
                android.widget.Toast.makeText(this, "Email đã tồn tại", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            User u = new User();
            u.setUsername(name);
            u.setEmail(email);
            u.setPassword(pass);
            boolean ok = db.registerUser(u);
            if (ok) {
                android.widget.Toast.makeText(this, "Đăng ký thành công", android.widget.Toast.LENGTH_SHORT).show();
                finish();
            } else {
                android.widget.Toast.makeText(this, "Lỗi đăng ký", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        btnLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }
}
