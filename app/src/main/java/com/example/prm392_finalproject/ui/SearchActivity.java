package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

public class SearchActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private int userId;
    private RestaurantAdapter adapter;
    private RecyclerView rvRestaurants;
    private TextView tvEmpty;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("FoodSpots", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        rvRestaurants = findViewById(R.id.rvRestaurants);
        tvEmpty = findViewById(R.id.tvEmpty);
        searchView = findViewById(R.id.searchView);

        topAppBar.setNavigationOnClickListener(v -> finish());

        rvRestaurants.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new RestaurantAdapter(this, new ArrayList<>(),
                (restaurant, position) -> toggleFavorite(restaurant, position));
        rvRestaurants.setAdapter(adapter);

        bottomNav.setSelectedItemId(R.id.nav_search);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_restaurants) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_search) {
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    resetState();
                } else {
                    performSearch(newText);
                }
                return true;
            }
        });

        resetState();
    }

    private void performSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            resetState();
            return;
        }
        List<Restaurant> results = db.searchRestaurantsByKeyword(userId, keyword);
        if (results == null || results.isEmpty()) {
            showEmpty("Không tìm thấy quán ăn phù hợp");
        } else {
            adapter.updateData(results);
            rvRestaurants.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
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

    private void resetState() {
        adapter.updateData(new ArrayList<>());
        showEmpty("Nhập từ khóa để bắt đầu tìm kiếm");
    }

    private void showEmpty(String message) {
        adapter.updateData(new ArrayList<>());
        tvEmpty.setText(message);
        tvEmpty.setVisibility(View.VISIBLE);
        rvRestaurants.setVisibility(View.GONE);
    }
}
