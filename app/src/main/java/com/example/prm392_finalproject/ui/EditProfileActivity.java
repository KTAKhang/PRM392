package com.example.prm392_finalproject.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.User;

public class EditProfileActivity extends AppCompatActivity {
    EditText etName, etEmail, etPassword;
    Button btnSave;
    DatabaseHelper db;
    SharedPreferences prefs;
    int userId;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("FoodSpots", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnSave);

        // load current user
        user = db.getUserById(userId);
        if (user != null) {
            etName.setText(user.getUsername());
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // optional: nếu email đổi cần kiểm tra trùng (bạn có sẵn isEmailExists)
            // nhưng isEmailExists hiện không phân biệt id — nếu muốn, update method để exclude chính user này.
            user.setUsername(name);
            user.setEmail(email);
            user.setPassword(pass);

            boolean ok = db.updateUser(user);
            if (ok) {
                // cập nhật username trong prefs để HomeActivity hiển thị đúng
                prefs.edit().putString("username", name).apply();
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
