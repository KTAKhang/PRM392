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
import com.example.prm392_finalproject.adapter.BookAdapter;
import com.example.prm392_finalproject.database.DatabaseHelper;
import com.example.prm392_finalproject.model.Book;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView rvBooks;
    private BookAdapter adapter;
    private DatabaseHelper db;
    private int userId;
    private List<Book> allFavoriteBooks = new ArrayList<>();
    private TextView tvEmpty;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("MyLibrary", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvBooks = findViewById(R.id.rvBooks);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        SearchView searchView = findViewById(R.id.searchView);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvBooks.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new BookAdapter(this, new ArrayList<>(), (book, position) -> toggleFavorite(book));
        rvBooks.setAdapter(adapter);

        topAppBar.setNavigationOnClickListener(v -> finish());
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.btnAdd) {
                startActivity(new Intent(this, AddBookActivity.class));
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
            if (itemId == R.id.nav_books) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_search) {
                Toast.makeText(this, "Chức năng tìm kiếm", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(this, AddBookActivity.class));
                return true;
            } else if (itemId == R.id.nav_favorite) {
                return true;
            } else if (itemId == R.id.nav_user) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    applyFilter(newText);
                    return true;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteBooks();
    }

    private void loadFavoriteBooks() {
        allFavoriteBooks = db.getFavoriteBooksByUser(userId);
        applyFilter(currentQuery);
    }

    private void applyFilter(String query) {
        currentQuery = query != null ? query : "";
        List<Book> display;
        if (TextUtils.isEmpty(currentQuery)) {
            display = allFavoriteBooks;
        } else {
            List<Book> filtered = new ArrayList<>();
            for (Book b : allFavoriteBooks) {
                if ((b.getTitle() != null && b.getTitle().toLowerCase().contains(currentQuery.toLowerCase()))
                        || (b.getAuthor() != null
                                && b.getAuthor().toLowerCase().contains(currentQuery.toLowerCase()))) {
                    filtered.add(b);
                }
            }
            display = filtered;
        }
        adapter.updateData(display);
        updateEmptyState(display.isEmpty());
    }

    private void toggleFavorite(Book book) {
        boolean newState = !book.isFavorite();
        boolean success = db.updateFavoriteStatus(book.getId(), newState);
        if (success) {
            book.setFavorite(newState);
            if (!newState) {
                allFavoriteBooks.remove(book);
                applyFilter(currentQuery);
                Toast.makeText(this, "Đã bỏ khỏi yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                applyFilter(currentQuery);
                Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không thể cập nhật yêu thích", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvBooks.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvBooks.setVisibility(View.VISIBLE);
        }
    }
}
