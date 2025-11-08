package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.Restaurant;

public class RestaurantDetailActivity extends AppCompatActivity {
    private TextView tvName, tvAddress, tvPhone, tvNotes;
    private Button btnEdit;
    private DatabaseHelper db;
    private int restaurantId;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_restaurant_detail);

        db = new DatabaseHelper(this);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvNotes = findViewById(R.id.tvNotes);
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
            return;
        }
        tvName.setText(restaurant.getName());
        tvAddress.setText(restaurant.getAddress());
        tvPhone.setText(restaurant.getPhone());
        tvNotes.setText(restaurant.getNotes());
    }
}

