package com.example.prm392_finalproject.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private int userId;
    private RestaurantAdapter adapter;
    private RecyclerView rvRestaurants;
    private LinearLayout tvEmpty;
    private SearchView searchView;
    private ChipGroup chipGroup;
    private Chip chipAll, chipName, chipAddress;
    private TextView tvResultsCount;
    
    // Loại tìm kiếm: 0 = Tất cả, 1 = Theo tên, 2 = Theo địa chỉ
    private int searchType = 0;

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
        chipGroup = findViewById(R.id.chipGroup);
        chipAll = findViewById(R.id.chipAll);
        chipName = findViewById(R.id.chipName);
        chipAddress = findViewById(R.id.chipAddress);
        tvResultsCount = findViewById(R.id.tvResultsCount);

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

        // Setup chip group listener
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipAll) {
                searchType = 0;
            } else if (checkedId == R.id.chipName) {
                searchType = 1;
            } else if (checkedId == R.id.chipAddress) {
                searchType = 2;
            }
            // Thực hiện lại tìm kiếm với loại mới nếu đã có từ khóa
            String currentQuery = searchView.getQuery().toString();
            if (!TextUtils.isEmpty(currentQuery)) {
                performSearch(currentQuery);
            }
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
        
        List<Restaurant> results;
        String searchTypeText = "";
        
        // Chọn phương thức tìm kiếm dựa trên searchType
        switch (searchType) {
            case 1: // Theo tên
                results = db.searchRestaurantsByName(userId, keyword);
                searchTypeText = "theo tên";
                break;
            case 2: // Theo địa chỉ
                results = db.searchRestaurantsByAddress(userId, keyword);
                searchTypeText = "theo địa chỉ";
                break;
            default: // Tất cả (0) - tìm theo cả tên và địa chỉ
                results = db.searchRestaurantsByNameAndAddress(userId, keyword);
                searchTypeText = "tất cả";
                break;
        }
        
        if (results == null || results.isEmpty()) {
            String message = "Không tìm thấy quán ăn " + searchTypeText + " với từ khóa \"" + keyword + "\"";
            showEmpty(message);
            tvResultsCount.setVisibility(View.GONE);
        } else {
            adapter.updateData(results);
            rvRestaurants.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            
            // Hiển thị số lượng kết quả
            String countText = "Tìm thấy " + results.size() + " quán ăn " + searchTypeText;
            if (searchType == 0) {
                countText = "Tìm thấy " + results.size() + " quán ăn";
            }
            tvResultsCount.setText(countText);
            tvResultsCount.setVisibility(View.VISIBLE);
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
        tvResultsCount.setVisibility(View.GONE);
    }

    private void showEmpty(String message) {
        adapter.updateData(new ArrayList<>());
        // Update text in the TextView of LinearLayout (second child)
        if (tvEmpty.getChildCount() > 1) {
            View child = tvEmpty.getChildAt(1);
            if (child instanceof TextView) {
                ((TextView) child).setText(message);
            }
        }
        tvEmpty.setVisibility(View.VISIBLE);
        rvRestaurants.setVisibility(View.GONE);
    }
}
