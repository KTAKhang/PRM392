package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.Restaurant;

public class RestaurantDetailActivity extends AppCompatActivity {
    private TextView tvName, tvAddress, tvPhone, tvNotes;
    private ImageView ivRestaurantImage;
    private Button btnEdit;
    private DatabaseHelper db;
    private int restaurantId;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_restaurant_detail);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db = new DatabaseHelper(this);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvNotes = findViewById(R.id.tvNotes);
        ivRestaurantImage = findViewById(R.id.ivRestaurantImage);
        btnEdit = findViewById(R.id.btnEdit);

        restaurantId = getIntent().getIntExtra("restaurant_id", -1);
        if (restaurantId == -1) {
            finish();
            return;
        }

        load();

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, EditRestaurantActivity.class);
            i.putExtra("restaurant_id", restaurantId);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        Restaurant restaurant = db.getRestaurantById(restaurantId);
        if (restaurant == null) {
            // Nếu sản phẩm không tồn tại (đã bị xóa), quay về HomeActivity
            Toast.makeText(this, "Quán ăn không còn tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tvName.setText(restaurant.getName());
        tvAddress.setText(restaurant.getAddress() != null && !restaurant.getAddress().isEmpty() ? restaurant.getAddress() : "-");
        tvPhone.setText(restaurant.getPhone() != null && !restaurant.getPhone().isEmpty() ? restaurant.getPhone() : "-");
        tvNotes.setText(restaurant.getNotes() != null && !restaurant.getNotes().isEmpty() ? restaurant.getNotes() : "Chưa có ghi chú");

        // Load image
        if (restaurant.getImageUri() != null && !restaurant.getImageUri().isEmpty()) {
            String imageUri = restaurant.getImageUri();
            Uri parsed = Uri.parse(imageUri);
            Object source = parsed.getScheme() != null ? parsed : imageUri;
            Glide.with(this)
                    .load(source)
                    .placeholder(R.drawable.sample_image)
                    .error(R.drawable.sample_image)
                    .into(ivRestaurantImage);
        } else {
            ivRestaurantImage.setImageResource(R.drawable.sample_image);
        }
    }
}

