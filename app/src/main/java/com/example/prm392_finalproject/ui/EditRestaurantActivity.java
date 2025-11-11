package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class EditRestaurantActivity extends AppCompatActivity {
    private EditText etName, etAddress, etPhone, etNotes;
    private Switch swFav;
    private Button btnUpdate, btnDelete, btnPickImage;
    private ImageView imgPreview;
    private DatabaseHelper db;
    private int restaurantId;
    private Restaurant restaurant;
    private Uri imageUri;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurant);

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
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_restaurants) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(this, AddRestaurantActivity.class));
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

        // Initialize database
        db = new DatabaseHelper(this);

        // Initialize form views
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etNotes = findViewById(R.id.etNotes);
        swFav = findViewById(R.id.swFav);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        imgPreview = findViewById(R.id.imgPreview);
        btnPickImage = findViewById(R.id.btnPickImage);

        // Get restaurant ID from intent
        restaurantId = getIntent().getIntExtra("restaurant_id", -1);
        if (restaurantId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy quán ăn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup image picker (giống y hệt AddRestaurantActivity)
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

        // Load restaurant data
        loadRestaurant();

        // Setup button listeners
        btnPickImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnUpdate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Nhập tên quán ăn", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đảm bảo restaurant object có đầy đủ thông tin
            if (restaurant == null) {
                Toast.makeText(this, "Lỗi: Không tìm thấy quán ăn", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Cập nhật thông tin từ form
            restaurant.setName(name);
            restaurant.setAddress(etAddress.getText().toString().trim());
            restaurant.setPhone(etPhone.getText().toString().trim());
            restaurant.setNotes(etNotes.getText().toString());
            restaurant.setFavorite(swFav.isChecked());

            // Chỉ cập nhật image URI nếu người dùng chọn hình ảnh mới
            // Nếu không, giữ nguyên hình ảnh cũ (đã có trong restaurant object)
            if (imageUri != null) {
                restaurant.setImageUri(imageUri.toString());
            }
            // Nếu imageUri == null, restaurant.getImageUri() vẫn giữ giá trị cũ từ database

            boolean ok = db.updateRestaurant(restaurant);
            if (ok) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                // Quay lại màn hình chi tiết (RestaurantDetailActivity)
                // onResume() của RestaurantDetailActivity sẽ tự động load lại dữ liệu mới
                finish();
            } else {
                Toast.makeText(this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            // Show confirmation dialog before deleting
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa quán ăn này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        boolean ok = db.deleteRestaurant(restaurantId);
                        Toast.makeText(this, ok ? "Đã xóa" : "Lỗi xóa", Toast.LENGTH_SHORT).show();
                        if (ok) {
                            // Khi xóa thành công, quay về HomeActivity vì sản phẩm đã không còn tồn tại
                            Intent intent = new Intent(this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void loadRestaurant() {
        restaurant = db.getRestaurantById(restaurantId);
        if (restaurant == null) {
            Toast.makeText(this, "Không tìm thấy quán ăn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fill form with restaurant data
        etName.setText(restaurant.getName());
        etAddress.setText(restaurant.getAddress());
        etPhone.setText(restaurant.getPhone());
        etNotes.setText(restaurant.getNotes());
        swFav.setChecked(restaurant.isFavorite());

        // Load existing image if available
        if (restaurant.getImageUri() != null && !restaurant.getImageUri().isEmpty()) {
            try {
                Uri uri = Uri.parse(restaurant.getImageUri());
                imgPreview.setImageURI(uri);
                imageUri = uri; // Keep reference to current image
            } catch (Exception e) {
                // If image loading fails, keep default image
                e.printStackTrace();
            }
        }
    }
}