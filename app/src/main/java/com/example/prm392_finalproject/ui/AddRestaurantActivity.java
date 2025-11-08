package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
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
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_add_restaurant);

        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("FoodSpots", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etNotes = findViewById(R.id.etNotes);
        swFav = findViewById(R.id.swFav);
        btnSave = findViewById(R.id.btnSave);
        imgPreview = findViewById(R.id.imgPreview);
        btnPickImage = findViewById(R.id.btnPickImage);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        try {
                            getContentResolver().takePersistableUriPermission(uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception ignored) {
                        }
                        imgPreview.setImageURI(uri);
                    }
                });

        btnPickImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Nhập tên quán ăn", Toast.LENGTH_SHORT).show();
                return;
            }

            Restaurant restaurant = new Restaurant();
            restaurant.setName(name);
            restaurant.setAddress(etAddress.getText().toString().trim());
            restaurant.setPhone(etPhone.getText().toString().trim());
            restaurant.setNotes(etNotes.getText().toString());
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
        });
    }
}
