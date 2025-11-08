package com.example.prm392_finalproject.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.Restaurant;

public class EditRestaurantActivity extends AppCompatActivity {
    private EditText etName, etAddress, etPhone, etNotes;
    private Switch swFav;
    private Button btnUpdate, btnDelete;
    private DatabaseHelper db;
    private int restaurantId;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_edit_restaurant);
        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etNotes = findViewById(R.id.etNotes);
        swFav = findViewById(R.id.swFav);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        restaurantId = getIntent().getIntExtra("restaurant_id", -1);
        if (restaurantId == -1) {
            finish();
            return;
        }

        loadRestaurant();

        btnUpdate.setOnClickListener(v -> {
            restaurant.setName(etName.getText().toString().trim());
            restaurant.setAddress(etAddress.getText().toString().trim());
            restaurant.setPhone(etPhone.getText().toString().trim());
            restaurant.setNotes(etNotes.getText().toString());
            restaurant.setFavorite(swFav.isChecked());
            boolean ok = db.updateRestaurant(restaurant);
            Toast.makeText(this, ok ? "Cập nhật thành công" : "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
            if (ok) {
                finish();
            }
        });

        btnDelete.setOnClickListener(v -> {
            boolean ok = db.deleteRestaurant(restaurantId);
            Toast.makeText(this, ok ? "Đã xóa" : "Lỗi xóa", Toast.LENGTH_SHORT).show();
            if (ok) {
                finish();
            }
        });
    }

    private void loadRestaurant() {
        restaurant = db.getRestaurantById(restaurantId);
        if (restaurant == null) {
            return;
        }
        etName.setText(restaurant.getName());
        etAddress.setText(restaurant.getAddress());
        etPhone.setText(restaurant.getPhone());
        etNotes.setText(restaurant.getNotes());
        swFav.setChecked(restaurant.isFavorite());
    }
}

