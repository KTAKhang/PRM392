package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_finalproject.R;
import com.example.prm392_finalproject.adapter.RestaurantAdapter;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.Restaurant;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView rvRestaurants;
    private RestaurantAdapter adapter;
    private DatabaseHelper db;
    private int userId;
    private List<Restaurant> allFavoriteRestaurants = new ArrayList<>();
    private LinearLayout tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("FoodSpots", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvRestaurants = findViewById(R.id.rvRestaurants);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvRestaurants.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new RestaurantAdapter(this, new ArrayList<>(), (restaurant, position) -> toggleFavorite(restaurant));
        rvRestaurants.setAdapter(adapter);

        topAppBar.setNavigationOnClickListener(v -> finish());
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.btnAdd) {
                startActivity(new Intent(this, AddRestaurantActivity.class));
                return true;
            } else if (item.getItemId() == R.id.btnFavorite) {
                return true;
            } else if (item.getItemId() == R.id.btnList) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            }
            return false;
        });

        bottomNav.setSelectedItemId(R.id.nav_favorite);
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
                return true;
            } else if (itemId == R.id.nav_user) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteRestaurants();
    }

    private void loadFavoriteRestaurants() {
        allFavoriteRestaurants = db.getFavoriteRestaurantsByUser(userId);
        adapter.updateData(allFavoriteRestaurants);
        updateEmptyState(allFavoriteRestaurants.isEmpty());
    }

    private void toggleFavorite(Restaurant restaurant) {
        boolean newState = !restaurant.isFavorite();
        boolean success = db.updateFavoriteStatus(restaurant.getId(), newState);
        if (success) {
            restaurant.setFavorite(newState);
            if (!newState) {
                allFavoriteRestaurants.remove(restaurant);
                adapter.updateData(allFavoriteRestaurants);
                updateEmptyState(allFavoriteRestaurants.isEmpty());
                Toast.makeText(this, "Đã bỏ khỏi yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                adapter.updateData(allFavoriteRestaurants);
                updateEmptyState(allFavoriteRestaurants.isEmpty());
                Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không thể cập nhật yêu thích", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvRestaurants.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvRestaurants.setVisibility(View.VISIBLE);
        }
    }
}
