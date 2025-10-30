package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.User;

public class ProfileActivity extends AppCompatActivity {
    TextView tvName, tvEmail;
    Button btnEdit, btnLogout;
    DatabaseHelper db;
    SharedPreferences prefs;
    int userId;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("MyLibrary", MODE_PRIVATE);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        btnEdit = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);

        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Nút Cập nhật chuyển sang EditProfileActivity
        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, EditProfileActivity.class);
            startActivity(i);
        });

        // Nút Đăng xuất xóa prefs và quay về LoginActivity
        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });

        loadUserIntoViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserIntoViews(); // reload thông tin khi quay lại từ EditProfileActivity
    }

    private void loadUserIntoViews() {
        currentUser = db.getUserById(userId);
        if (currentUser != null) {
            tvName.setText(currentUser.getUsername());
            tvEmail.setText(currentUser.getEmail());
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }
}
