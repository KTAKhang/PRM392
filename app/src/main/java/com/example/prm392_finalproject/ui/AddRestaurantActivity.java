package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.Restaurant;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddRestaurantActivity extends AppCompatActivity {

    private EditText etName, etAddress, etPhone, etNotes;
    private Switch swFav;
    private Button btnSave, btnPickImage;
    private ImageView imgPreview;
    private DatabaseHelper db;
    private int userId;
    private Uri imageUri;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        // Initialize views
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Setup toolbar
        topAppBar.setNavigationOnClickListener(v -> finish());
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.btnList) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (item.getItemId() == R.id.btnFavorite) {
                startActivity(new Intent(this, FavoriteActivity.class));
                return true;
            }
            return false;
        });

        // Setup bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_add);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_restaurants) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (itemId == R.id.nav_favorite) {
                startActivity(new Intent(this, FavoriteActivity.class));
                return true;
            } else if (itemId == R.id.nav_user) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // Initialize database and user
        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("FoodSpots", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        // Initialize form views
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etNotes = findViewById(R.id.etNotes);
        swFav = findViewById(R.id.swFav);
        btnSave = findViewById(R.id.btnSave);
        imgPreview = findViewById(R.id.imgPreview);
        btnPickImage = findViewById(R.id.btnPickImage);

        // Setup image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        try {
                            getContentResolver().takePersistableUriPermission(uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception ignored) {
                            // Some content providers don't support persistent permissions
                        }
                        imgPreview.setImageURI(uri);
                    }
                });

        // Setup button listeners
        btnPickImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                saveRestaurant();
            }
        });
    }

    /**
     * Validate toàn bộ form trước khi lưu
     * Tất cả các trường đều BẮT BUỘC ngoại trừ Ghi chú
     */
    private boolean validateForm() {
        boolean isValid = true;

        // Reset tất cả lỗi cũ
        etName.setError(null);
        etAddress.setError(null);
        etPhone.setError(null);
        etNotes.setError(null);

        // ======= 1️⃣ VALIDATE TÊN QUÁN ĂN =======
        String name = etName.getText().toString();
        if (name.trim().isEmpty()) {
            etName.setError("Vui lòng nhập tên quán ăn");
            if (isValid) etName.requestFocus();
            isValid = false;
        }
        // Chỉ cho chữ, số, khoảng trắng, dấu "(" ")" "-"
        else if (!name.matches("^[\\p{L}\\p{N}()\\-\\s]+$")) {
            etName.setError("Tên quán chỉ được chứa chữ, số, khoảng trắng, (), hoặc -");
            if (isValid) etName.requestFocus();
            isValid = false;
        }
        else if (!name.matches(".*[\\p{L}\\p{N}].*")) {
            etName.setError("Tên quán phải chứa ít nhất một chữ hoặc số");
            if (isValid) etName.requestFocus();
            isValid = false;
        }
        else if (name.length() < 3) {
            etName.setError("Tên quán ăn phải có ít nhất 3 ký tự");
            if (isValid) etName.requestFocus();
            isValid = false;
        }
        else if (name.length() > 100) {
            etName.setError("Tên quán ăn không được quá 100 ký tự");
            if (isValid) etName.requestFocus();
            isValid = false;
        }

        // ======= 2️⃣ VALIDATE ĐỊA CHỈ =======
        String address = etAddress.getText().toString();
        if (address.trim().isEmpty()) {
            etAddress.setError("Vui lòng nhập địa chỉ");
            if (isValid) etAddress.requestFocus();
            isValid = false;
        }
        // Cho phép chữ, số, khoảng trắng, , . / - ()
        else if (!address.matches("^[\\p{L}\\p{N}\\s,./()\\-]+$")) {
            etAddress.setError("Địa chỉ chỉ được chứa chữ, số và ký tự , . / - ()");
            if (isValid) etAddress.requestFocus();
            isValid = false;
        }
        else if (address.length() < 5) {
            etAddress.setError("Địa chỉ phải có ít nhất 5 ký tự");
            if (isValid) etAddress.requestFocus();
            isValid = false;
        }
        else if (address.length() > 200) {
            etAddress.setError("Địa chỉ không được quá 200 ký tự");
            if (isValid) etAddress.requestFocus();
            isValid = false;
        }

        // ======= 3️⃣ VALIDATE SỐ ĐIỆN THOẠI =======
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            if (isValid) etPhone.requestFocus();
            isValid = false;
        }
        // Chỉ nhận số, có độ dài 10 hoặc 11, bắt đầu bằng 0
        else if (!phone.matches("^0\\d{9,10}$")) {
        /*
         Giải thích:
         ^0         → bắt đầu bằng số 0
         \\d{9,10}  → theo sau là 9 hoặc 10 chữ số nữa (tổng là 10 hoặc 11)
         $          → kết thúc ở đó
        */
            etPhone.setError("Số điện thoại phải có 10 hoặc 11 chữ số và bắt đầu bằng số 0");
            if (isValid) etPhone.requestFocus();
            isValid = false;
        }

        // ======= 4️⃣ VALIDATE HÌNH ẢNH =======
        if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn hình ảnh cho quán ăn", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // ======= 5️⃣ VALIDATE GHI CHÚ =======
        String notes = etNotes.getText().toString();
        if (notes.trim().length() > 500) {
            etNotes.setError("Ghi chú không được quá 500 ký tự");
            if (isValid) etNotes.requestFocus();
            isValid = false;
        }

        // ======= 6️⃣ THÔNG BÁO CHUNG =======
        if (!isValid) {
            Toast.makeText(this, "Vui lòng kiểm tra lại thông tin đã nhập", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    /**
     * Lưu thông tin nhà hàng vào database
     */
    private void saveRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(etName.getText().toString().trim());
        restaurant.setAddress(etAddress.getText().toString().trim());
        restaurant.setPhone(etPhone.getText().toString().trim());
        restaurant.setNotes(etNotes.getText().toString().trim());
        restaurant.setFavorite(swFav.isChecked());
        restaurant.setUserId(userId);
        restaurant.setImageUri(imageUri != null ? imageUri.toString() : null);

        long id = db.insertRestaurant(restaurant);
        if (id != -1) {
            Toast.makeText(this, "Thêm quán ăn thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi thêm quán ăn", Toast.LENGTH_SHORT).show();
        }
    }
}