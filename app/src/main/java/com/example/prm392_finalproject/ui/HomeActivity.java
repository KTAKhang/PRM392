package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvRestaurants;
    private RestaurantAdapter adapter;
    private DatabaseHelper db;
    private int userId;
    private List<Restaurant> allRestaurants = new ArrayList<>();
    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("FoodSpots", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvRestaurants = findViewById(R.id.rvRestaurants);
        topAppBar = findViewById(R.id.topAppBar);
        bottomNav = findViewById(R.id.bottomNav);

        rvRestaurants.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new RestaurantAdapter(this, new ArrayList<>(), (restaurant, position) -> toggleFavorite(restaurant, position));
        rvRestaurants.setAdapter(adapter);

        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.btnAdd) {
                startActivity(new Intent(this, AddRestaurantActivity.class));
                return true;
            } else if (item.getItemId() == R.id.btnFavorite) {
                startActivity(new Intent(this, FavoriteActivity.class));
                return true;
            } else if (item.getItemId() == R.id.btnList) {
                Toast.makeText(this, "Danh sách quán ăn", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        bottomNav.setSelectedItemId(R.id.nav_restaurants);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_restaurants) {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRestaurants();
    }

    private void loadRestaurants() {
        allRestaurants = db.getRestaurantsByUser(userId);
        adapter.updateData(allRestaurants);
    }

    private void toggleFavorite(Restaurant restaurant, int position) {
        boolean newState = !restaurant.isFavorite();
        boolean success = db.updateFavoriteStatus(restaurant.getId(), newState);
        if (success) {
            restaurant.setFavorite(newState);
            adapter.notifyItemChanged(position);
            Toast.makeText(this, newState ? "Đã thêm vào yêu thích" : "Đã bỏ khỏi yêu thích", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, "Không thể cập nhật yêu thích", Toast.LENGTH_SHORT).show();
        }
    }
}
